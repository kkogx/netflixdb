package pl.kogx.netflixdb.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.kogx.netflixdb.domain.Video;

/**
 * Spring Data Elasticsearch repository for the Video entity.
 */
public interface VideoSearchRepository extends ElasticsearchRepository<Video, Long> {
}
