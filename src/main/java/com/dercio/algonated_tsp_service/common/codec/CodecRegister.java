package com.dercio.algonated_tsp_service.common.codec;

import com.dercio.algonated_tsp_service.common.AnnotationProcessor;
import io.vertx.core.eventbus.EventBus;
import org.reflections.Reflections;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class CodecRegister implements AnnotationProcessor<EventBus> {

    @Override
    public void process(EventBus eventBus) {
        new Reflections(basePackage())
                .get(TypesAnnotated.with(Codec.class).asClass())
                .stream()
                .map(aClass -> (Class<Object>) aClass)
                .forEach(aClass -> eventBus.registerDefaultCodec(aClass, new GenericCodec<>(aClass)));
    }
}
