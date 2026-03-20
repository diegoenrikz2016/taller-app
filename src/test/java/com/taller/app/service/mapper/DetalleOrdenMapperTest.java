package com.taller.app.service.mapper;

import static com.taller.app.domain.DetalleOrdenAsserts.*;
import static com.taller.app.domain.DetalleOrdenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DetalleOrdenMapperTest {

    private DetalleOrdenMapper detalleOrdenMapper;

    @BeforeEach
    void setUp() {
        detalleOrdenMapper = new DetalleOrdenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDetalleOrdenSample1();
        var actual = detalleOrdenMapper.toEntity(detalleOrdenMapper.toDto(expected));
        assertDetalleOrdenAllPropertiesEquals(expected, actual);
    }
}
