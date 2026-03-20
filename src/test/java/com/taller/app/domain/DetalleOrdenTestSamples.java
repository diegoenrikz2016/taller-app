package com.taller.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DetalleOrdenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DetalleOrden getDetalleOrdenSample1() {
        return new DetalleOrden().id(1L).descripcion("descripcion1").cantidad(1);
    }

    public static DetalleOrden getDetalleOrdenSample2() {
        return new DetalleOrden().id(2L).descripcion("descripcion2").cantidad(2);
    }

    public static DetalleOrden getDetalleOrdenRandomSampleGenerator() {
        return new DetalleOrden()
            .id(longCount.incrementAndGet())
            .descripcion(UUID.randomUUID().toString())
            .cantidad(intCount.incrementAndGet());
    }
}
