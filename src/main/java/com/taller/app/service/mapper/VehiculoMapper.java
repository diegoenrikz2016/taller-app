package com.taller.app.service.mapper;

import com.taller.app.domain.Cliente;
import com.taller.app.domain.Vehiculo;
import com.taller.app.service.dto.ClienteDTO;
import com.taller.app.service.dto.VehiculoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Vehiculo} and its DTO {@link VehiculoDTO}.
 */
@Mapper(componentModel = "spring")
public interface VehiculoMapper extends EntityMapper<VehiculoDTO, Vehiculo> {
    @Mapping(target = "cliente", source = "cliente", qualifiedByName = "clienteId")
    VehiculoDTO toDto(Vehiculo s);

    @Named("clienteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClienteDTO toDtoClienteId(Cliente cliente);
}
