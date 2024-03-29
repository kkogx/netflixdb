package pl.kogx.netflixdb.repository.search;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.kogx.netflixdb.domain.Video;

/**
 * Spring Data Elasticsearch repository for the Video entity.
 */
public interface VideoSearchRepository extends ElasticsearchRepository<Video, Long> {

    @CacheEvict("genres")
    @Override
    Video save(Video entity);

    Page<Video> findByGenreIds(Long genreId, Pageable pageable);

    void deleteByTimestampBefore(long timestamp);

    Iterable<Video> findAllByTimestampBefore(long timestamp);

    long countByGenreIds(long genreId);

    Iterable<Video> findAll();
}

