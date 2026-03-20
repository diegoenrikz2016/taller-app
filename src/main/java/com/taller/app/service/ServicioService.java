package com.taller.app.service;

import com.taller.app.domain.Servicio;
import com.taller.app.repository.ServicioRepository;
import com.taller.app.service.dto.ServicioDTO;
import com.taller.app.service.mapper.ServicioMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.taller.app.domain.Servicio}.
 */
@Service
@Transactional
public class ServicioService {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioService.class);

    private final ServicioRepository servicioRepository;

    private final ServicioMapper servicioMapper;

    public ServicioService(ServicioRepository servicioRepository, ServicioMapper servicioMapper) {
        this.servicioRepository = servicioRepository;
        this.servicioMapper = servicioMapper;
    }

    /**
     * Save a servicio.
     *
     * @param servicioDTO the entity to save.
     * @return the persisted entity.
     */
    public ServicioDTO save(ServicioDTO servicioDTO) {
        LOG.debug("Request to save Servicio : {}", servicioDTO);
        Servicio servicio = servicioMapper.toEntity(servicioDTO);
        servicio = servicioRepository.save(servicio);
        return servicioMapper.toDto(servicio);
    }

    /**
     * Update a servicio.
     *
     * @param servicioDTO the entity to save.
     * @return the persisted entity.
     */
    public ServicioDTO update(ServicioDTO servicioDTO) {
        LOG.debug("Request to update Servicio : {}", servicioDTO);
        Servicio servicio = servicioMapper.toEntity(servicioDTO);
        servicio = servicioRepository.save(servicio);
        return servicioMapper.toDto(servicio);
    }

    /**
     * Partially update a servicio.
     *
     * @param servicioDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ServicioDTO> partialUpdate(ServicioDTO servicioDTO) {
        LOG.debug("Request to partially update Servicio : {}", servicioDTO);

        return servicioRepository
            .findById(servicioDTO.getId())
            .map(existingServicio -> {
                servicioMapper.partialUpdate(existingServicio, servicioDTO);

                return existingServicio;
            })
            .map(servicioRepository::save)
            .map(servicioMapper::toDto);
    }

    /**
     * Get all the servicios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ServicioDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Servicios");
        return servicioRepository.findAll(pageable).map(servicioMapper::toDto);
    }

    /**
     * Get one servicio by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ServicioDTO> findOne(Long id) {
        LOG.debug("Request to get Servicio : {}", id);
        return servicioRepository.findById(id).map(servicioMapper::toDto);
    }

    /**
     * Delete the servicio by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Servicio : {}", id);
        servicioRepository.deleteById(id);
    }
}
