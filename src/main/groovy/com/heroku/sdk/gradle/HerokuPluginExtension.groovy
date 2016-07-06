package com.heroku.sdk.gradle

import org.gradle.api.Project

import com.google.common.base.Preconditions
import com.google.common.base.Strings

class HerokuPluginExtension {

    String appName

    public void resolvePathsAndValidate() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "appName is required.")
    }
}