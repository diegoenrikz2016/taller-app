package com.taller.app.web.rest;

import static com.taller.app.domain.VehiculoAsserts.*;
import static com.taller.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.app.IntegrationTest;
import com.taller.app.domain.Vehiculo;
import com.taller.app.repository.VehiculoRepository;
import com.taller.app.service.dto.VehiculoDTO;
import com.taller.app.service.mapper.VehiculoMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link VehiculoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VehiculoResourceIT {

    private static final String DEFAULT_PLACA = "AAAAAAAAAA";
    private static final String UPDATED_PLACA = "BBBBBBBBBB";

    private static final String DEFAULT_MARCA = "AAAAAAAAAA";
    private static final String UPDATED_MARCA = "BBBBBBBBBB";

    private static final String DEFAULT_MODELO = "AAAAAAAAAA";
    private static final String UPDATED_MODELO = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/vehiculos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private VehiculoMapper vehiculoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVehiculoMockMvc;

    private Vehiculo vehiculo;

    private Vehiculo insertedVehiculo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehiculo createEntity() {
        return new Vehiculo().placa(DEFAULT_PLACA).marca(DEFAULT_MARCA).modelo(DEFAULT_MODELO).color(DEFAULT_COLOR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehiculo createUpdatedEntity() {
        return new Vehiculo().placa(UPDATED_PLACA).marca(UPDATED_MARCA).modelo(UPDATED_MODELO).color(UPDATED_COLOR);
    }

    @BeforeEach
    void initTest() {
        vehiculo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVehiculo != null) {
            vehiculoRepository.delete(insertedVehiculo);
            insertedVehiculo = null;
        }
    }

    @Test
    @Transactional
    void createVehiculo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);
        var returnedVehiculoDTO = om.readValue(
            restVehiculoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VehiculoDTO.class
        );

        // Validate the Vehiculo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVehiculo = vehiculoMapper.toEntity(returnedVehiculoDTO);
        assertVehiculoUpdatableFieldsEquals(returnedVehiculo, getPersistedVehiculo(returnedVehiculo));

        insertedVehiculo = returnedVehiculo;
    }

    @Test
    @Transactional
    void createVehiculoWithExistingId() throws Exception {
        // Create the Vehiculo with an existing ID
        vehiculo.setId(1L);
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVehiculoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPlacaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vehiculo.setPlaca(null);

        // Create the Vehiculo, which fails.
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        restVehiculoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVehiculos() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        // Get all the vehiculoList
        restVehiculoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehiculo.getId().intValue())))
            .andExpect(jsonPath("$.[*].placa").value(hasItem(DEFAULT_PLACA)))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA)))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));
    }

    @Test
    @Transactional
    void getVehiculo() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        // Get the vehiculo
        restVehiculoMockMvc
            .perform(get(ENTITY_API_URL_ID, vehiculo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vehiculo.getId().intValue()))
            .andExpect(jsonPath("$.placa").value(DEFAULT_PLACA))
            .andExpect(jsonPath("$.marca").value(DEFAULT_MARCA))
            .andExpect(jsonPath("$.modelo").value(DEFAULT_MODELO))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR));
    }

    @Test
    @Transactional
    void getNonExistingVehiculo() throws Exception {
        // Get the vehiculo
        restVehiculoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVehiculo() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo
        Vehiculo updatedVehiculo = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVehiculo are not directly saved in db
        em.detach(updatedVehiculo);
        updatedVehiculo.placa(UPDATED_PLACA).marca(UPDATED_MARCA).modelo(UPDATED_MODELO).color(UPDATED_COLOR);
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(updatedVehiculo);

        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vehiculoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vehiculoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVehiculoToMatchAllProperties(updatedVehiculo);
    }

    @Test
    @Transactional
    void putNonExistingVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vehiculoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vehiculoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vehiculoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVehiculoWithPatch() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo using partial update
        Vehiculo partialUpdatedVehiculo = new Vehiculo();
        partialUpdatedVehiculo.setId(vehiculo.getId());

        partialUpdatedVehiculo.modelo(UPDATED_MODELO).color(UPDATED_COLOR);

        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVehiculo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVehiculo))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehiculoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVehiculo, vehiculo), getPersistedVehiculo(vehiculo));
    }

    @Test
    @Transactional
    void fullUpdateVehiculoWithPatch() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo using partial update
        Vehiculo partialUpdatedVehiculo = new Vehiculo();
        partialUpdatedVehiculo.setId(vehiculo.getId());

        partialUpdatedVehiculo.placa(UPDATED_PLACA).marca(UPDATED_MARCA).modelo(UPDATED_MODELO).color(UPDATED_COLOR);

        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVehiculo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVehiculo))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehiculoUpdatableFieldsEquals(partialUpdatedVehiculo, getPersistedVehiculo(partialUpdatedVehiculo));
    }

    @Test
    @Transactional
    void patchNonExistingVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vehiculoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vehiculoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vehiculoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // Create the Vehiculo
        VehiculoDTO vehiculoDTO = vehiculoMapper.toDto(vehiculo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(vehiculoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVehiculo() throws Exception {
        // Initialize the database
        insertedVehiculo = vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vehiculo
        restVehiculoMockMvc
            .perform(delete(ENTITY_API_URL_ID, vehiculo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vehiculoRepository.count();
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

    protected Vehiculo getPersistedVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
    }

    protected void assertPersistedVehiculoToMatchAllProperties(Vehiculo expectedVehiculo) {
        assertVehiculoAllPropertiesEquals(expectedVehiculo, getPersistedVehiculo(expectedVehiculo));
    }

    protected void assertPersistedVehiculoToMatchUpdatableProperties(Vehiculo expectedVehiculo) {
        assertVehiculoAllUpdatablePropertiesEquals(expectedVehiculo, getPersistedVehiculo(expectedVehiculo));
    }
}
