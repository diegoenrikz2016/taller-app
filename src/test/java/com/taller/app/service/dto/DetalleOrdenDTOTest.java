package com.taller.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DetalleOrdenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DetalleOrdenDTO.class);
        DetalleOrdenDTO detalleOrdenDTO1 = new DetalleOrdenDTO();
        detalleOrdenDTO1.setId(1L);
        DetalleOrdenDTO detalleOrdenDTO2 = new DetalleOrdenDTO();
        assertThat(detalleOrdenDTO1).isNotEqualTo(detalleOrdenDTO2);
        detalleOrdenDTO2.setId(detalleOrdenDTO1.getId());
        assertThat(detalleOrdenDTO1).isEqualTo(detalleOrdenDTO2);
        detalleOrdenDTO2.setId(2L);
        assertThat(detalleOrdenDTO1).isNotEqualTo(detalleOrdenDTO2);
        detalleOrdenDTO1.setId(null);
        assertThat(detalleOrdenDTO1).isNotEqualTo(detalleOrdenDTO2);
    }
}
