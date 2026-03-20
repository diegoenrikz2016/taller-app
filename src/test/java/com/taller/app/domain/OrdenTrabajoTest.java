package com.taller.app.domain;

import static com.taller.app.domain.DetalleOrdenTestSamples.*;
import static com.taller.app.domain.OrdenTrabajoTestSamples.*;
import static com.taller.app.domain.VehiculoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrdenTrabajoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrdenTrabajo.class);
        OrdenTrabajo ordenTrabajo1 = getOrdenTrabajoSample1();
        OrdenTrabajo ordenTrabajo2 = new OrdenTrabajo();
        assertThat(ordenTrabajo1).isNotEqualTo(ordenTrabajo2);

        ordenTrabajo2.setId(ordenTrabajo1.getId());
        assertThat(ordenTrabajo1).isEqualTo(ordenTrabajo2);

        ordenTrabajo2 = getOrdenTrabajoSample2();
        assertThat(ordenTrabajo1).isNotEqualTo(ordenTrabajo2);
    }

    @Test
    void detallesTest() {
        OrdenTrabajo ordenTrabajo = getOrdenTrabajoRandomSampleGenerator();
        DetalleOrden detalleOrdenBack = getDetalleOrdenRandomSampleGenerator();

        ordenTrabajo.addDetalles(detalleOrdenBack);
        assertThat(ordenTrabajo.getDetalleses()).containsOnly(detalleOrdenBack);
        assertThat(detalleOrdenBack.getOrdenTrabajo()).isEqualTo(ordenTrabajo);

        ordenTrabajo.removeDetalles(detalleOrdenBack);
        assertThat(ordenTrabajo.getDetalleses()).doesNotContain(detalleOrdenBack);
        assertThat(detalleOrdenBack.getOrdenTrabajo()).isNull();

        ordenTrabajo.detalleses(new HashSet<>(Set.of(detalleOrdenBack)));
        assertThat(ordenTrabajo.getDetalleses()).containsOnly(detalleOrdenBack);
        assertThat(detalleOrdenBack.getOrdenTrabajo()).isEqualTo(ordenTrabajo);

        ordenTrabajo.setDetalleses(new HashSet<>());
        assertThat(ordenTrabajo.getDetalleses()).doesNotContain(detalleOrdenBack);
        assertThat(detalleOrdenBack.getOrdenTrabajo()).isNull();
    }

    @Test
    void vehiculoTest() {
        OrdenTrabajo ordenTrabajo = getOrdenTrabajoRandomSampleGenerator();
        Vehiculo vehiculoBack = getVehiculoRandomSampleGenerator();

        ordenTrabajo.setVehiculo(vehiculoBack);
        assertThat(ordenTrabajo.getVehiculo()).isEqualTo(vehiculoBack);

        ordenTrabajo.vehiculo(null);
        assertThat(ordenTrabajo.getVehiculo()).isNull();
    }
}
