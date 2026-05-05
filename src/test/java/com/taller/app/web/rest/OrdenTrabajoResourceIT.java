package com.taller.app.web.rest;

import static com.taller.app.domain.OrdenTrabajoAsserts.*;
import static com.taller.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.taller.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.app.IntegrationTest;
import com.taller.app.domain.OrdenTrabajo;
import com.taller.app.domain.enumeration.EstadoOrden;
import com.taller.app.repository.OrdenTrabajoRepository;
import com.taller.app.service.dto.OrdenTrabajoDTO;
import com.taller.app.service.mapper.OrdenTrabajoMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrdenTrabajoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrdenTrabajoResourceIT {

    private static final LocalDate DEFAULT_FECHA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA = LocalDate.now(ZoneId.systemDefault());

    private static final EstadoOrden DEFAULT_ESTADO = EstadoOrden.PENDIENTE;
    private static final EstadoOrden UPDATED_ESTADO = EstadoOrden.EN_PROCESO;

    private static final String DEFAULT_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBBBBBBB";

    private static final String DEFAULT_MECANICO = "AAAAAAAAAA";
    private static final String UPDATED_MECANICO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_VALOR_PACTADO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_PACTADO = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ABONO = new BigDecimal(1);
    private static final BigDecimal UPDATED_ABONO = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SALDO = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_SALDO = BigDecimal.ZERO;

    private static final String ENTITY_API_URL = "/api/orden-trabajos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrdenTrabajoRepository ordenTrabajoRepository;

    @Autowired
    private OrdenTrabajoMapper ordenTrabajoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdenTrabajoMockMvc;

    private OrdenTrabajo ordenTrabajo;

    private OrdenTrabajo insertedOrdenTrabajo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrdenTrabajo createEntity() {
        return new OrdenTrabajo()
            .fecha(DEFAULT_FECHA)
            .estado(DEFAULT_ESTADO)
            .observaciones(DEFAULT_OBSERVACIONES)
            .mecanico(DEFAULT_MECANICO)
            .valorPactado(DEFAULT_VALOR_PACTADO)
            .abono(DEFAULT_ABONO)
            .saldo(DEFAULT_SALDO);
    }

    public static OrdenTrabajo createUpdatedEntity() {
        return new OrdenTrabajo()
            .fecha(UPDATED_FECHA)
            .estado(UPDATED_ESTADO)
            .observaciones(UPDATED_OBSERVACIONES)
            .mecanico(UPDATED_MECANICO)
            .valorPactado(UPDATED_VALOR_PACTADO)
            .abono(UPDATED_ABONO)
            .saldo(UPDATED_SALDO);
    }

    @BeforeEach
    void initTest() {
        ordenTrabajo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrdenTrabajo != null) {
            ordenTrabajoRepository.delete(insertedOrdenTrabajo);
            insertedOrdenTrabajo = null;
        }
    }

    @Test
    @Transactional
    void createOrdenTrabajo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);
        var returnedOrdenTrabajoDTO = om.readValue(
            restOrdenTrabajoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordenTrabajoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrdenTrabajoDTO.class
        );

        // Validate the OrdenTrabajo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrdenTrabajo = ordenTrabajoMapper.toEntity(returnedOrdenTrabajoDTO);
        assertOrdenTrabajoUpdatableFieldsEquals(returnedOrdenTrabajo, getPersistedOrdenTrabajo(returnedOrdenTrabajo));

        insertedOrdenTrabajo = returnedOrdenTrabajo;
    }

    @Test
    @Transactional
    void createOrdenTrabajoWithExistingId() throws Exception {
        // Create the OrdenTrabajo with an existing ID
        ordenTrabajo.setId(1L);
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdenTrabajoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordenTrabajoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFechaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ordenTrabajo.setFecha(null);

        // Create the OrdenTrabajo, which fails.
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        restOrdenTrabajoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordenTrabajoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ordenTrabajo.setEstado(null);

        // Create the OrdenTrabajo, which fails.
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        restOrdenTrabajoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordenTrabajoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrdenTrabajos() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        // Get all the ordenTrabajoList
        restOrdenTrabajoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ordenTrabajo.getId().intValue())))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES)))
            .andExpect(jsonPath("$.[*].mecanico").value(hasItem(DEFAULT_MECANICO)))
            .andExpect(jsonPath("$.[*].valorPactado").value(hasItem(sameNumber(DEFAULT_VALOR_PACTADO))))
            .andExpect(jsonPath("$.[*].abono").value(hasItem(sameNumber(DEFAULT_ABONO))))
            .andExpect(jsonPath("$.[*].saldo").value(hasItem(sameNumber(DEFAULT_SALDO))));
    }

    @Test
    @Transactional
    void getOrdenTrabajo() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        // Get the ordenTrabajo
        restOrdenTrabajoMockMvc
            .perform(get(ENTITY_API_URL_ID, ordenTrabajo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ordenTrabajo.getId().intValue()))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.observaciones").value(DEFAULT_OBSERVACIONES))
            .andExpect(jsonPath("$.mecanico").value(DEFAULT_MECANICO))
            .andExpect(jsonPath("$.valorPactado").value(sameNumber(DEFAULT_VALOR_PACTADO)))
            .andExpect(jsonPath("$.abono").value(sameNumber(DEFAULT_ABONO)))
            .andExpect(jsonPath("$.saldo").value(sameNumber(DEFAULT_SALDO)));
    }

    @Test
    @Transactional
    void getNonExistingOrdenTrabajo() throws Exception {
        // Get the ordenTrabajo
        restOrdenTrabajoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrdenTrabajo() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ordenTrabajo
        OrdenTrabajo updatedOrdenTrabajo = ordenTrabajoRepository.findById(ordenTrabajo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrdenTrabajo are not directly saved in db
        em.detach(updatedOrdenTrabajo);
        updatedOrdenTrabajo
            .fecha(UPDATED_FECHA)
            .estado(UPDATED_ESTADO)
            .observaciones(UPDATED_OBSERVACIONES)
            .mecanico(UPDATED_MECANICO)
            .valorPactado(UPDATED_VALOR_PACTADO)
            .abono(UPDATED_ABONO)
            .saldo(UPDATED_SALDO);
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(updatedOrdenTrabajo);

        restOrdenTrabajoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordenTrabajoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ordenTrabajoDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrdenTrabajoToMatchAllProperties(updatedOrdenTrabajo);
    }

    @Test
    @Transactional
    void putNonExistingOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordenTrabajoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ordenTrabajoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ordenTrabajoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordenTrabajoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrdenTrabajoWithPatch() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ordenTrabajo using partial update
        OrdenTrabajo partialUpdatedOrdenTrabajo = new OrdenTrabajo();
        partialUpdatedOrdenTrabajo.setId(ordenTrabajo.getId());

        partialUpdatedOrdenTrabajo.mecanico(UPDATED_MECANICO).valorPactado(UPDATED_VALOR_PACTADO).abono(UPDATED_ABONO);

        restOrdenTrabajoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrdenTrabajo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrdenTrabajo))
            )
            .andExpect(status().isOk());

        // Validate the OrdenTrabajo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdenTrabajoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrdenTrabajo, ordenTrabajo),
            getPersistedOrdenTrabajo(ordenTrabajo)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrdenTrabajoWithPatch() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ordenTrabajo using partial update
        OrdenTrabajo partialUpdatedOrdenTrabajo = new OrdenTrabajo();
        partialUpdatedOrdenTrabajo.setId(ordenTrabajo.getId());

        partialUpdatedOrdenTrabajo
            .fecha(UPDATED_FECHA)
            .estado(UPDATED_ESTADO)
            .observaciones(UPDATED_OBSERVACIONES)
            .mecanico(UPDATED_MECANICO)
            .valorPactado(UPDATED_VALOR_PACTADO)
            .abono(UPDATED_ABONO)
            .saldo(UPDATED_SALDO);

        restOrdenTrabajoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrdenTrabajo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrdenTrabajo))
            )
            .andExpect(status().isOk());

        // Validate the OrdenTrabajo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdenTrabajoUpdatableFieldsEquals(partialUpdatedOrdenTrabajo, getPersistedOrdenTrabajo(partialUpdatedOrdenTrabajo));
    }

    @Test
    @Transactional
    void patchNonExistingOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ordenTrabajoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordenTrabajoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordenTrabajoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrdenTrabajo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ordenTrabajo.setId(longCount.incrementAndGet());

        // Create the OrdenTrabajo
        OrdenTrabajoDTO ordenTrabajoDTO = ordenTrabajoMapper.toDto(ordenTrabajo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdenTrabajoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ordenTrabajoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrdenTrabajo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrdenTrabajo() throws Exception {
        // Initialize the database
        insertedOrdenTrabajo = ordenTrabajoRepository.saveAndFlush(ordenTrabajo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ordenTrabajo
        restOrdenTrabajoMockMvc
            .perform(delete(ENTITY_API_URL_ID, ordenTrabajo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ordenTrabajoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected OrdenTrabajo getPersistedOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoRepository.findById(ordenTrabajo.getId()).orElseThrow();
    }

    protected void assertPersistedOrdenTrabajoToMatchAllProperties(OrdenTrabajo expectedOrdenTrabajo) {
        assertOrdenTrabajoAllPropertiesEquals(expectedOrdenTrabajo, getPersistedOrdenTrabajo(expectedOrdenTrabajo));
    }

    protected void assertPersistedOrdenTrabajoToMatchUpdatableProperties(OrdenTrabajo expectedOrdenTrabajo) {
        assertOrdenTrabajoAllUpdatablePropertiesEquals(expectedOrdenTrabajo, getPersistedOrdenTrabajo(expectedOrdenTrabajo));
    }
}
