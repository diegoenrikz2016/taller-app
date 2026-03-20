package com.taller.app.service.mapper;

import static com.taller.app.domain.OrdenTrabajoAsserts.*;
import static com.taller.app.domain.OrdenTrabajoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrdenTrabajoMapperTest {

    private OrdenTrabajoMapper ordenTrabajoMapper;

    @BeforeEach
    void setUp() {
        ordenTrabajoMapper = new OrdenTrabajoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrdenTrabajoSample1();
        var actual = ordenTrabajoMapper.toEntity(ordenTrabajoMapper.toDto(expected));
        assertOrdenTrabajoAllPropertiesEquals(expected, actual);
    }
}
