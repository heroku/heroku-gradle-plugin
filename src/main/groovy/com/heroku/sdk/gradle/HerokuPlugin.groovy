package com.heroku.sdk.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class HerokuPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        HerokuPluginExtension ext = project.extensions.create('heroku', HerokuPluginExtension)

        project.afterEvaluate {
            ext.resolvePathsAndValidate()
        }

        project.task('deployHeroku').doLast {
            List<File> files = ext.getIncludedFiles(project.rootDir)
            if (project.heroku.includeBuildDir) files << project.buildDir

            GradleApp app = new GradleApp(
                    (String) project.heroku.appName,
                    (File) project.rootDir,
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
}
