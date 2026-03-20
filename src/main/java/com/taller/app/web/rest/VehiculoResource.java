package com.taller.app.web.rest;

import com.taller.app.repository.VehiculoRepository;
import com.taller.app.service.VehiculoService;
import com.taller.app.service.dto.VehiculoDTO;
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
 * REST controller for managing {@link com.taller.app.domain.Vehiculo}.
 */
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoResource {

    private static final Logger LOG = LoggerFactory.getLogger(VehiculoResource.class);

    private static final String ENTITY_NAME = "vehiculo";

    @Value("${jhipster.clientApp.name:taller}")
    private String applicationName;

    private final VehiculoService vehiculoService;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoResource(VehiculoService vehiculoService, VehiculoRepository vehiculoRepository) {
        this.vehiculoService = vehiculoService;
        this.vehiculoRepository = vehiculoRepository;
    }

    /**
     * {@code POST  /vehiculos} : Create a new vehiculo.
     *
     * @param vehiculoDTO the vehiculoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehiculoDTO, or with status {@code 400 (Bad Request)} if the vehiculo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VehiculoDTO> createVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Vehiculo : {}", vehiculoDTO);
        if (vehiculoDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehiculo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        vehiculoDTO = vehiculoService.save(vehiculoDTO);
        return ResponseEntity.created(new URI("/api/vehiculos/" + vehiculoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vehiculoDTO.getId().toString()))
            .body(vehiculoDTO);
    }

    /**
     * {@code PUT  /vehiculos/:id} : Updates an existing vehiculo.
     *
     * @param id the id of the vehiculoDTO to save.
     * @param vehiculoDTO the vehiculoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehiculoDTO,
     * or with status {@code 400 (Bad Request)} if the vehiculoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehiculoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> updateVehiculo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VehiculoDTO vehiculoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Vehiculo : {}, {}", id, vehiculoDTO);
        if (vehiculoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehiculoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehiculoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vehiculoDTO = vehiculoService.update(vehiculoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehiculoDTO.getId().toString()))
            .body(vehiculoDTO);
    }

    /**
     * {@code PATCH  /vehiculos/:id} : Partial updates given fields of an existing vehiculo, field will ignore if it is null
     *
     * @param id the id of the vehiculoDTO to save.
     * @param vehiculoDTO the vehiculoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehiculoDTO,
     * or with status {@code 400 (Bad Request)} if the vehiculoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vehiculoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vehiculoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VehiculoDTO> partialUpdateVehiculo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VehiculoDTO vehiculoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Vehiculo partially : {}, {}", id, vehiculoDTO);
        if (vehiculoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehiculoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehiculoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VehiculoDTO> result = vehiculoService.partialUpdate(vehiculoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehiculoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /vehiculos} : get all the Vehiculos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Vehiculos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VehiculoDTO>> getAllVehiculos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Vehiculos");
        Page<VehiculoDTO> page = vehiculoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vehiculos/:id} : get the "id" vehiculo.
     *
     * @param id the id of the vehiculoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehiculoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> getVehiculo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Vehiculo : {}", id);
        Optional<VehiculoDTO> vehiculoDTO = vehiculoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehiculoDTO);
    }

    /**
     * {@code DELETE  /vehiculos/:id} : delete the "id" vehiculo.
     *
     * @param id the id of the vehiculoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Vehiculo : {}", id);
        vehiculoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
