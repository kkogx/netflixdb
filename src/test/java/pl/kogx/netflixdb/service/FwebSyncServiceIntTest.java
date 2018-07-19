package pl.kogx.netflixdb.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.NetflixdbApp;
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;

/**
 * @see NetflixSyncService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetflixdbApp.class)
@Transactional
public class FwebSyncServiceIntTest {

    @Autowired
    private FwebSyncService fwebSyncService;

    @Before
    public void init() {
    }

    @Test
    public void test() {
        fwebSyncService.syncMovies();
    }

    @Test
    public void testIndividual() {
        fwebSyncService.syncMovie(70143836L);
    }
}
