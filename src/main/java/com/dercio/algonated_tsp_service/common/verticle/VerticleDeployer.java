package com.dercio.algonated_tsp_service.common.verticle;

import com.dercio.algonated_tsp_service.common.AnnotationProcessor;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@AllArgsConstructor
public class VerticleDeployer implements AnnotationProcessor<DeploymentOptions> {

    private final Vertx vertx;
    private final Injector injector;

    @Override
    public void process(DeploymentOptions deploymentOptions) {
        new Reflections(basePackage()).get(TypesAnnotated.with(Verticle.class).asClass())
                .stream()
                .map(injector::getInstance)
                .filter(AbstractVerticle.class::isInstance)
                .map(AbstractVerticle.class::cast)
                .forEach(verticle -> vertx.deployVerticle(verticle, deploymentOptions));
    }
}
