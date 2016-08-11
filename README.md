# Heroku Gradle Plugin [![Build Status](https://travis-ci.org/heroku/heroku-gradle.svg?branch=master)](https://travis-ci.org/heroku/heroku-gradle) [ ![Download](https://api.bintray.com/packages/heroku/maven/gradle/images/download.svg) ](https://bintray.com/heroku/maven/gradle/_latestVersion)

This plugin is used to deploy Gradle based JVM applications directly to Heroku without pushing to a Git repository. This can be useful when deploying from a CI server.

## Using the Plugin

Add the plugin to your `build.gradle`:

```
plugins {
  id "com.heroku.sdk.heroku-gradle" version "0.1.0"
}
```

Create a Heroku app using the [Heroku CLI](https://toolbelt.heroku.com):

```
$ heroku create
```

Build your application, and run the `deployHeroku` task:

```
$ ./gradlew build deployHeroku
```

## Development

The heavy lifting for this plugin is done by the heroku-deploy library. The source code for that project can be found in the [heroku-maven-plugin repository](https://github.com/heroku/heroku-maven-plugin/tree/master/heroku-deploy). If you need to update that library, do this:

```
$ git clone https://github.com/heroku/heroku-maven-plugin
$ cd heroku-maven-plugin/heroku-deploy
# make your changes
$ mvn clean install
```

Then update the heroku-deploy dependency version in the heroku-gradle `build.gradle` to 0.1.0 (or whatever version is specified in the heroku-deploy `pom.xml`). The next time you run the scripted tests it will pick up the snapshot version from your local Maven repository.
