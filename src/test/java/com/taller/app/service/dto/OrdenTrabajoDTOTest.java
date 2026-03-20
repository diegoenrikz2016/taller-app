package com.taller.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrdenTrabajoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrdenTrabajoDTO.class);
        OrdenTrabajoDTO ordenTrabajoDTO1 = new OrdenTrabajoDTO();
        ordenTrabajoDTO1.setId(1L);
        OrdenTrabajoDTO ordenTrabajoDTO2 = new OrdenTrabajoDTO();
        assertThat(ordenTrabajoDTO1).isNotEqualTo(ordenTrabajoDTO2);
        ordenTrabajoDTO2.setId(ordenTrabajoDTO1.getId());
        assertThat(ordenTrabajoDTO1).isEqualTo(ordenTrabajoDTO2);
        ordenTrabajoDTO2.setId(2L);
        assertThat(ordenTrabajoDTO1).isNotEqualTo(ordenTrabajoDTO2);
        ordenTrabajoDTO1.setId(null);
        assertThat(ordenTrabajoDTO1).isNotEqualTo(ordenTrabajoDTO2);
    }
}
