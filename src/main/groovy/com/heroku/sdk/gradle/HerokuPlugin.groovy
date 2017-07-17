package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.apache.http.client.HttpResponseException
import org.gradle.api.InvalidUserDataException

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
              project.heroku.appName,
              project.rootDir,
              project.buildDir,
              project.heroku.buildpacks,
              project.logger)

            try {
              app.deploy(
                files,
                project.heroku.configVars,
                (String) (project.heroku.jdkUrl == null ? project.heroku.jdkVersion : project.heroku.jdkUrl),
                project.heroku.stack,
                project.heroku.processTypes,
                project.heroku.slugFilename)
            } catch (HttpResponseException e) {
              if (e.getStatusCode() == 404) {
                throw new InvalidUserDataException("Could not find app: " + project.heroku.appName, e)
              } else {
                throw e;
              }
            }
        }
    }
}
