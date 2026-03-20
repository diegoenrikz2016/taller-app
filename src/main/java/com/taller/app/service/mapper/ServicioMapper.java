package com.taller.app.service.mapper;

import com.taller.app.domain.Servicio;
import com.taller.app.service.dto.ServicioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Servicio} and its DTO {@link ServicioDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicioMapper extends EntityMapper<ServicioDTO, Servicio> {}
