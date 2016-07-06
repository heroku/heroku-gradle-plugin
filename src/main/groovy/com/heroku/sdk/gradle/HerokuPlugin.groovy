package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class HerokuPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('heroku', HerokuPluginExtension)
        project.task('deployHeroku', type: HerokuDeployTask)
    }
}