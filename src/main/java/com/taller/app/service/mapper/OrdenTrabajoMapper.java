package com.taller.app.service.mapper;

import com.taller.app.domain.OrdenTrabajo;
import com.taller.app.domain.Vehiculo;
import com.taller.app.service.dto.OrdenTrabajoDTO;
import com.taller.app.service.dto.VehiculoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrdenTrabajo} and its DTO {@link OrdenTrabajoDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdenTrabajoMapper extends EntityMapper<OrdenTrabajoDTO, OrdenTrabajo> {
    @Mapping(target = "vehiculo", source = "vehiculo", qualifiedByName = "vehiculoId")
    OrdenTrabajoDTO toDto(OrdenTrabajo s);

    @Named("vehiculoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VehiculoDTO toDtoVehiculoId(Vehiculo vehiculo);
}
