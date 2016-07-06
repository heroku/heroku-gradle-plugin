package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import org.gradle.api.internal.artifacts.mvnsettings.DefaultLocalMavenRepositoryLocator
import org.gradle.api.internal.artifacts.mvnsettings.DefaultMavenFileLocations
import org.gradle.api.internal.artifacts.mvnsettings.DefaultMavenSettingsProvider
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.GradleRunner

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class HerokuDeployTaskSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir
    File buildFile
    List<File> pluginClasspath

    GradleRunner with(String... tasks) {
        GradleRunner.create()
                .withPluginClasspath(pluginClasspath)
                .withProjectDir(projectDir)
                .withArguments(tasks)
    }

    String exec(String task) {
        StringBuffer sout = new StringBuffer(), serr = new StringBuffer()
        Process proc = task.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        return sout.toString()
    }

    boolean execCond(String task) {
        StringBuffer sout = new StringBuffer(), serr = new StringBuffer()
        Process proc = task.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        return proc.exitValue() == 0
    }

    def setup() {
        projectDir = temporaryFolder.root
        buildFile = temporaryFolder.newFile('build.gradle')

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { new File(it) }
    }

    def 'fail when missing app name'() {
        given:
            buildFile << '''
                plugins {
                    id 'com.heroku.sdk.heroku-gradle'
                }
            '''.stripIndent()

        when:
            BuildResult buildResult = with('deployHeroku').buildAndFail()

        then:
            buildResult.output.contains("app name is required")
    }
}
