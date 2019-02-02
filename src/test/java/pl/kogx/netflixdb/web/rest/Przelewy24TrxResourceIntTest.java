package pl.kogx.netflixdb.web.rest;

import pl.kogx.netflixdb.NetflixdbApp;

import pl.kogx.netflixdb.domain.Przelewy24Trx;
import pl.kogx.netflixdb.repository.Przelewy24TrxRepository;
import pl.kogx.netflixdb.repository.search.Przelewy24TrxSearchRepository;
import pl.kogx.netflixdb.service.Przelewy24TrxService;
import pl.kogx.netflixdb.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


import static pl.kogx.netflixdb.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the Przelewy24TrxResource REST controller.
 *
 * @see Przelewy24TrxResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetflixdbApp.class)
public class Przelewy24TrxResourceIntTest {

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CONFIRMED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_SESSION_ID = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    @Autowired
    private Przelewy24TrxRepository przelewy24TrxRepository;

    

    @Autowired
    private Przelewy24TrxService przelewy24TrxService;

    /**
     * This repository is mocked in the pl.kogx.netflixdb.repository.search test package.
     *
     * @see pl.kogx.netflixdb.repository.search.Przelewy24TrxSearchRepositoryMockConfiguration
     */
    @Autowired
    private Przelewy24TrxSearchRepository mockPrzelewy24TrxSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPrzelewy24TrxMockMvc;

