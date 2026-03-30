package com.taller.app.service.mapper;

import com.taller.app.domain.Cliente;
import com.taller.app.service.dto.ClienteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cliente} and its DTO {@link ClienteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClienteMapper extends EntityMapper<ClienteDTO, Cliente> {
    ClienteDTO toDto(Cliente s);
}
