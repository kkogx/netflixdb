package pl.kogx.netflixdb.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.kogx.netflixdb.domain.Video;

import java.util.Date;

/**
 * Spring Data Elasticsearch repository for the Video entity.
 */
public interface VideoSearchRepository extends ElasticsearchRepository<Video, Long> {

    Page<Video> findAllByGenreId(Long genreId, Pageable pageable);

    void deleteByTimestampBefore(Date timestamp);
}