    private Przelewy24Trx przelewy24Trx;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final Przelewy24TrxResource przelewy24TrxResource = new Przelewy24TrxResource(przelewy24TrxService);
        this.restPrzelewy24TrxMockMvc = MockMvcBuilders.standaloneSetup(przelewy24TrxResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Przelewy24Trx createEntity(EntityManager em) {
        Przelewy24Trx przelewy24Trx = new Przelewy24Trx()
            .createdDate(DEFAULT_CREATED_DATE)
            .confirmedDate(DEFAULT_CONFIRMED_DATE)
            .email(DEFAULT_EMAIL)
            .sessionId(DEFAULT_SESSION_ID)
            .description(DEFAULT_DESCRIPTION)
            .currency(DEFAULT_CURRENCY)
            .country(DEFAULT_COUNTRY)
            .amount(DEFAULT_AMOUNT);
        return przelewy24Trx;
    }

    @Before
    public void initTest() {
        przelewy24Trx = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrzelewy24Trx() throws Exception {
        int databaseSizeBeforeCreate = przelewy24TrxRepository.findAll().size();

        // Create the Przelewy24Trx
        restPrzelewy24TrxMockMvc.perform(post("/api/przelewy-24-trxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(przelewy24Trx)))
            .andExpect(status().isCreated());

        // Validate the Przelewy24Trx in the database
        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeCreate + 1);
        Przelewy24Trx testPrzelewy24Trx = przelewy24TrxList.get(przelewy24TrxList.size() - 1);
        assertThat(testPrzelewy24Trx.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testPrzelewy24Trx.getConfirmedDate()).isEqualTo(DEFAULT_CONFIRMED_DATE);
        assertThat(testPrzelewy24Trx.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPrzelewy24Trx.getSessionId()).isEqualTo(DEFAULT_SESSION_ID);
        assertThat(testPrzelewy24Trx.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrzelewy24Trx.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPrzelewy24Trx.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testPrzelewy24Trx.getAmount()).isEqualTo(DEFAULT_AMOUNT);

        // Validate the Przelewy24Trx in Elasticsearch
        verify(mockPrzelewy24TrxSearchRepository, times(1)).save(testPrzelewy24Trx);
    }

    @Test
    @Transactional
    public void createPrzelewy24TrxWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = przelewy24TrxRepository.findAll().size();

        // Create the Przelewy24Trx with an existing ID
        przelewy24Trx.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrzelewy24TrxMockMvc.perform(post("/api/przelewy-24-trxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(przelewy24Trx)))
            .andExpect(status().isBadRequest());

        // Validate the Przelewy24Trx in the database
        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeCreate);

        // Validate the Przelewy24Trx in Elasticsearch
        verify(mockPrzelewy24TrxSearchRepository, times(0)).save(przelewy24Trx);
    }

    @Test
    @Transactional
    public void checkSessionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = przelewy24TrxRepository.findAll().size();
        // set the field null
        przelewy24Trx.setSessionId(null);

        // Create the Przelewy24Trx, which fails.

        restPrzelewy24TrxMockMvc.perform(post("/api/przelewy-24-trxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(przelewy24Trx)))
            .andExpect(status().isBadRequest());

        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrzelewy24Trxes() throws Exception {
        // Initialize the database
        przelewy24TrxRepository.saveAndFlush(przelewy24Trx);

        // Get all the przelewy24TrxList
        restPrzelewy24TrxMockMvc.perform(get("/api/przelewy-24-trxes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(przelewy24Trx.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].confirmedDate").value(hasItem(DEFAULT_CONFIRMED_DATE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)));
    }
    

    @Test
    @Transactional
    public void getPrzelewy24Trx() throws Exception {
        // Initialize the database
        przelewy24TrxRepository.saveAndFlush(przelewy24Trx);

        // Get the przelewy24Trx
        restPrzelewy24TrxMockMvc.perform(get("/api/przelewy-24-trxes/{id}", przelewy24Trx.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(przelewy24Trx.getId().intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.confirmedDate").value(DEFAULT_CONFIRMED_DATE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.sessionId").value(DEFAULT_SESSION_ID.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT));
    }
    @Test
    @Transactional
    public void getNonExistingPrzelewy24Trx() throws Exception {
        // Get the przelewy24Trx
        restPrzelewy24TrxMockMvc.perform(get("/api/przelewy-24-trxes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrzelewy24Trx() throws Exception {
        // Initialize the database
        przelewy24TrxService.save(przelewy24Trx);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPrzelewy24TrxSearchRepository);

        int databaseSizeBeforeUpdate = przelewy24TrxRepository.findAll().size();

        // Update the przelewy24Trx
        Przelewy24Trx updatedPrzelewy24Trx = przelewy24TrxRepository.findById(przelewy24Trx.getId()).get();
        // Disconnect from session so that the updates on updatedPrzelewy24Trx are not directly saved in db
        em.detach(updatedPrzelewy24Trx);
        updatedPrzelewy24Trx
            .createdDate(UPDATED_CREATED_DATE)
            .confirmedDate(UPDATED_CONFIRMED_DATE)
            .email(UPDATED_EMAIL)
            .sessionId(UPDATED_SESSION_ID)
            .description(UPDATED_DESCRIPTION)
            .currency(UPDATED_CURRENCY)
            .country(UPDATED_COUNTRY)
            .amount(UPDATED_AMOUNT);

        restPrzelewy24TrxMockMvc.perform(put("/api/przelewy-24-trxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPrzelewy24Trx)))
            .andExpect(status().isOk());

        // Validate the Przelewy24Trx in the database
        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeUpdate);
        Przelewy24Trx testPrzelewy24Trx = przelewy24TrxList.get(przelewy24TrxList.size() - 1);
        assertThat(testPrzelewy24Trx.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testPrzelewy24Trx.getConfirmedDate()).isEqualTo(UPDATED_CONFIRMED_DATE);
        assertThat(testPrzelewy24Trx.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPrzelewy24Trx.getSessionId()).isEqualTo(UPDATED_SESSION_ID);
        assertThat(testPrzelewy24Trx.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrzelewy24Trx.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPrzelewy24Trx.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPrzelewy24Trx.getAmount()).isEqualTo(UPDATED_AMOUNT);

        // Validate the Przelewy24Trx in Elasticsearch
        verify(mockPrzelewy24TrxSearchRepository, times(1)).save(testPrzelewy24Trx);
    }

    @Test
    @Transactional
    public void updateNonExistingPrzelewy24Trx() throws Exception {
        int databaseSizeBeforeUpdate = przelewy24TrxRepository.findAll().size();

        // Create the Przelewy24Trx

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPrzelewy24TrxMockMvc.perform(put("/api/przelewy-24-trxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(przelewy24Trx)))
            .andExpect(status().isBadRequest());

        // Validate the Przelewy24Trx in the database
        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Przelewy24Trx in Elasticsearch
        verify(mockPrzelewy24TrxSearchRepository, times(0)).save(przelewy24Trx);
    }

    @Test
    @Transactional
    public void deletePrzelewy24Trx() throws Exception {
        // Initialize the database
        przelewy24TrxService.save(przelewy24Trx);

        int databaseSizeBeforeDelete = przelewy24TrxRepository.findAll().size();

        // Get the przelewy24Trx
        restPrzelewy24TrxMockMvc.perform(delete("/api/przelewy-24-trxes/{id}", przelewy24Trx.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Przelewy24Trx> przelewy24TrxList = przelewy24TrxRepository.findAll();
        assertThat(przelewy24TrxList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Przelewy24Trx in Elasticsearch
        verify(mockPrzelewy24TrxSearchRepository, times(1)).deleteById(przelewy24Trx.getId());
    }

    @Test
    @Transactional
    public void searchPrzelewy24Trx() throws Exception {
        // Initialize the database
        przelewy24TrxService.save(przelewy24Trx);
        when(mockPrzelewy24TrxSearchRepository.search(queryStringQuery("id:" + przelewy24Trx.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(przelewy24Trx), PageRequest.of(0, 1), 1));
        // Search the przelewy24Trx
        restPrzelewy24TrxMockMvc.perform(get("/api/_search/przelewy-24-trxes?query=id:" + przelewy24Trx.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(przelewy24Trx.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].confirmedDate").value(hasItem(DEFAULT_CONFIRMED_DATE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Przelewy24Trx.class);
        Przelewy24Trx przelewy24Trx1 = new Przelewy24Trx();
        przelewy24Trx1.setId(1L);
        Przelewy24Trx przelewy24Trx2 = new Przelewy24Trx();
        przelewy24Trx2.setId(przelewy24Trx1.getId());
        assertThat(przelewy24Trx1).isEqualTo(przelewy24Trx2);
        przelewy24Trx2.setId(2L);
        assertThat(przelewy24Trx1).isNotEqualTo(przelewy24Trx2);
        przelewy24Trx1.setId(null);
        assertThat(przelewy24Trx1).isNotEqualTo(przelewy24Trx2);
    }
}
