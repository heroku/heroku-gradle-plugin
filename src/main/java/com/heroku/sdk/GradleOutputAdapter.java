package com.heroku.sdk;

import com.heroku.sdk.deploy.lib.OutputAdapter;

public class GradleOutputAdapter implements OutputAdapter {
    @Override
    public void logInfo(String message) {
        System.out.println(message);
    }

    @Override
    public void logDebug(String message) {
        System.out.println(message);
    }

    @Override
    public void logWarn(String message) {
        System.out.println(message);
    }

    @Override
    public void logError(String message) {
        System.out.println(message);
    }

    @Override
    public void logUploadProgress(long uploaded, long contentLength) {
        System.out.printf("Uploaded: %d...\n", uploaded);
    }
}
