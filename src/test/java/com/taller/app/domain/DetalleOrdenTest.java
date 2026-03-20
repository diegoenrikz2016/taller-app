package com.taller.app.domain;

import static com.taller.app.domain.DetalleOrdenTestSamples.*;
import static com.taller.app.domain.OrdenTrabajoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DetalleOrdenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DetalleOrden.class);
        DetalleOrden detalleOrden1 = getDetalleOrdenSample1();
        DetalleOrden detalleOrden2 = new DetalleOrden();
        assertThat(detalleOrden1).isNotEqualTo(detalleOrden2);

        detalleOrden2.setId(detalleOrden1.getId());
        assertThat(detalleOrden1).isEqualTo(detalleOrden2);

        detalleOrden2 = getDetalleOrdenSample2();
        assertThat(detalleOrden1).isNotEqualTo(detalleOrden2);
    }

    @Test
    void ordenTrabajoTest() {
        DetalleOrden detalleOrden = getDetalleOrdenRandomSampleGenerator();
        OrdenTrabajo ordenTrabajoBack = getOrdenTrabajoRandomSampleGenerator();

        detalleOrden.setOrdenTrabajo(ordenTrabajoBack);
        assertThat(detalleOrden.getOrdenTrabajo()).isEqualTo(ordenTrabajoBack);

        detalleOrden.ordenTrabajo(null);
        assertThat(detalleOrden.getOrdenTrabajo()).isNull();
    }
}
