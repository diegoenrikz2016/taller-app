package com.taller.app.service;

import com.taller.app.domain.DetalleOrden;
import com.taller.app.repository.DetalleOrdenRepository;
import com.taller.app.service.dto.DetalleOrdenDTO;
import com.taller.app.service.mapper.DetalleOrdenMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.taller.app.domain.DetalleOrden}.
 */
@Service
@Transactional
public class DetalleOrdenService {

    private static final Logger LOG = LoggerFactory.getLogger(DetalleOrdenService.class);

    private final DetalleOrdenRepository detalleOrdenRepository;

    private final DetalleOrdenMapper detalleOrdenMapper;

    public DetalleOrdenService(DetalleOrdenRepository detalleOrdenRepository, DetalleOrdenMapper detalleOrdenMapper) {
        this.detalleOrdenRepository = detalleOrdenRepository;
        this.detalleOrdenMapper = detalleOrdenMapper;
    }

    /**
     * Save a detalleOrden.
     *
     * @param detalleOrdenDTO the entity to save.
     * @return the persisted entity.
     */
    public DetalleOrdenDTO save(DetalleOrdenDTO detalleOrdenDTO) {
        LOG.debug("Request to save DetalleOrden : {}", detalleOrdenDTO);
        DetalleOrden detalleOrden = detalleOrdenMapper.toEntity(detalleOrdenDTO);
        detalleOrden = detalleOrdenRepository.save(detalleOrden);
        return detalleOrdenMapper.toDto(detalleOrden);
    }

    /**
     * Update a detalleOrden.
     *
     * @param detalleOrdenDTO the entity to save.
     * @return the persisted entity.
     */
    public DetalleOrdenDTO update(DetalleOrdenDTO detalleOrdenDTO) {
        LOG.debug("Request to update DetalleOrden : {}", detalleOrdenDTO);
        DetalleOrden detalleOrden = detalleOrdenMapper.toEntity(detalleOrdenDTO);
        detalleOrden = detalleOrdenRepository.save(detalleOrden);
        return detalleOrdenMapper.toDto(detalleOrden);
    }

    /**
     * Partially update a detalleOrden.
     *
     * @param detalleOrdenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DetalleOrdenDTO> partialUpdate(DetalleOrdenDTO detalleOrdenDTO) {
        LOG.debug("Request to partially update DetalleOrden : {}", detalleOrdenDTO);

        return detalleOrdenRepository
            .findById(detalleOrdenDTO.getId())
            .map(existingDetalleOrden -> {
                detalleOrdenMapper.partialUpdate(existingDetalleOrden, detalleOrdenDTO);

                return existingDetalleOrden;
            })
            .map(detalleOrdenRepository::save)
            .map(detalleOrdenMapper::toDto);
    }

    /**
     * Get all the detalleOrdens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DetalleOrdenDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DetalleOrdens");
        return detalleOrdenRepository.findAll(pageable).map(detalleOrdenMapper::toDto);
    }

    /**
     * Get one detalleOrden by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DetalleOrdenDTO> findOne(Long id) {
        LOG.debug("Request to get DetalleOrden : {}", id);
        return detalleOrdenRepository.findById(id).map(detalleOrdenMapper::toDto);
    }

    /**
     * Delete the detalleOrden by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DetalleOrden : {}", id);
        detalleOrdenRepository.deleteById(id);
    }
}
