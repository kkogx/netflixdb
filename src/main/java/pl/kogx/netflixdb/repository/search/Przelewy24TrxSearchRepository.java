package pl.kogx.netflixdb.repository.search;

import pl.kogx.netflixdb.domain.Przelewy24Trx;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Przelewy24Trx entity.
 */
public interface Przelewy24TrxSearchRepository extends ElasticsearchRepository<Przelewy24Trx, Long> {
}
