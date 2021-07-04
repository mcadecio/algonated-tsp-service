package com.dercio.algonated_tsp_service.verticles.runner.code;

import com.dercio.algonated_tsp_service.response.Response;
import com.dercio.algonated_tsp_service.verticles.VerticleAddresses;
import com.dercio.algonated_tsp_service.verticles.analytics.CodeRunnerSummary;
import com.dercio.algonated_tsp_service.verticles.analytics.TSPAnalyticsVerticle;
import com.dercio.algonated_tsp_service.verticles.codec.CodecRegisterVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class CodeRunnerVerticleTest {

    private static final Vertx vertx = Vertx.vertx();

    @BeforeAll
    public static void prepare(VertxTestContext testContext) {
        vertx.deployVerticle(new CodecRegisterVerticle());
        vertx.deployVerticle(new TSPAnalyticsVerticle());
        vertx.deployVerticle(
                new CodeRunnerVerticle(),
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    @DisplayName("Compiles and runs a simple request")
    void compileAndRunSimpleRequest(VertxTestContext testContext) {
        double[][] distances = {
                {1, 2, 3, 4, 5},
                {2, 1, 3, 4, 5},
                {3, 1, 2, 4, 5},
                {1, 3, 2, 4, 5},
                {2, 3, 1, 4, 5}
        };
        vertx.eventBus().<Response>request(
                VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                simpleCodeOptions(),
                messageReply -> testContext.verify(() -> {
                    Response response = messageReply.result().body();
                    assertTrue(response.isSuccess());
                    assertEquals("Compile and Run was a success", response.getConsoleOutput());
                    assertEquals(List.of(1, 1, 0, 1, 1), response.getResult());
                    assertArrayEquals(distances, response.getData());
                    assertEquals(simpleCodeSummary(), response.getSummary());
                    assertEquals(Collections.emptyList(), response.getSolutions());
                    testContext.completeNow();
                })
        );
    }

    @Test
    @DisplayName("Compiles and runs a simple request and retrieves the list of solutions" +
            "as a 2d list of integers")
    void retrievesSolutionsAs2DListOfIntegers(VertxTestContext testContext) {
        double[][] distances = {
                {1, 2, 3, 4},
                {2, 1, 3, 4},
                {3, 1, 2, 4},
                {1, 3, 2, 4}
        };
        var solutions = List.of(
                List.of(1, 1, 0, 1),
                List.of(1, 0, 0, 1),
                List.of(1, 1, 1, 1),
                List.of(0, 1, 0, 1)
        );
        vertx.eventBus().<Response>request(
                VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                typicalCodeOptions(),
                messageReply -> testContext.verify(() -> {
                    assertTrue(messageReply.succeeded());
                    Response response = messageReply.result().body();
                    assertTrue(response.isSuccess());
                    assertEquals("Compile and Run was a success", response.getConsoleOutput());
                    assertEquals(List.of(0, 1, 0, 1), response.getResult());
                    assertArrayEquals(distances, response.getData());
                    assertNotNull(response.getSummary());
                    assertEquals(solutions, response.getSolutions());
                    testContext.completeNow();
                })
        );
    }

    @Test
    @DisplayName("Request fails when there is an illegal import")
    void illegalImportFound(VertxTestContext testContext) {
        var options = simpleCodeOptions();
        options.setImportsAllowed(Collections.singletonList("import java.util.ArrayList;"));
        vertx.eventBus().<Response>request(
                VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                options,
                messageReply -> testContext.verify(() -> {
                    assertTrue(messageReply.failed());
                    assertEquals("Please remove the following imports:\n[import java.util.List;]",
                            messageReply.cause().getMessage());
                    testContext.completeNow();
                })
        );
    }

    @Test
    @DisplayName("Request fails when there is an illegal method")
    void illegalMethodFound(VertxTestContext testContext) {
        var options = simpleCodeOptions();
        options.setIllegalMethods(Collections.singletonList("System.exit(.*)"));
        options.setCode(options.getCode() + "System.exit(0);");
        vertx.eventBus().<Response>request(
                VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                options,
                messageReply -> testContext.verify(() -> {
                    assertTrue(messageReply.failed());
                    assertEquals("Please remove the following illegal methods:\n[System.exit(0);]",
                            messageReply.cause().getMessage());
                    testContext.completeNow();
                })
        );
    }

    @Test
    @DisplayName("Request fails when class name is different from classname in code")
    void differentClassNameFails(VertxTestContext testContext) {
        var options = simpleCodeOptions();
        options.setClassName("SomethingElse");
        vertx.eventBus().<Response>request(
                VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                options,
                messageReply -> testContext.verify(() -> {
                    assertTrue(messageReply.failed());
                    assertTrue(messageReply.cause()
                            .getMessage()
                            .contains("class TSPProblem is public, " +
                                    "should be declared in a file named TSPProblem.java"));
                    testContext.completeNow();
                })
        );
    }

    private CodeOptions simpleCodeOptions() {
        double[][] distances = {
                {1, 2, 3, 4, 5},
                {2, 1, 3, 4, 5},
                {3, 1, 2, 4, 5},
                {1, 3, 2, 4, 5},
                {2, 3, 1, 4, 5}
        };
        return CodeOptions.builder()
                .className("TSPProblem")
                .packageName("com.exercise")
                .methodToCall("runTSP")
                .iterations(5)
                .importsAllowed(List.of("import java.util.List;", "import java.util.ArrayList;"))
                .illegalMethods(Collections.emptyList())
                .distances(distances)
                .code("import java.util.ArrayList;\n" +
                        "import java.util.List;\n\n" +
                        "public class TSPProblem {\n" +
                        "    private List<List<Integer>> solutions = new ArrayList<>();\n\n" +
                        "    public List<Integer> runTSP(double[][] distances, int iterations) {\n" +
                        "        return List.of(1,1,0,1,1);\n" +
                        "    }\n\n" +
                        "}")
                .build();
    }

    private CodeRunnerSummary simpleCodeSummary() {
        var summary = new CodeRunnerSummary();
        summary.setIterations(5);
        summary.setTimeRun(0.0);
        summary.setFitness(7.0);
        summary.setEfficacy(-1.0);
        return summary;
    }

    private CodeOptions typicalCodeOptions() {
        double[][] distances = {
                {1, 2, 3, 4},
                {2, 1, 3, 4},
                {3, 1, 2, 4},
                {1, 3, 2, 4}
        };
        return CodeOptions.builder()
                .className("TSPProblem")
                .packageName("com.exercise")
                .methodToCall("runTSP")
                .iterations(5000)
                .importsAllowed(List.of("import java.util.List;", "import java.util.ArrayList;"))
                .illegalMethods(Collections.emptyList())
                .distances(distances)
                .code("import java.util.ArrayList;\n" +
                        "import java.util.List;\n\n" +
                        "public class TSPProblem {\n" +
                        "    private final List<List<Integer>> solutions = new ArrayList<>();\n" +
                        "\n" +
                        "    public List<Integer> runTSP(double[][] distances, int iterations) {\n" +
                        "        solutions.add(List.of(1,1,0,1));\n" +
                        "        solutions.add(List.of(1,0,0,1));\n" +
                        "        solutions.add(List.of(1,1,1,1));\n" +
                        "        solutions.add(List.of(0,1,0,1));\n" +
                        "        return List.of(0,1,0,1);\n" +
                        "    }\n" +
                        "}")
                .build();
    }

    @AfterAll
    public static void cleanup() {
        vertx.close();
    }

}