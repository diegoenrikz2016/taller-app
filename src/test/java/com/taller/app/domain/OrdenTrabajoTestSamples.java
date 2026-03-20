package com.taller.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrdenTrabajoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static OrdenTrabajo getOrdenTrabajoSample1() {
        return new OrdenTrabajo().id(1L).observaciones("observaciones1").mecanico("mecanico1");
    }

    public static OrdenTrabajo getOrdenTrabajoSample2() {
        return new OrdenTrabajo().id(2L).observaciones("observaciones2").mecanico("mecanico2");
    }

    public static OrdenTrabajo getOrdenTrabajoRandomSampleGenerator() {
        return new OrdenTrabajo()
            .id(longCount.incrementAndGet())
            .observaciones(UUID.randomUUID().toString())
            .mecanico(UUID.randomUUID().toString());
    }
}
