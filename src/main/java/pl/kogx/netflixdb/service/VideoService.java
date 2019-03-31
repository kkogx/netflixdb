package pl.kogx.netflixdb.service;

import lombok.Builder;
import lombok.ToString;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.User;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.repository.search.VideoSearchRepository;
import pl.kogx.netflixdb.service.dto.SeenOption;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.NullAwareBeanUtilsBean;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service class for managing videos.
 */
@Service
@Transactional
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoSearchRepository videoSearchRepository;

    private final UserService userService;

    public VideoService(VideoSearchRepository videoSearchRepository, UserService userService) {
        this.videoSearchRepository = videoSearchRepository;
        this.userService = userService;
    }

    public VideoDTO updateVideo(VideoDTO videoDTO) {
        Video video = videoSearchRepository.findById(videoDTO.getId()).orElse(new Video(videoDTO.getId()));
        new NullAwareBeanUtilsBean().copyProperties(video, videoDTO);
        return new VideoDTO(videoSearchRepository.save(video));
    }

    public Page<VideoDTO> findByGenreId(Long genreId, Pageable pageable) {
        return videoSearchRepository.findByGenreIds(genreId, pageable).map(VideoDTO::new);
    }

    public VideoDTO findById(Long id) {
        Optional<Video> video = videoSearchRepository.findById(id);
        return video.map(VideoDTO::new).orElse(null);
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
     * Get all the videos.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Iterable<Video> findAll() {
        log.debug("Request to get all Videos");
        return videoSearchRepository.findAll(Sort.by("id"));
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

    public void deleteByTimestampBefore(Date timestamp) {
        log.debug("Request to delete Video timestamp before: {}", timestamp);
        videoSearchRepository.deleteByTimestampBefore(timestamp.getTime());
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
    public Page<Video> search(SearchParams params) {
        log.info("Request to search for a page of Videos for params={}", params.toString());
        Page<Video> page = doSearch(params);
        return page;
    }

    public Page<Video> doSearch(SearchParams params) {
        NdbBoolQueryBuilder builder = new NdbBoolQueryBuilder();
        if (StringUtils.isEmpty(params.query)) {
            params.query = "*";
        }
        builder = builder.must(queryStringQuery(params.query).field("title").field("fwebTitle"));
        builder = builder.mustBetween(params.fwebMin, -1, "fwebVotes");
        builder = builder.mustBetween(params.imdbMin, -1, "imdbVotes");
        builder = builder.mustBetween(params.yearMin, -1, "releaseYear");
        if (!ArrayUtils.isEmpty(params.genres)) {
            builder = builder.must(QueryBuilders.termsQuery("genreIds", params.genres));
        }
        if (!ArrayUtils.isEmpty(params.types)) {
            builder = builder.must(QueryBuilders.termsQuery("type", params.types));
        }

        // filter out results without imdb and fweb ratings
        builder.should(QueryBuilders.existsQuery("omdbAvailable"));
        builder.should(QueryBuilders.existsQuery("fwebAvailable"));

        //filter by seen attribute
        if (EnumSet.of(SeenOption.YES, SeenOption.NO).contains(params.seen)) {
            Optional<User> user = userService.getUserWithAuthorities();
            if (user.isPresent()) {
                Set<Long> seen = User.collectSeenIds(user.get());
                if(params.seen == SeenOption.YES) {
                    builder = builder.must(QueryBuilders.termsQuery("id", seen));
                } else if(params.seen == SeenOption.NO) {
                    builder = builder.mustNot(QueryBuilders.termsQuery("id", seen));
                }
            }
        }
        return videoSearchRepository.search(builder, params.pageable);
    }

    public void deleteAll() {
        videoSearchRepository.deleteAll();
    }

    public long countByGenreId(long genreId) {
        return videoSearchRepository.countByGenreIds(genreId);
    }

    public List<Video> findByEmptyRating() {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("omdbAvailable")));
        builder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("fwebAvailable")));
        List<Video> result = new ArrayList<>();
        videoSearchRepository.search(builder).forEach(result::add);
        return result;
    }

    @Builder
    @ToString
    public static final class SearchParams {
        String query;
        Integer fwebMin;
        Integer imdbMin;
        Integer yearMin;
        String[] types;
        Integer[] genres;
        SeenOption seen;
        Pageable pageable;
    }

    private static class NdbBoolQueryBuilder extends BoolQueryBuilder {

        private NdbBoolQueryBuilder mustBetween(Integer min, Integer max, String name) {
            if (min <= 0 && max <= 0) {
                return this;
            }
            RangeQueryBuilder betweenBuilder = QueryBuilders.rangeQuery(name);
            if (min > 0) {
                betweenBuilder = betweenBuilder.gte(min);
            }
            if (max > 0) {
                betweenBuilder = betweenBuilder.lte(max);
            }
            return this.must(betweenBuilder);
        }

        @Override
        public NdbBoolQueryBuilder must(QueryBuilder queryBuilder) {
            return (NdbBoolQueryBuilder) super.must(queryBuilder);
        }

        @Override
        public NdbBoolQueryBuilder mustNot(QueryBuilder queryBuilder) {
            return (NdbBoolQueryBuilder) super.mustNot(queryBuilder);
        }
    }
}
