package com.heroku.sdk;

import com.heroku.sdk.deploy.lib.OutputAdapter;
import org.gradle.api.logging.Logger;

public class GradleOutputAdapter implements OutputAdapter {
    private Logger logger;

    public GradleOutputAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void logInfo(String message) {
        // By default, Gradle does not show INFO log messages to the user. heroku-deploy assumes that the INFO log
        // level is visible to the user and outputs deployment status information at that log level. This translates
        // to Gradle's LIFECYCLE log level and is therefore used here instead.
        logger.lifecycle(message);
    }

    @Override
    public void logDebug(String message) {
        logger.debug(message);
    }

    @Override
    public void logWarn(String message) {
        logger.warn(message);
    }

    @Override
    public void logError(String message) {
        logger.error(message);
    }

    @Override
    public void logUploadProgress(long uploaded, long contentLength) {
        logger.debug("Upload progress: {} bytes out of {}", uploaded, contentLength);
    }
}
