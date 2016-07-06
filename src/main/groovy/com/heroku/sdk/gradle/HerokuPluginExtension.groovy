package com.heroku.sdk.gradle

import org.gradle.api.Project

import com.google.common.base.Preconditions
import com.google.common.base.Strings

class HerokuPluginExtension {
    Project project

    private String appName = null

    public HerokuPluginExtension(Project project) {
        this.project = project
    }

    public void setAppName(String appName) {
        this.appName = appName
    }

    public String getAppName() {
        return appName
    }

    public void resolvePathsAndValidate() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "appName is required.")
    }
}