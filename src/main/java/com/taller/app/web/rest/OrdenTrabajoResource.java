package com.taller.app.web.rest;

import com.taller.app.repository.OrdenTrabajoRepository;
import com.taller.app.service.OrdenTrabajoPdfService;
import com.taller.app.service.OrdenTrabajoService;
import com.taller.app.service.dto.OrdenTrabajoDTO;
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
 * REST controller for managing {@link com.taller.app.domain.OrdenTrabajo}.
 */
@RestController
@RequestMapping("/api/orden-trabajos")
public class OrdenTrabajoResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrdenTrabajoResource.class);

    private static final String ENTITY_NAME = "ordenTrabajo";

    @Value("${jhipster.clientApp.name:taller}")
    private String applicationName;

    private final OrdenTrabajoService ordenTrabajoService;

    private final OrdenTrabajoPdfService ordenTrabajoPdfService;

    private final OrdenTrabajoRepository ordenTrabajoRepository;

    public OrdenTrabajoResource(
        OrdenTrabajoService ordenTrabajoService,
        OrdenTrabajoRepository ordenTrabajoRepository,
        OrdenTrabajoPdfService ordenTrabajoPdfService
    ) {
        this.ordenTrabajoService = ordenTrabajoService;
        this.ordenTrabajoPdfService = ordenTrabajoPdfService;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
    }

    /**
     * {@code POST  /orden-trabajos} : Create a new ordenTrabajo.
     *
     * @param ordenTrabajoDTO the ordenTrabajoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new ordenTrabajoDTO, or with status
     *         {@code 400 (Bad Request)} if the ordenTrabajo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrdenTrabajoDTO> createOrdenTrabajo(@Valid @RequestBody OrdenTrabajoDTO ordenTrabajoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OrdenTrabajo : {}", ordenTrabajoDTO);
        if (ordenTrabajoDTO.getId() != null) {
            throw new BadRequestAlertException("A new ordenTrabajo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ordenTrabajoDTO = ordenTrabajoService.save(ordenTrabajoDTO);
        return ResponseEntity.created(new URI("/api/orden-trabajos/" + ordenTrabajoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ordenTrabajoDTO.getId().toString()))
            .body(ordenTrabajoDTO);
    }

    /**
     * {@code PUT  /orden-trabajos/:id} : Updates an existing ordenTrabajo.
     *
     * @param id              the id of the ordenTrabajoDTO to save.
     * @param ordenTrabajoDTO the ordenTrabajoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated ordenTrabajoDTO,
     *         or with status {@code 400 (Bad Request)} if the ordenTrabajoDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         ordenTrabajoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> updateOrdenTrabajo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrdenTrabajoDTO ordenTrabajoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrdenTrabajo : {}, {}", id, ordenTrabajoDTO);
        if (ordenTrabajoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordenTrabajoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordenTrabajoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ordenTrabajoDTO = ordenTrabajoService.update(ordenTrabajoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordenTrabajoDTO.getId().toString()))
            .body(ordenTrabajoDTO);
    }

    /**
     * {@code PATCH  /orden-trabajos/:id} : Partial updates given fields of an
     * existing ordenTrabajo, field will ignore if it is null
     *
     * @param id              the id of the ordenTrabajoDTO to save.
     * @param ordenTrabajoDTO the ordenTrabajoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated ordenTrabajoDTO,
     *         or with status {@code 400 (Bad Request)} if the ordenTrabajoDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the ordenTrabajoDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         ordenTrabajoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrdenTrabajoDTO> partialUpdateOrdenTrabajo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrdenTrabajoDTO ordenTrabajoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrdenTrabajo partially : {}, {}", id, ordenTrabajoDTO);
        if (ordenTrabajoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordenTrabajoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordenTrabajoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrdenTrabajoDTO> result = ordenTrabajoService.partialUpdate(ordenTrabajoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordenTrabajoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /orden-trabajos} : get all the Orden Trabajos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of Orden Trabajos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrdenTrabajoDTO>> getAllOrdenTrabajos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of OrdenTrabajos");
        Page<OrdenTrabajoDTO> page = ordenTrabajoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orden-trabajos/:id} : get the "id" ordenTrabajo.
     *
     * @param id the id of the ordenTrabajoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the ordenTrabajoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> getOrdenTrabajo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrdenTrabajo : {}", id);
        Optional<OrdenTrabajoDTO> ordenTrabajoDTO = ordenTrabajoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ordenTrabajoDTO);
    }

    /**
     * {@code DELETE  /orden-trabajos/:id} : delete the "id" ordenTrabajo.
     *
     * @param id the id of the ordenTrabajoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdenTrabajo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrdenTrabajo : {}", id);
        ordenTrabajoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrdenTrabajoDTO>> searchOrdenes(@RequestParam String query) {
        List<OrdenTrabajoDTO> list = ordenTrabajoService.search(query);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        OrdenTrabajoDTO orden = ordenTrabajoService.findOne(id).orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        byte[] pdf = ordenTrabajoPdfService.generarPdf(orden);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=orden_" + id + ".pdf")
            .header("Content-Type", "application/pdf")
            .body(pdf);
    }
}
