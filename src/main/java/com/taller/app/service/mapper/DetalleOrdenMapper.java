package com.taller.app.service.mapper;

import com.taller.app.domain.DetalleOrden;
import com.taller.app.domain.OrdenTrabajo;
import com.taller.app.service.dto.DetalleOrdenDTO;
import com.taller.app.service.dto.OrdenTrabajoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DetalleOrden} and its DTO {@link DetalleOrdenDTO}.
 */
@Mapper(componentModel = "spring")
public interface DetalleOrdenMapper extends EntityMapper<DetalleOrdenDTO, DetalleOrden> {
    @Mapping(target = "ordenTrabajo", source = "ordenTrabajo")
    DetalleOrdenDTO toDto(DetalleOrden s);
}
