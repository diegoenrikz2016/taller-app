package com.taller.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VehiculoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Vehiculo getVehiculoSample1() {
        return new Vehiculo().id(1L).placa("placa1").marca("marca1").modelo("modelo1").color("color1");
    }

    public static Vehiculo getVehiculoSample2() {
        return new Vehiculo().id(2L).placa("placa2").marca("marca2").modelo("modelo2").color("color2");
    }

    public static Vehiculo getVehiculoRandomSampleGenerator() {
        return new Vehiculo()
            .id(longCount.incrementAndGet())
            .placa(UUID.randomUUID().toString())
            .marca(UUID.randomUUID().toString())
            .modelo(UUID.randomUUID().toString())
            .color(UUID.randomUUID().toString());
    }
}
