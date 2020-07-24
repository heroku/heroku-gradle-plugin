package com.heroku.sdk;

import com.heroku.sdk.deploy.lib.OutputAdapter;
import com.heroku.sdk.deploy.lib.deploymemt.Deployer;
import com.heroku.sdk.deploy.lib.deploymemt.DeploymentDescriptor;
import com.heroku.sdk.deploy.lib.resolver.ApiKeyResolver;
import com.heroku.sdk.deploy.lib.resolver.AppNameResolver;
import com.heroku.sdk.deploy.lib.sourceblob.JvmProjectSourceBlobCreator;
import com.heroku.sdk.deploy.lib.sourceblob.SourceBlobDescriptor;
import com.heroku.sdk.deploy.lib.sourceblob.SourceBlobPackager;
import com.heroku.sdk.deploy.util.Procfile;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HerokuGradlePlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getExtensions().add("heroku", HerokuExtension.class);

        project.getTasks().register("deployHeroku", task -> {
            task.setDescription("Deploys JVM web-application directly to Heroku without pushing to a Git repository.");
            task.setGroup("Heroku");

            HerokuExtension herokuExtension = project.getExtensions().getByType(HerokuExtension.class);

            List<Path> includedPaths = herokuExtension
                    .getIncludes()
                    .stream()
                    .map(Paths::get)
                    .collect(Collectors.toList());

            if (herokuExtension.isIncludeBuildDir()) {
                includedPaths.add(Paths.get("build/"));
            }

            task.doLast(s -> {
                Path projectRootDirPath = project.getRootDir().toPath();

                try {
                    OutputAdapter outputAdapter = new GradleOutputAdapter(project.getLogger());

                    Supplier<Optional<String>> customJdkResolver =
                            () -> Optional.ofNullable(herokuExtension.getJdkVersion());

                    Supplier<Procfile> customProcfileResolver =
                            () -> new Procfile(herokuExtension.getProcessTypes());

                    Supplier<Optional<String>> customAppNameResolver =
                            () -> Optional.ofNullable(herokuExtension.getAppName());

                    SourceBlobDescriptor sourceBlobDescriptor = JvmProjectSourceBlobCreator.create(
                            projectRootDirPath,
                            "heroku-gradle",
                            includedPaths,
                            customProcfileResolver,
                            Procfile.empty(),
                            customJdkResolver,
                            outputAdapter);

                    Path sourceBlobPath = SourceBlobPackager.pack(sourceBlobDescriptor, outputAdapter);

                    String appName = AppNameResolver
                            .resolve(projectRootDirPath, customAppNameResolver)
                            .orElseThrow(() -> new IllegalArgumentException("Could not resolve app name!"));

                    String apiKey = ApiKeyResolver
                            .resolve(projectRootDirPath)
                            .orElseThrow(() -> new IllegalArgumentException("Could not resolve API key."));

                    String deployedVersionString = project.getVersion().toString();

                    DeploymentDescriptor deploymentDescriptor
                            = new DeploymentDescriptor(appName, herokuExtension.getBuildpacks(), herokuExtension.getConfigVars(), sourceBlobPath, deployedVersionString);

                    boolean deployResult = Deployer.deploy(apiKey, "heroku-gradle", getPluginVersion(), deploymentDescriptor, outputAdapter);

                    if (!deployResult) {
                        // heroku-deploy reports errors directly to the user using the OutputAdapter. We have to throw
                        // this exception here to mark this task as failed.
                        throw new TaskExecutionException(task, new RuntimeException("Deployment failed."));
                    }
                } catch (IOException | InterruptedException e) {
                    throw new TaskExecutionException(task, e);
                }
            });
        });
    }

    private static String getPluginVersion() throws IOException {
        String filename = "heroku-gradle.properties";
        InputStream propertiesInputStream = HerokuGradlePlugin.class.getClassLoader().getResourceAsStream(filename);

        if (propertiesInputStream != null) {
            Properties properties = new Properties();
            properties.load(propertiesInputStream);
            return properties.getProperty("version");
        }

        return "unknown";
    }
}
