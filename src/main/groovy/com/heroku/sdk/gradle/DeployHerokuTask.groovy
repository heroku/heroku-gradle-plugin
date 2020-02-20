package com.heroku.sdk.gradle

import javax.inject.Inject

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle Task that deploys Gradle based JVM applications directly to Heroku
 * without pushing to a Git repository.
 */
class DeployHerokuTask extends DefaultTask {

    /**
     * The heroku {} block in the build script.
     */
    HerokuPluginExtension ext;

    @Inject
    DeployHerokuTask(HerokuPluginExtension ext) {
        description = "Deploys JVM web-application directly to Heroku without pushing to a Git repository."
        group = "Heroku"
        this.ext = ext
    }

    @TaskAction
    void deploy() {
        File includeRootDir = project.heroku.includeRootDir ?: project.rootDir
        List<File> files = ext.getIncludedFiles(includeRootDir)
        if (project.heroku.includeBuildDir) files << project.buildDir

        GradleApp app = new GradleApp(
                (String) project.heroku.appName,
                includeRootDir,
                (File) project.buildDir,
                (List<String>) project.heroku.buildpacks,
                project.logger)

        app.deploy(
                (List<File>) files,
                (Map<String,String>) project.heroku.configVars,
                (String) project.heroku.jdkVersion,
                (Map<String,String>) project.heroku.processTypes,
                (String) project.heroku.slugFilename)
    }
}
