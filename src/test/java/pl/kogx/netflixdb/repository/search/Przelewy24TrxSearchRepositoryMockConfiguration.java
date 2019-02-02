package pl.kogx.netflixdb.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of Przelewy24TrxSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class Przelewy24TrxSearchRepositoryMockConfiguration {

    @MockBean
    private Przelewy24TrxSearchRepository mockPrzelewy24TrxSearchRepository;

}
