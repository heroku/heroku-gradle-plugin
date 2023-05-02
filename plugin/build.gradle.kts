project.group = "com.heroku.sdk"
project.version = "3.0.0"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.heroku.sdk:heroku-deploy:3.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation(gradleTestKit())
    testImplementation("org.buildobjects:jproc:2.8.2")
    testImplementation("com.google.guava:guava:31.1-jre")
}

gradlePlugin {
    website.set("https://github.com/heroku/heroku-gradle")
    vcsUrl.set("https://github.com/heroku/heroku-gradle.git")

    plugins {
        create("heroku-gradle") {
            id = "com.heroku.sdk.heroku-gradle"
            implementationClass = "com.heroku.sdk.HerokuGradlePlugin"
            displayName = "Heroku Gradle Plugin"
            description = "A Gradle plugin for deploying to Heroku."
            tags.set(listOf("heroku", "deployment"))
        }
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

tasks.named<JavaCompile>(functionalTestSourceSet.getCompileTaskName("java")) {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()

    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
