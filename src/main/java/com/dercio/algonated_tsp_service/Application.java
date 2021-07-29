package com.dercio.algonated_tsp_service;

import com.dercio.algonated_tsp_service.config.HttpConfig;
import com.dercio.algonated_tsp_service.response.Response;
import com.dercio.algonated_tsp_service.verticles.VerticleAddresses;
import com.dercio.algonated_tsp_service.verticles.analytics.TSPAnalyticsVerticle;
import com.dercio.algonated_tsp_service.verticles.codec.CodecRegisterVerticle;
import com.dercio.algonated_tsp_service.verticles.runner.code.CodeOptions;
import com.dercio.algonated_tsp_service.verticles.runner.code.CodeRunnerVerticle;
import com.dercio.algonated_tsp_service.verticles.runner.demo.DemoOptions;
import com.dercio.algonated_tsp_service.verticles.runner.demo.DemoRunnerVerticle;
import com.dercio.algonated_tsp_service.verticles.tsp.TSPDemoVerticle;
import com.dercio.algonated_tsp_service.verticles.tsp.TSPSubmissionVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Application extends AbstractVerticle {

    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-type";
    private HttpServer httpServer;

    @Override
    public void start(Promise<Void> startPromise) {
        var httpConfig = new HttpConfig();
        final var router = Router.router(vertx);
        httpConfig.configureRouter(router);

        router.post("/exercise/submit/tsp")
                .handler(this::logRequestReceipt)
                .handler(this::handleTSPRequest)
                .handler(this::logResponseDispatch)
                .failureHandler(this::failureHandler);

        router.post("/exercise/demo/tsp")
                .handler(this::logRequestReceipt)
                .handler(this::handleDemoRequest)
                .handler(this::logResponseDispatch)
                .failureHandler(this::failureHandler);

        router.get("/health")
                .handler(this::handleHealth);

        httpServer = vertx.createHttpServer();
        httpServer
                .requestHandler(router)
                .listen(httpConfig.getPort(), httpConfig.getHost())
                .onSuccess(event -> log.info("HTTP Server Started ... {}", event.actualPort()))
                .onFailure(error -> log.error("Error starting up", error))
                .onComplete(event -> startPromise.complete());
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        httpServer.close()
                .onSuccess(event -> log.info("Goodbye HTTP server ..."))
                .onFailure(error -> log.error(error.getMessage()))
                .onComplete(event -> stopPromise.complete());
    }

    private void logResponseDispatch(RoutingContext rc) {
        log.info("Dispatched response to --> {}", rc.request().host());
        rc.response().end();
    }

    private void logRequestReceipt(RoutingContext rc) {
        log.info("Received request on --> {} from --> {}", rc.request().path(), rc.request().host());
        rc.response().setChunked(true);
        rc.next();
    }

    private void failureHandler(RoutingContext event) {
        log.error("Error: {}", event.failure().getMessage());
        event.response()
                .setStatusCode(400)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(new Response()
                        .setSuccess(false)
                        .setConsoleOutput(extractMessage(event.failure()))
                        .encode());
    }

    private String extractMessage(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return "Internal Server Error: NullPointerException";
        } else {
            return throwable.getMessage();
        }
    }

    private void handleTSPRequest(RoutingContext rc) {
        vertx.eventBus().<Response>request(
                VerticleAddresses.TSP_SUBMISSION.toString(),
                rc.getBodyAsJson().mapTo(CodeOptions.class),
                reply -> {
                    if (reply.succeeded()) {
                        rc.response()
                                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                                .write(reply.result().body().encode());
                        rc.next();
                    } else {
                        rc.fail(reply.cause());
                    }
                });
    }

    private void handleDemoRequest(RoutingContext rc) {
        vertx.eventBus().<Response>request(
                VerticleAddresses.TSP_DEMO.toString(),
                rc.getBodyAsJson().mapTo(DemoOptions.class),
                reply -> {
                    if (reply.succeeded()) {
                        rc.response()
                                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                                .write(reply.result().body().encode());
                        rc.next();
                    } else {
                        rc.fail(reply.cause());
                    }
                });
    }

    private void handleHealth(RoutingContext rc) {
        var reply = new HashMap<>();
        var runtime = Runtime.getRuntime();
        reply.put("status", "up");
        reply.put("freeMemory", runtime.freeMemory());
        reply.put("maxMemory", runtime.maxMemory());
        reply.put("totalMemory", runtime.totalMemory());
        rc.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(reply));
    }

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        var deployment = new DeploymentOptions();
        vertx.deployVerticle(new CodecRegisterVerticle(), deployment);
        vertx.deployVerticle(new TSPSubmissionVerticle(), deployment);
        vertx.deployVerticle(new CodeRunnerVerticle(), deployment);
        vertx.deployVerticle(new TSPAnalyticsVerticle(), deployment);
        vertx.deployVerticle(new DemoRunnerVerticle(), deployment);
        vertx.deployVerticle(new TSPDemoVerticle(), deployment);
        vertx.deployVerticle(new Application(), deployment);
    }
}
