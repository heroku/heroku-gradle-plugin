package com.heroku.sdk.gradle

import com.heroku.sdk.deploy.App
import org.slf4j.Logger

import java.io.File;
import java.util.List;

class GradleApp extends App {

    Logger log

    GradleApp(String appName, File rootDir, File targetDir, Logger logger) {
        this(rootDir, targetDir, new ArrayList<String>(), logger);
    }

    GradleApp(String appName, File rootDir, File targetDir, List<String> buildpacks, Logger logger) {
        super("heroku-gradle", appName, rootDir, targetDir, buildpacks);
        this.log = logger
    }

    @Override
    void logInfo(String message) {
        log.quiet(message);
    }

    @Override
    void logDebug(String message) {
        log.debug(message);
    }

    @Override
    void logWarn(String message) {
        log.warn(message);
    }
}
