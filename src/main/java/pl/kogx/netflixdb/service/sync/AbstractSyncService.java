package pl.kogx.netflixdb.service.sync;

import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;

import java.util.Map;

public abstract class AbstractSyncService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final VideoService videoService;

    protected final ApplicationProperties applicationProperties;

    protected final Map<String, String> genreByIdMap;

    private volatile boolean execute = false;

    @Autowired
    public AbstractSyncService(VideoService videoService, ApplicationProperties applicationProperties) {
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
        this.genreByIdMap = ApplicationProperties.getGenreByIdMap(applicationProperties);
    }

    public void stop() {
        this.execute = false;
    }

    public void sync() {
        this.execute = true;
        long time = System.currentTimeMillis();
        log.info("Starting sync");
        try {
            Tuple<Long, Long> countTotal = syncInternal();
            log.info("Sync complete, syncedCount={}, failedCount={}, took {} millis",
                countTotal.v1(), countTotal.v2(), System.currentTimeMillis() - time);
        } catch (InterruptedException e) {
            log.info("Sync interrupted");
        }
    }

    protected abstract Tuple<Long, Long> syncInternal() throws InterruptedException;

    protected void checkIfNotInterrupted() throws InterruptedException {
        if (!execute) {
            throw new InterruptedException();
        }
    }
}
