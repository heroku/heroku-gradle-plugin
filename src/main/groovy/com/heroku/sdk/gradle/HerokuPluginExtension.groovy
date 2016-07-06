package com.heroku.sdk.gradle

import org.gradle.api.Project

import com.google.common.base.Preconditions
import com.google.common.base.Strings

class HerokuPluginExtension {
    Project project

    private String name = null

    public HerokuPluginExtension(Project project) {
        this.project = project
    }

    public void setName(String name) {
        this.name = name
    }

    public String getName() {
        return name
    }

    public void resolvePathsAndValidate() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "app name is required.")
    }
}