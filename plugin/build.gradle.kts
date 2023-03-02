project.group = "com.heroku.sdk"
project.version = "3.0.0"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.heroku.sdk:heroku-deploy:3.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
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

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
