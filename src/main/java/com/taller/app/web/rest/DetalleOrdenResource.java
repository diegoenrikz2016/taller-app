package com.taller.app.web.rest;

import com.taller.app.repository.DetalleOrdenRepository;
import com.taller.app.service.DetalleOrdenService;
import com.taller.app.service.dto.DetalleOrdenDTO;
import com.taller.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.taller.app.domain.DetalleOrden}.
 */
@RestController
@RequestMapping("/api/detalle-ordens")
public class DetalleOrdenResource {

    private static final Logger LOG = LoggerFactory.getLogger(DetalleOrdenResource.class);

    private static final String ENTITY_NAME = "detalleOrden";

    @Value("${jhipster.clientApp.name:taller}")
    private String applicationName;

    private final DetalleOrdenService detalleOrdenService;

    private final DetalleOrdenRepository detalleOrdenRepository;

    public DetalleOrdenResource(DetalleOrdenService detalleOrdenService, DetalleOrdenRepository detalleOrdenRepository) {
        this.detalleOrdenService = detalleOrdenService;
        this.detalleOrdenRepository = detalleOrdenRepository;
    }

    /**
     * {@code POST  /detalle-ordens} : Create a new detalleOrden.
     *
     * @param detalleOrdenDTO the detalleOrdenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new detalleOrdenDTO, or with status {@code 400 (Bad Request)} if the detalleOrden has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DetalleOrdenDTO> createDetalleOrden(@Valid @RequestBody DetalleOrdenDTO detalleOrdenDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DetalleOrden : {}", detalleOrdenDTO);
        if (detalleOrdenDTO.getId() != null) {
            throw new BadRequestAlertException("A new detalleOrden cannot already have an ID", ENTITY_NAME, "idexists");
        }
        detalleOrdenDTO = detalleOrdenService.save(detalleOrdenDTO);
        return ResponseEntity.created(new URI("/api/detalle-ordens/" + detalleOrdenDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, detalleOrdenDTO.getId().toString()))
            .body(detalleOrdenDTO);
    }

    /**
     * {@code PUT  /detalle-ordens/:id} : Updates an existing detalleOrden.
     *
     * @param id the id of the detalleOrdenDTO to save.
     * @param detalleOrdenDTO the detalleOrdenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detalleOrdenDTO,
     * or with status {@code 400 (Bad Request)} if the detalleOrdenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the detalleOrdenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DetalleOrdenDTO> updateDetalleOrden(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DetalleOrdenDTO detalleOrdenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DetalleOrden : {}, {}", id, detalleOrdenDTO);
        if (detalleOrdenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, detalleOrdenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!detalleOrdenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        detalleOrdenDTO = detalleOrdenService.update(detalleOrdenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, detalleOrdenDTO.getId().toString()))
            .body(detalleOrdenDTO);
    }

    /**
     * {@code PATCH  /detalle-ordens/:id} : Partial updates given fields of an existing detalleOrden, field will ignore if it is null
     *
     * @param id the id of the detalleOrdenDTO to save.
     * @param detalleOrdenDTO the detalleOrdenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated detalleOrdenDTO,
     * or with status {@code 400 (Bad Request)} if the detalleOrdenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the detalleOrdenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the detalleOrdenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DetalleOrdenDTO> partialUpdateDetalleOrden(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DetalleOrdenDTO detalleOrdenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DetalleOrden partially : {}, {}", id, detalleOrdenDTO);
        if (detalleOrdenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, detalleOrdenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!detalleOrdenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DetalleOrdenDTO> result = detalleOrdenService.partialUpdate(detalleOrdenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, detalleOrdenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /detalle-ordens} : get all the Detalle Ordens.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Detalle Ordens in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DetalleOrdenDTO>> getAllDetalleOrdens(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of DetalleOrdens");
        Page<DetalleOrdenDTO> page = detalleOrdenService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /detalle-ordens/:id} : get the "id" detalleOrden.
     *
     * @param id the id of the detalleOrdenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the detalleOrdenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleOrdenDTO> getDetalleOrden(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DetalleOrden : {}", id);
        Optional<DetalleOrdenDTO> detalleOrdenDTO = detalleOrdenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(detalleOrdenDTO);
    }

    /**
     * {@code DELETE  /detalle-ordens/:id} : delete the "id" detalleOrden.
     *
     * @param id the id of the detalleOrdenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleOrden(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DetalleOrden : {}", id);
        detalleOrdenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
