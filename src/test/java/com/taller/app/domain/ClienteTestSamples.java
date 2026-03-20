package com.taller.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClienteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Cliente getClienteSample1() {
        return new Cliente().id(1L).nombre("nombre1").cedula("cedula1").telefono("telefono1").direccion("direccion1");
    }

    public static Cliente getClienteSample2() {
        return new Cliente().id(2L).nombre("nombre2").cedula("cedula2").telefono("telefono2").direccion("direccion2");
    }

    public static Cliente getClienteRandomSampleGenerator() {
        return new Cliente()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .cedula(UUID.randomUUID().toString())
            .telefono(UUID.randomUUID().toString())
            .direccion(UUID.randomUUID().toString());
    }
}
