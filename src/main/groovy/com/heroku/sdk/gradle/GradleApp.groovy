package com.heroku.sdk.gradle

import com.heroku.sdk.deploy.App

class GradleApp extends App {
    @Override
    void logInfo(String message) {
        println message
    }
}
