package pl.kogx.netflixdb.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.NetflixdbApp;
import pl.kogx.netflixdb.config.Constants;
import pl.kogx.netflixdb.domain.User;
import pl.kogx.netflixdb.repository.UserRepository;
import pl.kogx.netflixdb.repository.search.UserSearchRepository;
import pl.kogx.netflixdb.service.dto.UserDTO;
import pl.kogx.netflixdb.service.util.RandomUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @see NetflixSyncService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetflixdbApp.class)
@Transactional
public class NetflixSyncServiceIntTest {

    @Autowired
    private NetflixSyncService netflixSyncService;

    @Before
    public void init() {
    }

    @Test
    public void test() {
        System.out.println("Test!");
        netflixSyncService.syncMovies();
    }
}
