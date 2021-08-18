# Heroku Gradle Plugin
[![](https://badgen.net/github/license/heroku/heroku-gradle)](LICENSE)
[![](https://badgen.net/circleci/github/heroku/heroku-gradle/main)](https://circleci.com/gh/heroku/heroku-gradle/tree/main)

This plugin is used to deploy Gradle based JVM applications directly to Heroku without pushing to a Git repository. This can be useful when deploying from a CI server.

## Using the Plugin

Add the plugin to your `build.gradle`:

```
plugins {
  id "com.heroku.sdk.heroku-gradle" version "2.0.0"
}
```

Create a Heroku app using the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli):

```
$ heroku create
```

Build your application, and run the `deployHeroku` task:

```
$ ./gradlew build deployHeroku
```

## Configuration

You can configure the heroku-gradle plugin by using the `heroku` directive in
your `build.gradle` configuration.

You can configure your Heroku app name like this:

```
heroku {
  appName = "sushi"
}
```

You can include extra files like this:

```
heroku {
  includes = ["README.md"]
}
```

You can exclude all files except your fat-jar like this:

```
heroku {
  includes = ["build/libs/my-app.jar"]
  includeBuildDir = false
}
```

You can strip the path from the file paths
(e.g. turning "foo/bar/build/foobar.tgz" into "foobar.tgz" ).
This will default to project.rootDir (i.e. the root of all modules)
```
heroku {
  includeRootDir = project.buildDir
}
```

You can explicitly define the required jdk version (in system.properties)

```
heroku {
  jdkVersion = 11
}
```

You can customize the command used to run your app (in Procfile) like this:

```
heroku {
  processTypes(
      web: "java -jar build/libs/my-app.jar"
  )
}
```

## Development

The heavy lifting for this plugin is done by the heroku-deploy library. The source code for that project can be found in the [heroku-maven-plugin repository](https://github.com/heroku/heroku-maven-plugin/tree/main/heroku-deploy). If you need to update that library, do this:

```
$ git clone https://github.com/heroku/heroku-maven-plugin
$ cd heroku-maven-plugin/heroku-deploy
# make your changes
$ mvn clean install
```

Then update the heroku-deploy dependency version in the heroku-gradle `build.gradle` to 0.1.0 (or whatever version is specified in the heroku-deploy `pom.xml`). The next time you run the scripted tests it will pick up the snapshot version from your local Maven repository.
