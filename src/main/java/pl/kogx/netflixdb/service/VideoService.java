package pl.kogx.netflixdb.service;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.repository.search.VideoSearchRepository;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.NullAwareBeanUtilsBean;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service class for managing videos.
 */
@Service
@Transactional
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoSearchRepository videoSearchRepository;

    public VideoService(VideoSearchRepository videoSearchRepository) {
        this.videoSearchRepository = videoSearchRepository;
    }

    public VideoDTO updateVideo(VideoDTO videoDTO) {
        Video video = videoSearchRepository.findById(videoDTO.getId()).orElse(new Video(videoDTO.getId()));
        new NullAwareBeanUtilsBean().copyProperties(video, videoDTO);
        return new VideoDTO(videoSearchRepository.save(video));
    }

    public Page<VideoDTO> findByGenreId(Long genreId, Pageable pageable) {
        return videoSearchRepository.findAllByGenreId(genreId, pageable).map(VideoDTO::new);
    }

    public VideoDTO findById(Long id) {
        return new VideoDTO(videoSearchRepository.findById(id).orElse(null));
    }


    /**
     * Save a video.
     *
     * @param video the entity to save
     * @return the persisted entity
     */
    public Video save(Video video) {
        log.debug("Request to save Video : {}", video);
        return videoSearchRepository.save(video);
    }

    /**
     * Get all the videos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Video> findAll(Pageable pageable) {
        log.debug("Request to get all Videos");
        return videoSearchRepository.findAll(pageable);
    }


    /**
     * Get one video by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Video> findOne(Long id) {
        log.debug("Request to get Video : {}", id);
        return videoSearchRepository.findById(id);
    }

    /**
     * Delete the video by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Video : {}", id);
        videoSearchRepository.deleteById(id);
    }

    /**
     * Search for the video corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Video> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Videos for query {}", query);
        return videoSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Video> search(String query, Integer fwebMin, Integer fwebMax, Integer imdbMin, Integer imdbMax, Integer[] genres, Pageable pageable) {
        log.debug("Request to search for a page of Videos for query={} fs={} fx={} is={} ix={} g={}", query, fwebMin, fwebMax, imdbMin, imdbMax, genres);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isEmpty(query)) {
            query = "*";
        }
        builder = builder.must(queryStringQuery(query).field("fwebTitle").field("title"));
        if (fwebMin > 0 || fwebMax > 0) {
            RangeQueryBuilder fwebVotesBuilder = QueryBuilders.rangeQuery("fwebVotes");
            if (fwebMin > 0) {
                fwebVotesBuilder = fwebVotesBuilder.gte(fwebMin);
            }
            if (fwebMax > 0) {
                fwebVotesBuilder = fwebVotesBuilder.lte(fwebMax);
            }
            builder = builder.must(fwebVotesBuilder);
        }
        if (imdbMin > 0 || imdbMax > 0) {
            RangeQueryBuilder imdbVotesBuilder = QueryBuilders.rangeQuery("imdbVotes");
            if (imdbMin > 0) {
                imdbVotesBuilder = imdbVotesBuilder.gte(imdbMin);
            }
            if (imdbMax > 0) {
                imdbVotesBuilder = imdbVotesBuilder.lte(imdbMax);
            }
            builder = builder.must(imdbVotesBuilder);
        }
        if(!ArrayUtils.isEmpty(genres)) {
                builder = builder.must(QueryBuilders.termsQuery("genreId", genres));
        }
        return videoSearchRepository.search(builder, pageable);
    }
}
