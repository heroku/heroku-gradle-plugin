package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class HerokuPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        HerokuPluginExtension ext = project.extensions.create('heroku', HerokuPluginExtension)

        project.afterEvaluate {
            ext.resolvePathsAndValidate()
        }

        project.task('deployHeroku') << {
            GradleApp app = new GradleApp(
              project.heroku.appName,
              project.rootDir,
              project.buildDir,
              project.heroku.buildpacks,
              project.logger)
            app.deploy(
              ext.getIncludedFiles(project.rootDir),
              project.heroku.configVars,
              (String) (project.heroku.jdkUrl == null ? project.heroku.jdkVersion : project.heroku.jdkUrl),
              project.heroku.stack,
              project.heroku.processTypes,
              project.heroku.slugFilename)
        }
    }
}
