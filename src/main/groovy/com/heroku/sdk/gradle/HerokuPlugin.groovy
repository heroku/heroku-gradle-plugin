package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class HerokuPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        HerokuPluginExtension ext = project.extensions.create('heroku', HerokuPluginExtension, project)

        project.task('deployHeroku') {
            println "Deploying to heroku"
        }

        project.afterEvaluate {
            ext.resolvePathsAndValidate()
        }

    }
}