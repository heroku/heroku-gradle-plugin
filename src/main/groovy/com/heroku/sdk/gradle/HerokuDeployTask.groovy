package com.heroku.sdk.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.file.FileCollection
import org.gradle.api.InvalidUserDataException

class HerokuDeployTask extends DefaultTask {

    Map<String, String> options = [:]
    Map<String, String> parameters

    @SuppressWarnings('ConfusingMethodName')
    void parameters(Map<String, String> parameters) {
        this.parameters = parameters
    }

    HerokuDeployTask() {
        ExpandoMetaClass mc = new ExpandoMetaClass(HerokuDeployTask, false, true)
        mc.initialize()
        this.metaClass = mc
    }

    // If the user calls a missing method, assume they're trying to set an
    // option. Set the option indicated by the method name to have the value
    // indicated by the first argument to the method. Other arguments are
    // ignored.
    //
    // This probably isn't very smart (or fast), but it was a quick and easy
    // way to add support for all of Saxon's command-line arguments while
    // having a nice API.
    @SuppressWarnings('UnusedPrivateMethod')
    private Boolean methodMissing(String name, arguments) {
        Object argument = arguments[0]

        Closure cachedMethod = {
            this.options[name] = argument
        }

        this.metaClass[name] = cachedMethod
        cachedMethod(argument)
    }

    @TaskAction
    void run() {
        println "Deploying to heroku..."
    }
}