package com.taller.app.domain;

import static com.taller.app.domain.ClienteTestSamples.*;
import static com.taller.app.domain.VehiculoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VehiculoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehiculo.class);
        Vehiculo vehiculo1 = getVehiculoSample1();
        Vehiculo vehiculo2 = new Vehiculo();
        assertThat(vehiculo1).isNotEqualTo(vehiculo2);

        vehiculo2.setId(vehiculo1.getId());
        assertThat(vehiculo1).isEqualTo(vehiculo2);

        vehiculo2 = getVehiculoSample2();
        assertThat(vehiculo1).isNotEqualTo(vehiculo2);
    }

    @Test
    void clienteTest() {
        Vehiculo vehiculo = getVehiculoRandomSampleGenerator();
        Cliente clienteBack = getClienteRandomSampleGenerator();

        vehiculo.setCliente(clienteBack);
        assertThat(vehiculo.getCliente()).isEqualTo(clienteBack);

        vehiculo.cliente(null);
        assertThat(vehiculo.getCliente()).isNull();
    }
}
