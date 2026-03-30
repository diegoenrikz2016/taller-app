package com.taller.app.service;

import com.taller.app.domain.Vehiculo;
import com.taller.app.repository.VehiculoRepository;
import com.taller.app.service.dto.VehiculoDTO;
import com.taller.app.service.mapper.VehiculoMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.taller.app.domain.Vehiculo}.
 */
@Service
@Transactional
public class VehiculoService {

    private static final Logger LOG = LoggerFactory.getLogger(VehiculoService.class);

    private final VehiculoRepository vehiculoRepository;

    private final VehiculoMapper vehiculoMapper;

    public VehiculoService(VehiculoRepository vehiculoRepository, VehiculoMapper vehiculoMapper) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoMapper = vehiculoMapper;
    }

    /**
     * Save a vehiculo.
     *
     * @param vehiculoDTO the entity to save.
     * @return the persisted entity.
     */
    public VehiculoDTO save(VehiculoDTO vehiculoDTO) {
        LOG.debug("Request to save Vehiculo : {}", vehiculoDTO);
        Vehiculo vehiculo = vehiculoMapper.toEntity(vehiculoDTO);
        vehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toDto(vehiculo);
    }

    /**
     * Update a vehiculo.
     *
     * @param vehiculoDTO the entity to save.
     * @return the persisted entity.
     */
    public VehiculoDTO update(VehiculoDTO vehiculoDTO) {
        LOG.debug("Request to update Vehiculo : {}", vehiculoDTO);
        Vehiculo vehiculo = vehiculoMapper.toEntity(vehiculoDTO);
        vehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toDto(vehiculo);
    }

    /**
     * Partially update a vehiculo.
     *
     * @param vehiculoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VehiculoDTO> partialUpdate(VehiculoDTO vehiculoDTO) {
        LOG.debug("Request to partially update Vehiculo : {}", vehiculoDTO);

        return vehiculoRepository
            .findById(vehiculoDTO.getId())
            .map(existingVehiculo -> {
                vehiculoMapper.partialUpdate(existingVehiculo, vehiculoDTO);

                return existingVehiculo;
            })
            .map(vehiculoRepository::save)
            .map(vehiculoMapper::toDto);
    }

    /**
     * Get all the vehiculos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VehiculoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Vehiculos");
        return vehiculoRepository.findAll(pageable).map(vehiculoMapper::toDto);
    }

    /**
     * Get one vehiculo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VehiculoDTO> findOne(Long id) {
        LOG.debug("Request to get Vehiculo : {}", id);
        return vehiculoRepository.findById(id).map(vehiculoMapper::toDto);
    }

    /**
     * Delete the vehiculo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Vehiculo : {}", id);
        vehiculoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VehiculoDTO> search(String query) {
        return vehiculoRepository
            .findByPlacaContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrModeloContainingIgnoreCase(query, query, query)
            .stream()
            .map(vehiculoMapper::toDto)
            .toList();
    }
}
