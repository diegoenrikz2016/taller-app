package com.taller.app.service;

import com.taller.app.domain.OrdenTrabajo;
import com.taller.app.repository.OrdenTrabajoRepository;
import com.taller.app.service.PdfService;
import com.taller.app.service.dto.OrdenTrabajoDTO;
import com.taller.app.service.mapper.OrdenTrabajoMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.taller.app.domain.OrdenTrabajo}.
 */
@Service
@Transactional
public class OrdenTrabajoService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdenTrabajoService.class);

    private final OrdenTrabajoRepository ordenTrabajoRepository;

    private final OrdenTrabajoMapper ordenTrabajoMapper;

    @Autowired
    private PdfService pdfService;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenTrabajoRepository, OrdenTrabajoMapper ordenTrabajoMapper) {
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.ordenTrabajoMapper = ordenTrabajoMapper;
    }

    /**
     * Save a ordenTrabajo.
     *
     * @param ordenTrabajoDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdenTrabajoDTO save(OrdenTrabajoDTO ordenTrabajoDTO) {
        LOG.debug("Request to save OrdenTrabajo : {}", ordenTrabajoDTO);
        OrdenTrabajo ordenTrabajo = ordenTrabajoMapper.toEntity(ordenTrabajoDTO);
        ordenTrabajo = ordenTrabajoRepository.save(ordenTrabajo);
        return ordenTrabajoMapper.toDto(ordenTrabajo);
    }

    /**
     * Update a ordenTrabajo.
     *
     * @param ordenTrabajoDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdenTrabajoDTO update(OrdenTrabajoDTO ordenTrabajoDTO) {
        LOG.debug("Request to update OrdenTrabajo : {}", ordenTrabajoDTO);
        OrdenTrabajo ordenTrabajo = ordenTrabajoMapper.toEntity(ordenTrabajoDTO);
        ordenTrabajo = ordenTrabajoRepository.save(ordenTrabajo);
        return ordenTrabajoMapper.toDto(ordenTrabajo);
    }

    /**
     * Partially update a ordenTrabajo.
     *
     * @param ordenTrabajoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrdenTrabajoDTO> partialUpdate(OrdenTrabajoDTO ordenTrabajoDTO) {
        LOG.debug("Request to partially update OrdenTrabajo : {}", ordenTrabajoDTO);

        return ordenTrabajoRepository
            .findById(ordenTrabajoDTO.getId())
            .map(existingOrdenTrabajo -> {
                ordenTrabajoMapper.partialUpdate(existingOrdenTrabajo, ordenTrabajoDTO);

                return existingOrdenTrabajo;
            })
            .map(ordenTrabajoRepository::save)
            .map(ordenTrabajoMapper::toDto);
    }

    /**
     * Get all the ordenTrabajos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrdenTrabajoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all OrdenTrabajos");
        return ordenTrabajoRepository.findAll(pageable).map(ordenTrabajoMapper::toDto);
    }

    /**
     * Get one ordenTrabajo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrdenTrabajoDTO> findOne(Long id) {
        LOG.debug("Request to get OrdenTrabajo : {}", id);
        return ordenTrabajoRepository.findByIdWithRelationships(id).map(ordenTrabajoMapper::toDto);
    }

    /**
     * Delete the ordenTrabajo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OrdenTrabajo : {}", id);
        ordenTrabajoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OrdenTrabajoDTO> search(String query) {
        return ordenTrabajoRepository
            .findByObservacionesContainingIgnoreCaseOrMecanicoContainingIgnoreCase(query, query)
            .stream()
            .map(ordenTrabajoMapper::toDto)
            .toList();
    }
}
