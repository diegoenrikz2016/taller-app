package com.taller.app.web.rest;

import static com.taller.app.domain.DetalleOrdenAsserts.*;
import static com.taller.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.taller.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.app.IntegrationTest;
import com.taller.app.domain.DetalleOrden;
import com.taller.app.repository.DetalleOrdenRepository;
import com.taller.app.service.dto.DetalleOrdenDTO;
import com.taller.app.service.mapper.DetalleOrdenMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link DetalleOrdenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DetalleOrdenResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final BigDecimal DEFAULT_PRECIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/detalle-ordens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private DetalleOrdenMapper detalleOrdenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDetalleOrdenMockMvc;

    private DetalleOrden detalleOrden;

    private DetalleOrden insertedDetalleOrden;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetalleOrden createEntity() {
        return new DetalleOrden().descripcion(DEFAULT_DESCRIPCION).cantidad(DEFAULT_CANTIDAD).precio(DEFAULT_PRECIO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DetalleOrden createUpdatedEntity() {
        return new DetalleOrden().descripcion(UPDATED_DESCRIPCION).cantidad(UPDATED_CANTIDAD).precio(UPDATED_PRECIO);
    }

    @BeforeEach
    void initTest() {
        detalleOrden = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDetalleOrden != null) {
            detalleOrdenRepository.delete(insertedDetalleOrden);
            insertedDetalleOrden = null;
        }
    }

    @Test
    @Transactional
    void createDetalleOrden() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);
        var returnedDetalleOrdenDTO = om.readValue(
            restDetalleOrdenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DetalleOrdenDTO.class
        );

        // Validate the DetalleOrden in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDetalleOrden = detalleOrdenMapper.toEntity(returnedDetalleOrdenDTO);
        assertDetalleOrdenUpdatableFieldsEquals(returnedDetalleOrden, getPersistedDetalleOrden(returnedDetalleOrden));

        insertedDetalleOrden = returnedDetalleOrden;
    }

    @Test
    @Transactional
    void createDetalleOrdenWithExistingId() throws Exception {
        // Create the DetalleOrden with an existing ID
        detalleOrden.setId(1L);
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDetalleOrdenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescripcionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        detalleOrden.setDescripcion(null);

        // Create the DetalleOrden, which fails.
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        restDetalleOrdenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCantidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        detalleOrden.setCantidad(null);

        // Create the DetalleOrden, which fails.
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        restDetalleOrdenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        detalleOrden.setPrecio(null);

        // Create the DetalleOrden, which fails.
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        restDetalleOrdenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDetalleOrdens() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        // Get all the detalleOrdenList
        restDetalleOrdenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(detalleOrden.getId().intValue())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(sameNumber(DEFAULT_PRECIO))));
    }

    @Test
    @Transactional
    void getDetalleOrden() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        // Get the detalleOrden
        restDetalleOrdenMockMvc
            .perform(get(ENTITY_API_URL_ID, detalleOrden.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(detalleOrden.getId().intValue()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.precio").value(sameNumber(DEFAULT_PRECIO)));
    }

    @Test
    @Transactional
    void getNonExistingDetalleOrden() throws Exception {
        // Get the detalleOrden
        restDetalleOrdenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDetalleOrden() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detalleOrden
        DetalleOrden updatedDetalleOrden = detalleOrdenRepository.findById(detalleOrden.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDetalleOrden are not directly saved in db
        em.detach(updatedDetalleOrden);
        updatedDetalleOrden.descripcion(UPDATED_DESCRIPCION).cantidad(UPDATED_CANTIDAD).precio(UPDATED_PRECIO);
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(updatedDetalleOrden);

        restDetalleOrdenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, detalleOrdenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detalleOrdenDTO))
            )
            .andExpect(status().isOk());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDetalleOrdenToMatchAllProperties(updatedDetalleOrden);
    }

    @Test
    @Transactional
    void putNonExistingDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, detalleOrdenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detalleOrdenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(detalleOrdenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDetalleOrdenWithPatch() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detalleOrden using partial update
        DetalleOrden partialUpdatedDetalleOrden = new DetalleOrden();
        partialUpdatedDetalleOrden.setId(detalleOrden.getId());

        partialUpdatedDetalleOrden.cantidad(UPDATED_CANTIDAD).precio(UPDATED_PRECIO);

        restDetalleOrdenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDetalleOrden.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDetalleOrden))
            )
            .andExpect(status().isOk());

        // Validate the DetalleOrden in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDetalleOrdenUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDetalleOrden, detalleOrden),
            getPersistedDetalleOrden(detalleOrden)
        );
    }

    @Test
    @Transactional
    void fullUpdateDetalleOrdenWithPatch() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the detalleOrden using partial update
        DetalleOrden partialUpdatedDetalleOrden = new DetalleOrden();
        partialUpdatedDetalleOrden.setId(detalleOrden.getId());

        partialUpdatedDetalleOrden.descripcion(UPDATED_DESCRIPCION).cantidad(UPDATED_CANTIDAD).precio(UPDATED_PRECIO);

        restDetalleOrdenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDetalleOrden.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDetalleOrden))
            )
            .andExpect(status().isOk());

        // Validate the DetalleOrden in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDetalleOrdenUpdatableFieldsEquals(partialUpdatedDetalleOrden, getPersistedDetalleOrden(partialUpdatedDetalleOrden));
    }

    @Test
    @Transactional
    void patchNonExistingDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, detalleOrdenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(detalleOrdenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(detalleOrdenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDetalleOrden() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        detalleOrden.setId(longCount.incrementAndGet());

        // Create the DetalleOrden
        DetalleOrdenDTO detalleOrdenDTO = detalleOrdenMapper.toDto(detalleOrden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDetalleOrdenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(detalleOrdenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DetalleOrden in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDetalleOrden() throws Exception {
        // Initialize the database
        insertedDetalleOrden = detalleOrdenRepository.saveAndFlush(detalleOrden);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the detalleOrden
        restDetalleOrdenMockMvc
            .perform(delete(ENTITY_API_URL_ID, detalleOrden.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return detalleOrdenRepository.count();
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

    protected DetalleOrden getPersistedDetalleOrden(DetalleOrden detalleOrden) {
        return detalleOrdenRepository.findById(detalleOrden.getId()).orElseThrow();
    }

    protected void assertPersistedDetalleOrdenToMatchAllProperties(DetalleOrden expectedDetalleOrden) {
        assertDetalleOrdenAllPropertiesEquals(expectedDetalleOrden, getPersistedDetalleOrden(expectedDetalleOrden));
    }

    protected void assertPersistedDetalleOrdenToMatchUpdatableProperties(DetalleOrden expectedDetalleOrden) {
        assertDetalleOrdenAllUpdatablePropertiesEquals(expectedDetalleOrden, getPersistedDetalleOrden(expectedDetalleOrden));
    }
}
