package com.heroku.sdk;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.gradle.internal.impldep.com.google.common.io.Files;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HerokuPluginTest {
    @TempDir
    private File temporaryDirectory;
    private File buildFile;
    private String herokuAppName;

    @BeforeEach
    public void setup() throws IOException {
        File buildDirectory = new File(temporaryDirectory, "build");
        if (!buildDirectory.mkdirs()) {
            throw new IllegalStateException("Could not create build directory!");
        };

        buildFile = new File(temporaryDirectory, "build.gradle");
        herokuAppName = "heroku-gradle-" + UUID.randomUUID().toString().substring(0, 10);

        System.out.println(new File("fixtures/sample-jar.jar").getAbsolutePath());

        Files.copy(new File("fixtures/sample-jar.jar"), new File(buildDirectory, "sample-jar.jar"));

        new ProcBuilder("heroku")
                .withArgs("create", herokuAppName)
                .withOutputStream(System.out)
                .withErrorStream(System.err)
                .run();
    }

    @AfterEach
    public void cleanup() {
        new ProcBuilder("heroku")
                .withArgs("destroy", herokuAppName, "--confirm", herokuAppName)
                .withOutputStream(System.out)
                .withErrorStream(System.err)
                .run();
    }

    @Test
    public void testHappyPath() throws InterruptedException, URISyntaxException, IOException {
        writeBuildFile(
            """
            plugins {
              id 'com.heroku.sdk.heroku-gradle'
            }
            heroku {
               appName '%s'
               processTypes(
                   web: "java -jar build/sample-jar.jar"
               )
            }
            """.formatted(herokuAppName)
        );

        BuildResult buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(temporaryDirectory)
                .withArguments("deployHeroku")
                .forwardOutput()
                .build();

        TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":deployHeroku")).getOutcome();
        assertEquals(taskOutcome, TaskOutcome.SUCCESS);

        assertTrue(buildResult.getOutput().contains(herokuAppName));
        assertTrue(buildResult.getOutput().contains("including: build/"));
        assertTrue(buildResult.getOutput().contains("- success"));
        assertTrue(buildResult.getOutput().contains("Installing OpenJDK 1.8"));
        assertTrue(buildResult.getOutput().contains("Done"));

        // Give the platform some time to boot the deployed application
        Thread.sleep(5000);

        HttpRequest request = HttpRequest.newBuilder(new URI("https://" + herokuAppName + ".herokuapp.com/")).build();
        HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.statusCode(), 200);
        assertTrue(response.body().contains("Hello from Java!"));
    }

    @Test
    public void testWithProcfile() throws InterruptedException, URISyntaxException, IOException {
        writeBuildFile(
            """
            plugins {
              id 'com.heroku.sdk.heroku-gradle'
            }
            heroku {
               appName '%s'
            }
            """.formatted(herokuAppName)
        );

        File procfile = new File(temporaryDirectory, "Procfile");
        Files.asCharSink(procfile, StandardCharsets.UTF_8).write("web: java -jar build/sample-jar.jar");

        BuildResult buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(temporaryDirectory)
                .withArguments("deployHeroku")
                .forwardOutput()
                .build();

        TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":deployHeroku")).getOutcome();
        assertEquals(taskOutcome, TaskOutcome.SUCCESS);

        assertTrue(buildResult.getOutput().contains(herokuAppName));
        assertTrue(buildResult.getOutput().contains("including: build/"));
        assertTrue(buildResult.getOutput().contains("- success"));
        assertTrue(buildResult.getOutput().contains("Installing OpenJDK 1.8"));
        assertTrue(buildResult.getOutput().contains("Done"));

        // Give the platform some time to boot the deployed application
        Thread.sleep(5000);

        HttpRequest request = HttpRequest.newBuilder(new URI("https://" + herokuAppName + ".herokuapp.com/")).build();
        HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.statusCode(), 200);
        assertTrue(response.body().contains("Hello from Java!"));
    }

    @Test
    public void testWithExtraConfig() throws IOException {
        writeBuildFile(
            """
                plugins {
                    id 'com.heroku.sdk.heroku-gradle'
                }
                heroku {
                    appName '%s'
                    includes = ['README.md']
                }
            """.formatted(herokuAppName)
        );

        File procfile = new File(temporaryDirectory, "README.md");
        Files.asCharSink(procfile, StandardCharsets.UTF_8).write("Hello World!");

        BuildResult buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(temporaryDirectory)
                .withArguments("deployHeroku")
                .forwardOutput()
                .build();

        TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":deployHeroku")).getOutcome();
        assertEquals(taskOutcome, TaskOutcome.SUCCESS);

        assertTrue(buildResult.getOutput().contains(herokuAppName));
        assertTrue(buildResult.getOutput().contains("including: build/"));
        assertTrue(buildResult.getOutput().contains("including: README.md"));
        assertTrue(buildResult.getOutput().contains("- success"));
        assertTrue(buildResult.getOutput().contains("Installing OpenJDK 1.8"));
        assertTrue(buildResult.getOutput().contains("Done"));

        ProcResult result = new ProcBuilder("heroku")
                .withArgs("run", "ls", "-a", herokuAppName)
                .withTimeoutMillis(10000)
                .withErrorStream(System.err)
                .run();

        assertTrue(result.getOutputString().contains("README.md"));
    }

    @Test
    public void testMissingAppName() throws IOException {
        writeBuildFile(
            """
            plugins {
                id 'com.heroku.sdk.heroku-gradle'
            }
            """
        );

        BuildResult buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(temporaryDirectory)
                .withArguments("deployHeroku")
                .forwardOutput()
                .buildAndFail();

        TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":deployHeroku")).getOutcome();
        assertEquals(taskOutcome, TaskOutcome.FAILED);
        assertTrue(buildResult.getOutput().contains("Could not resolve app name!"));
    }

    @Test
    public void testInvalidAppName() throws IOException {
        writeBuildFile(
            """
                plugins {
                    id 'com.heroku.sdk.heroku-gradle'
                }
                heroku {
                    appName '87y9sadsf8dy7hfff32j'
                }
                """
        );

        BuildResult buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(temporaryDirectory)
                .withArguments("deployHeroku")
                .forwardOutput()
                .buildAndFail();

        TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":deployHeroku")).getOutcome();
        assertEquals(taskOutcome, TaskOutcome.FAILED);
        assertTrue(buildResult.getOutput().contains("Could not find application! Make sure you configured your application name correctly."));
    }

    private void writeBuildFile(String contents) throws IOException {
        Files.asCharSink(buildFile, StandardCharsets.UTF_8).write(contents);
    }
}
