package pl.kogx.netflixdb.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.NetflixdbApp;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;

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
        netflixSyncService.sync();
    }


    @Test
    public void testIndividual() {
        netflixSyncService.syncMovie(207856L);
    }
}
