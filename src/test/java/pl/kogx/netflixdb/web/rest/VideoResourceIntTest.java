package pl.kogx.netflixdb.web.rest;

import pl.kogx.netflixdb.NetflixdbApp;

import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.repository.VideoRepository;
import pl.kogx.netflixdb.repository.search.VideoSearchRepository;
import pl.kogx.netflixdb.service.VideoService;
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
 * Test class for the VideoResource REST controller.
 *
 * @see VideoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetflixdbApp.class)
public class VideoResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_FWEB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_FWEB_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_RELEASE_YEAR = 1;
    private static final Integer UPDATED_RELEASE_YEAR = 2;

    private static final String DEFAULT_GENRE = "AAAAAAAAAA";
    private static final String UPDATED_GENRE = "BBBBBBBBBB";

    private static final Long DEFAULT_GENRE_ID = 1L;
    private static final Long UPDATED_GENRE_ID = 2L;

    private static final Boolean DEFAULT_ORIGINAL = false;
    private static final Boolean UPDATED_ORIGINAL = true;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OMDB_AVAILABLE = false;
    private static final Boolean UPDATED_OMDB_AVAILABLE = true;

    private static final Boolean DEFAULT_FWEB_AVAILABLE = false;
    private static final Boolean UPDATED_FWEB_AVAILABLE = true;

    private static final Float DEFAULT_IMDB_RATING = 1F;
    private static final Float UPDATED_IMDB_RATING = 2F;

    private static final Float DEFAULT_FWEB_RATING = 1F;
    private static final Float UPDATED_FWEB_RATING = 2F;

    private static final Long DEFAULT_IMDB_VOTES = 1L;
    private static final Long UPDATED_IMDB_VOTES = 2L;

    private static final Long DEFAULT_FWEB_VOTES = 1L;
    private static final Long UPDATED_FWEB_VOTES = 2L;

    private static final Integer DEFAULT_METASCORE = 1;
    private static final Integer UPDATED_METASCORE = 2;

    private static final Float DEFAULT_TOMATO_RATING = 1F;
    private static final Float UPDATED_TOMATO_RATING = 2F;

    private static final Float DEFAULT_TOMATO_USER_RATING = 1F;
    private static final Float UPDATED_TOMATO_USER_RATING = 2F;

    private static final String DEFAULT_IMDB_ID = "AAAAAAAAAA";
    private static final String UPDATED_IMDB_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_FWEB_ID = 1L;
    private static final Long UPDATED_FWEB_ID = 2L;

    private static final String DEFAULT_BOXART = "AAAAAAAAAA";
    private static final String UPDATED_BOXART = "BBBBBBBBBB";

    private static final String DEFAULT_FWEB_PLOT = "AAAAAAAAAA";
    private static final String UPDATED_FWEB_PLOT = "BBBBBBBBBB";

    @Autowired
    private VideoRepository videoRepository;

    

    @Autowired
    private VideoService videoService;

    /**
     * This repository is mocked in the pl.kogx.netflixdb.repository.search test package.
     *
     * @see pl.kogx.netflixdb.repository.search.VideoSearchRepositoryMockConfiguration
     */
    @Autowired
    private VideoSearchRepository mockVideoSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVideoMockMvc;

    private Video video;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VideoResource videoResource = new VideoResource(videoService);
        this.restVideoMockMvc = MockMvcBuilders.standaloneSetup(videoResource)
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
    public static Video createEntity(EntityManager em) {
        Video video = new Video()
            .title(DEFAULT_TITLE)
            .fwebTitle(DEFAULT_FWEB_TITLE)
            .releaseYear(DEFAULT_RELEASE_YEAR)
            .genre(DEFAULT_GENRE)
            .genreId(DEFAULT_GENRE_ID)
            .original(DEFAULT_ORIGINAL)
            .type(DEFAULT_TYPE)
            .omdbAvailable(DEFAULT_OMDB_AVAILABLE)
            .fwebAvailable(DEFAULT_FWEB_AVAILABLE)
            .imdbRating(DEFAULT_IMDB_RATING)
            .fwebRating(DEFAULT_FWEB_RATING)
            .imdbVotes(DEFAULT_IMDB_VOTES)
            .fwebVotes(DEFAULT_FWEB_VOTES)
            .metascore(DEFAULT_METASCORE)
            .tomatoRating(DEFAULT_TOMATO_RATING)
            .tomatoUserRating(DEFAULT_TOMATO_USER_RATING)
            .imdbID(DEFAULT_IMDB_ID)
            .fwebID(DEFAULT_FWEB_ID)
            .boxart(DEFAULT_BOXART)
            .fwebPlot(DEFAULT_FWEB_PLOT);
        return video;
    }

    @Before
    public void initTest() {
        video = createEntity(em);
    }

    @Test
    @Transactional
    public void createVideo() throws Exception {
        int databaseSizeBeforeCreate = videoRepository.findAll().size();

        // Create the Video
        restVideoMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(video)))
            .andExpect(status().isCreated());

        // Validate the Video in the database
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(databaseSizeBeforeCreate + 1);
        Video testVideo = videoList.get(videoList.size() - 1);
        assertThat(testVideo.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testVideo.getFwebTitle()).isEqualTo(DEFAULT_FWEB_TITLE);
        assertThat(testVideo.getReleaseYear()).isEqualTo(DEFAULT_RELEASE_YEAR);
        assertThat(testVideo.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testVideo.getGenreId()).isEqualTo(DEFAULT_GENRE_ID);
        assertThat(testVideo.isOriginal()).isEqualTo(DEFAULT_ORIGINAL);
        assertThat(testVideo.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testVideo.isOmdbAvailable()).isEqualTo(DEFAULT_OMDB_AVAILABLE);
        assertThat(testVideo.isFwebAvailable()).isEqualTo(DEFAULT_FWEB_AVAILABLE);
        assertThat(testVideo.getImdbRating()).isEqualTo(DEFAULT_IMDB_RATING);
        assertThat(testVideo.getFwebRating()).isEqualTo(DEFAULT_FWEB_RATING);
        assertThat(testVideo.getImdbVotes()).isEqualTo(DEFAULT_IMDB_VOTES);
        assertThat(testVideo.getFwebVotes()).isEqualTo(DEFAULT_FWEB_VOTES);
        assertThat(testVideo.getMetascore()).isEqualTo(DEFAULT_METASCORE);
        assertThat(testVideo.getTomatoRating()).isEqualTo(DEFAULT_TOMATO_RATING);
        assertThat(testVideo.getTomatoUserRating()).isEqualTo(DEFAULT_TOMATO_USER_RATING);
        assertThat(testVideo.getImdbID()).isEqualTo(DEFAULT_IMDB_ID);
        assertThat(testVideo.getFwebID()).isEqualTo(DEFAULT_FWEB_ID);
        assertThat(testVideo.getBoxart()).isEqualTo(DEFAULT_BOXART);
        assertThat(testVideo.getFwebPlot()).isEqualTo(DEFAULT_FWEB_PLOT);

        // Validate the Video in Elasticsearch
        verify(mockVideoSearchRepository, times(1)).save(testVideo);
    }

    @Test
    @Transactional
    public void createVideoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = videoRepository.findAll().size();

        // Create the Video with an existing ID
        video.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVideoMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(video)))
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(databaseSizeBeforeCreate);

        // Validate the Video in Elasticsearch
        verify(mockVideoSearchRepository, times(0)).save(video);
    }

    @Test
    @Transactional
    public void getAllVideos() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);

        // Get all the videoList
        restVideoMockMvc.perform(get("/api/videos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(video.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].fwebTitle").value(hasItem(DEFAULT_FWEB_TITLE.toString())))
            .andExpect(jsonPath("$.[*].releaseYear").value(hasItem(DEFAULT_RELEASE_YEAR)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())))
            .andExpect(jsonPath("$.[*].genreId").value(hasItem(DEFAULT_GENRE_ID.intValue())))
            .andExpect(jsonPath("$.[*].original").value(hasItem(DEFAULT_ORIGINAL.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].omdbAvailable").value(hasItem(DEFAULT_OMDB_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].fwebAvailable").value(hasItem(DEFAULT_FWEB_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].imdbRating").value(hasItem(DEFAULT_IMDB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].fwebRating").value(hasItem(DEFAULT_FWEB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbVotes").value(hasItem(DEFAULT_IMDB_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].fwebVotes").value(hasItem(DEFAULT_FWEB_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].metascore").value(hasItem(DEFAULT_METASCORE)))
            .andExpect(jsonPath("$.[*].tomatoRating").value(hasItem(DEFAULT_TOMATO_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].tomatoUserRating").value(hasItem(DEFAULT_TOMATO_USER_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbID").value(hasItem(DEFAULT_IMDB_ID.toString())))
            .andExpect(jsonPath("$.[*].fwebID").value(hasItem(DEFAULT_FWEB_ID.intValue())))
            .andExpect(jsonPath("$.[*].boxart").value(hasItem(DEFAULT_BOXART.toString())))
            .andExpect(jsonPath("$.[*].fwebPlot").value(hasItem(DEFAULT_FWEB_PLOT.toString())));
    }
    

    @Test
    @Transactional
    public void getVideo() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);

        // Get the video
        restVideoMockMvc.perform(get("/api/videos/{id}", video.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(video.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.fwebTitle").value(DEFAULT_FWEB_TITLE.toString()))
            .andExpect(jsonPath("$.releaseYear").value(DEFAULT_RELEASE_YEAR))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE.toString()))
            .andExpect(jsonPath("$.genreId").value(DEFAULT_GENRE_ID.intValue()))
            .andExpect(jsonPath("$.original").value(DEFAULT_ORIGINAL.booleanValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.omdbAvailable").value(DEFAULT_OMDB_AVAILABLE.booleanValue()))
            .andExpect(jsonPath("$.fwebAvailable").value(DEFAULT_FWEB_AVAILABLE.booleanValue()))
            .andExpect(jsonPath("$.imdbRating").value(DEFAULT_IMDB_RATING.doubleValue()))
            .andExpect(jsonPath("$.fwebRating").value(DEFAULT_FWEB_RATING.doubleValue()))
            .andExpect(jsonPath("$.imdbVotes").value(DEFAULT_IMDB_VOTES.intValue()))
            .andExpect(jsonPath("$.fwebVotes").value(DEFAULT_FWEB_VOTES.intValue()))
            .andExpect(jsonPath("$.metascore").value(DEFAULT_METASCORE))
            .andExpect(jsonPath("$.tomatoRating").value(DEFAULT_TOMATO_RATING.doubleValue()))
            .andExpect(jsonPath("$.tomatoUserRating").value(DEFAULT_TOMATO_USER_RATING.doubleValue()))
            .andExpect(jsonPath("$.imdbID").value(DEFAULT_IMDB_ID.toString()))
            .andExpect(jsonPath("$.fwebID").value(DEFAULT_FWEB_ID.intValue()))
            .andExpect(jsonPath("$.boxart").value(DEFAULT_BOXART.toString()))
            .andExpect(jsonPath("$.fwebPlot").value(DEFAULT_FWEB_PLOT.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingVideo() throws Exception {
        // Get the video
        restVideoMockMvc.perform(get("/api/videos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVideo() throws Exception {
        // Initialize the database
        videoService.save(video);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockVideoSearchRepository);

        int databaseSizeBeforeUpdate = videoRepository.findAll().size();

        // Update the video
        Video updatedVideo = videoRepository.findById(video.getId()).get();
        // Disconnect from session so that the updates on updatedVideo are not directly saved in db
        em.detach(updatedVideo);
        updatedVideo
            .title(UPDATED_TITLE)
            .fwebTitle(UPDATED_FWEB_TITLE)
            .releaseYear(UPDATED_RELEASE_YEAR)
            .genre(UPDATED_GENRE)
            .genreId(UPDATED_GENRE_ID)
            .original(UPDATED_ORIGINAL)
            .type(UPDATED_TYPE)
            .omdbAvailable(UPDATED_OMDB_AVAILABLE)
            .fwebAvailable(UPDATED_FWEB_AVAILABLE)
            .imdbRating(UPDATED_IMDB_RATING)
            .fwebRating(UPDATED_FWEB_RATING)
            .imdbVotes(UPDATED_IMDB_VOTES)
            .fwebVotes(UPDATED_FWEB_VOTES)
            .metascore(UPDATED_METASCORE)
            .tomatoRating(UPDATED_TOMATO_RATING)
            .tomatoUserRating(UPDATED_TOMATO_USER_RATING)
            .imdbID(UPDATED_IMDB_ID)
            .fwebID(UPDATED_FWEB_ID)
            .boxart(UPDATED_BOXART)
            .fwebPlot(UPDATED_FWEB_PLOT);

        restVideoMockMvc.perform(put("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVideo)))
            .andExpect(status().isOk());

        // Validate the Video in the database
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(databaseSizeBeforeUpdate);
        Video testVideo = videoList.get(videoList.size() - 1);
        assertThat(testVideo.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testVideo.getFwebTitle()).isEqualTo(UPDATED_FWEB_TITLE);
        assertThat(testVideo.getReleaseYear()).isEqualTo(UPDATED_RELEASE_YEAR);
        assertThat(testVideo.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testVideo.getGenreId()).isEqualTo(UPDATED_GENRE_ID);
        assertThat(testVideo.isOriginal()).isEqualTo(UPDATED_ORIGINAL);
        assertThat(testVideo.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testVideo.isOmdbAvailable()).isEqualTo(UPDATED_OMDB_AVAILABLE);
        assertThat(testVideo.isFwebAvailable()).isEqualTo(UPDATED_FWEB_AVAILABLE);
        assertThat(testVideo.getImdbRating()).isEqualTo(UPDATED_IMDB_RATING);
        assertThat(testVideo.getFwebRating()).isEqualTo(UPDATED_FWEB_RATING);
        assertThat(testVideo.getImdbVotes()).isEqualTo(UPDATED_IMDB_VOTES);
        assertThat(testVideo.getFwebVotes()).isEqualTo(UPDATED_FWEB_VOTES);
        assertThat(testVideo.getMetascore()).isEqualTo(UPDATED_METASCORE);
        assertThat(testVideo.getTomatoRating()).isEqualTo(UPDATED_TOMATO_RATING);
        assertThat(testVideo.getTomatoUserRating()).isEqualTo(UPDATED_TOMATO_USER_RATING);
        assertThat(testVideo.getImdbID()).isEqualTo(UPDATED_IMDB_ID);
        assertThat(testVideo.getFwebID()).isEqualTo(UPDATED_FWEB_ID);
        assertThat(testVideo.getBoxart()).isEqualTo(UPDATED_BOXART);
        assertThat(testVideo.getFwebPlot()).isEqualTo(UPDATED_FWEB_PLOT);

        // Validate the Video in Elasticsearch
        verify(mockVideoSearchRepository, times(1)).save(testVideo);
    }

    @Test
    @Transactional
    public void updateNonExistingVideo() throws Exception {
        int databaseSizeBeforeUpdate = videoRepository.findAll().size();

        // Create the Video

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVideoMockMvc.perform(put("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(video)))
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Video in Elasticsearch
        verify(mockVideoSearchRepository, times(0)).save(video);
    }

    @Test
    @Transactional
    public void deleteVideo() throws Exception {
        // Initialize the database
        videoService.save(video);

        int databaseSizeBeforeDelete = videoRepository.findAll().size();

        // Get the video
        restVideoMockMvc.perform(delete("/api/videos/{id}", video.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Video in Elasticsearch
        verify(mockVideoSearchRepository, times(1)).deleteById(video.getId());
    }

    @Test
    @Transactional
    public void searchVideo() throws Exception {
        // Initialize the database
        videoService.save(video);
        when(mockVideoSearchRepository.search(queryStringQuery("id:" + video.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(video), PageRequest.of(0, 1), 1));
        // Search the video
        restVideoMockMvc.perform(get("/api/_search/videos?query=id:" + video.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(video.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].fwebTitle").value(hasItem(DEFAULT_FWEB_TITLE.toString())))
            .andExpect(jsonPath("$.[*].releaseYear").value(hasItem(DEFAULT_RELEASE_YEAR)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())))
            .andExpect(jsonPath("$.[*].genreId").value(hasItem(DEFAULT_GENRE_ID.intValue())))
            .andExpect(jsonPath("$.[*].original").value(hasItem(DEFAULT_ORIGINAL.booleanValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].omdbAvailable").value(hasItem(DEFAULT_OMDB_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].fwebAvailable").value(hasItem(DEFAULT_FWEB_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].imdbRating").value(hasItem(DEFAULT_IMDB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].fwebRating").value(hasItem(DEFAULT_FWEB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbVotes").value(hasItem(DEFAULT_IMDB_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].fwebVotes").value(hasItem(DEFAULT_FWEB_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].metascore").value(hasItem(DEFAULT_METASCORE)))
            .andExpect(jsonPath("$.[*].tomatoRating").value(hasItem(DEFAULT_TOMATO_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].tomatoUserRating").value(hasItem(DEFAULT_TOMATO_USER_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbID").value(hasItem(DEFAULT_IMDB_ID.toString())))
            .andExpect(jsonPath("$.[*].fwebID").value(hasItem(DEFAULT_FWEB_ID.intValue())))
            .andExpect(jsonPath("$.[*].boxart").value(hasItem(DEFAULT_BOXART.toString())))
            .andExpect(jsonPath("$.[*].fwebPlot").value(hasItem(DEFAULT_FWEB_PLOT.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Video.class);
        Video video1 = new Video();
        video1.setId(1L);
        Video video2 = new Video();
        video2.setId(video1.getId());
        assertThat(video1).isEqualTo(video2);
        video2.setId(2L);
        assertThat(video1).isNotEqualTo(video2);
        video1.setId(null);
        assertThat(video1).isNotEqualTo(video2);
    }
}
