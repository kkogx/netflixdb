package pl.kogx.netflixdb.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.domain.Genre;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.GenreResolver;
import pl.kogx.netflixdb.web.rest.errors.BadRequestAlertException;
import pl.kogx.netflixdb.web.rest.util.HeaderUtil;
import pl.kogx.netflixdb.web.rest.util.PaginationUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * REST controller for managing Video.
 */
@RestController
@RequestMapping("/api")
public class VideoResource {

    private final Logger log = LoggerFactory.getLogger(VideoResource.class);

    private static final String ENTITY_NAME = "video";

    private final GenreResolver genreResolver;

    private final VideoService videoService;

    private final List<Genre> filmGenres;

    private final List<Genre> showGenres;

    public VideoResource(VideoService videoService, GenreResolver genreResolver) {
        this.videoService = videoService;
        this.filmGenres = toList(genreResolver.getFilmGenreByIdMap());
        this.showGenres = toList(genreResolver.getShowGenreByIdMap());
        this.genreResolver = genreResolver;
    }

    private List<Genre> toList(Map<String, String> genreByIdMap) {
        return genreByIdMap.entrySet().stream()
            .map(kv -> new Genre(Long.valueOf(kv.getKey()), kv.getValue()))
            .sorted(Comparator.comparing(Genre::getName)).collect(Collectors.toList());
    }

    /**
     * POST  /videos : Create a new video.
     *
     * @param video the video to create
     * @return the ResponseEntity with status 201 (Created) and with body the new video, or with status 400 (Bad Request) if the video has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/videos")
    @Timed
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO video) throws URISyntaxException {
        log.debug("REST request to save Video : {}", video);
        if (video.getId() != null) {
            throw new BadRequestAlertException("A new video cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Video result = videoService.save(video.toVideo());
        return ResponseEntity.created(new URI("/api/videos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new VideoDTO(result));
    }

    /**
     * PUT  /videos : Updates an existing video.
     *
     * @param video the video to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated video,
     * or with status 400 (Bad Request) if the video is not valid,
     * or with status 500 (Internal Server Error) if the video couldn't be updated
     */
    @PutMapping("/videos")
    @Timed
    public ResponseEntity<VideoDTO> updateVideo(@RequestBody VideoDTO video) {
        log.debug("REST request to update Video : {}", video);
        if (video.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Video result = videoService.save(video.toVideo());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, video.getId().toString()))
            .body(new VideoDTO(result));
    }

    /**
     * GET  /videos : get all the videos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of videos in body
     */
    @GetMapping("/videos")
    @Timed
    public ResponseEntity<List<VideoDTO>> getAllVideos(Pageable pageable) {
        log.debug("REST request to get a page of Videos");
        Page<Video> page = videoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/videos");
        return new ResponseEntity<>(page.getContent().stream().map(this::mapVideoToDTO).collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    /**
     * GET  /videos/:id : get the "id" video.
     *
     * @param id the id of the video to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the video, or with status 404 (Not Found)
     */
    @GetMapping("/videos/{id}")
    @Timed
    public ResponseEntity<VideoDTO> getVideo(@PathVariable Long id) {
        log.debug("REST request to get Video : {}", id);
        Optional<Video> video = videoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(video.map(this::mapVideoToDTO));
    }

    /**
     * DELETE  /videos/:id : delete the "id" video.
     *
     * @param id the id of the video to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/videos/{id}")
    @Timed
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        log.debug("REST request to delete Video : {}", id);
        videoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/videos?query=:query : search for the video corresponding
     * to the query.
     *
     * @param query    the query of the video search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/videos")
    @Timed
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Videos for query {}", query);
        Page<Video> page = videoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/videos");
        return new ResponseEntity<>(page.getContent()
            .stream()
            .map(this::mapVideoToDTO)
            .collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    @GetMapping("/_search/videos/range")
    @Timed
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam String query, @RequestParam Integer fwebMin,
                                                    @RequestParam Integer imdbMin, @RequestParam Integer yearMin,
                                                    @RequestParam Integer[] genres, Pageable pageable) {
        log.debug("REST request to search for a page of Videos for query {}", query);
        Page<Video> page = videoService.search(query, fwebMin, imdbMin, yearMin, null, genres, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/videos/range");
        return new ResponseEntity<>(page.getContent()
            .stream()
            .map(this::mapVideoToDTO)
            .collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    @GetMapping("/_search/videos/range/film")
    @Timed
    public ResponseEntity<List<VideoDTO>> searchFilms(@RequestParam String query, @RequestParam Integer fwebMin,
                                                   @RequestParam Integer imdbMin, @RequestParam Integer yearMin,
                                                   @RequestParam Integer[] genres, Pageable pageable) {
        log.debug("REST request to search for a page of Films for query {}", query);
        Page<Video> page = videoService.search(query, fwebMin, imdbMin, yearMin, new String[]{"movie"}, genres, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/videos/range/film");
        return new ResponseEntity<>(page.getContent()
            .stream()
            .map(this::mapVideoToDTO)
            .collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    @GetMapping("/_search/videos/range/show")
    @Timed
    public ResponseEntity<List<VideoDTO>> searchShows(@RequestParam String query, @RequestParam Integer fwebMin,
                                                   @RequestParam Integer imdbMin, @RequestParam Integer yearMin,
                                                   @RequestParam Integer[] genres, Pageable pageable) {
        log.debug("REST request to search for a page of Shows for query {}", query);
        Page<Video> page = videoService.search(query, fwebMin, imdbMin, yearMin, new String[]{"show"}, genres, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/videos/range/show");
        return new ResponseEntity<>(page.getContent()
            .stream()
            .map(this::mapVideoToDTO)
            .collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    @Cacheable("genres")
    @GetMapping("/videos/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        log.debug("REST request to get all (non-empty) genres");
        return new ResponseEntity<>(filterGenres(Stream.concat(filmGenres.stream(), showGenres.stream())), HttpStatus.OK);
    }

    @Cacheable("genres")
    @GetMapping("/videos/genres/film")
    public ResponseEntity<List<Genre>> getFilmGenres() {
        log.debug("REST request to get all (non-empty) film genres");
        return new ResponseEntity<>(filterGenres(filmGenres.stream()), HttpStatus.OK);
    }

    @Cacheable("genres")
    @GetMapping("/videos/genres/show")
    public ResponseEntity<List<Genre>> getShowGenres() {
        log.debug("REST request to get all (non-empty) film genres");
        return new ResponseEntity<>(filterGenres(showGenres.stream()), HttpStatus.OK);
    }

    private List<Genre> filterGenres(Stream<Genre> genres) {
        return genres.filter(g -> videoService.countByGenreId(g.getId()) > 0).collect(Collectors.toList());
    }

    private VideoDTO mapVideoToDTO(Video video) {
        VideoDTO v = new VideoDTO(video);
        v.setGenres(mapGenres(video.getGenreIds().stream().distinct().sorted().collect(Collectors.toList())));
        return v;
    }

    private String mapGenres(List<Long> genreIds) {
        final int MAX_GENRES = 3;
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (int idx = 0; idx < genreIds.size(); idx++) {
            if (idx >= MAX_GENRES) {
                sb.append("...");
                break;
            }
            sb.append(separator);
            sb.append(genreResolver.getGenreById(genreIds.get(idx).toString()));
            separator = ", ";
        }
        return sb.toString();
    }
}
