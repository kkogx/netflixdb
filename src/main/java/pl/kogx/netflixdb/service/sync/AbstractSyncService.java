package pl.kogx.netflixdb.service.sync;

import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.util.GenreResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSyncService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final VideoService videoService;

    protected final GenreResolver genreResolver;

    protected final ApplicationProperties applicationProperties;

    private volatile boolean execute = false;

    @Autowired
    public AbstractSyncService(VideoService videoService, ApplicationProperties applicationProperties, GenreResolver genreResolver) {
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
        this.genreResolver = genreResolver;
    }

    public boolean isRunning() {
        return this.execute;
    }

    public void stop() {
        this.execute = false;
    }

    public void sync(Long id) {
        this.execute = true;
        doSync(id);
    }

    public void syncAll() {
        this.syncAll(AllowedByIdPolicy.ALL_ALLOWED);
    }

    public void syncAll(AllowedByIdPolicy allowedById) {
        this.execute = true;
        long time = System.currentTimeMillis();
        log.info("Starting syncAll");
        try {
            Tuple<Long, Long> countTotal = doSyncAll(allowedById);
            log.info("Sync complete, syncedCount={}, failedCount={}, took {} millis",
                countTotal.v1(), countTotal.v2(), System.currentTimeMillis() - time);
        } catch (InterruptedException e) {
            log.info("Sync interrupted");
        }
    }

    protected Tuple<Long, Long> doSyncAll(AllowedByIdPolicy allowedById) throws InterruptedException {
        Tuple<Long, Long> countTotal = Tuple.tuple(0L, 0L);
        List<String> keys = new ArrayList<>(genreResolver.getGenreByIdMap().keySet());
        Collections.shuffle(keys);
        for (String genreId : keys) {
            Tuple<Integer, Integer> count = syncByGenre(allowedById, genreId.trim(), genreResolver.getGenreById(genreId).trim());
            countTotal = Tuple.tuple(countTotal.v1() + count.v1(), countTotal.v2() + count.v2());
        }
        return countTotal;
    }

    protected abstract Tuple<Integer, Integer> syncByGenre(AllowedByIdPolicy allowedById, String genreId, String genreName) throws InterruptedException;

    public abstract void doSync(long id);

    protected void checkIfNotInterrupted() throws InterruptedException {
        if (!execute) {
            throw new InterruptedException();
        }
    }
}
