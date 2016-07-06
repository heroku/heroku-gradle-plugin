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
            //GradleApp app = new GradleApp()
            println "Deploying to heroku: " + project.heroku.appName
        }

    }
}