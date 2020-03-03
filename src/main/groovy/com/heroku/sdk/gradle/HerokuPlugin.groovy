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

        project.tasks.create('deployHeroku', DeployHerokuTask.class, ext)
    }
}
