package com.taller.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VehiculoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VehiculoDTO.class);
        VehiculoDTO vehiculoDTO1 = new VehiculoDTO();
        vehiculoDTO1.setId(1L);
        VehiculoDTO vehiculoDTO2 = new VehiculoDTO();
        assertThat(vehiculoDTO1).isNotEqualTo(vehiculoDTO2);
        vehiculoDTO2.setId(vehiculoDTO1.getId());
        assertThat(vehiculoDTO1).isEqualTo(vehiculoDTO2);
        vehiculoDTO2.setId(2L);
        assertThat(vehiculoDTO1).isNotEqualTo(vehiculoDTO2);
        vehiculoDTO1.setId(null);
        assertThat(vehiculoDTO1).isNotEqualTo(vehiculoDTO2);
    }
}
