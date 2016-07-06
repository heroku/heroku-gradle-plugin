package com.heroku.sdk.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import spock.lang.*

import java.nio.file.Files
import java.nio.file.Paths

class HerokuDeployTaskSpec extends Specification {

    final String HEROKU = 'heroku'

    Project project

    void setup() {
        project = ProjectBuilder.builder().withName(HEROKU).build()
        project.configurations.create(HEROKU)
    }

    def 'Giving String as input'() {
        when:
            Task task = project.tasks.create(name: HEROKU, type: HerokuDeployTask) {
                input "foobar"
            }

        then:
            task.options.input == "foobar"
    }

}
