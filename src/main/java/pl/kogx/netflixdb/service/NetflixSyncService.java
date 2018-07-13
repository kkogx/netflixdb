package pl.kogx.netflixdb.service;

import com.sun.xml.internal.ws.util.CompletedFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NetflixSyncService {

    private static final Logger log = LoggerFactory.getLogger(NetflixSyncService.class);

    private boolean scheduled = false;

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    @Async
    public CompletableFuture<Integer> syncMovies() {
        int syncedCount = 0;


        return CompletableFuture.completedFuture(syncedCount);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if(scheduled) {
            syncMovies();
        }
    }
}
