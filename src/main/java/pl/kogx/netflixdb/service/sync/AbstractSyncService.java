package pl.kogx.netflixdb.service.sync;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    public boolean isRunning() {
        return this.execute;
    }

    public void stop() {
        this.execute = false;
    }

    public void sync() {
        this.execute = true;
        long time = System.currentTimeMillis();
        log.info("Starting sync");
        try {
            Tuple<Long, Long> countTotal = doSync();
            log.info("Sync complete, syncedCount={}, failedCount={}, took {} millis",
                countTotal.v1(), countTotal.v2(), System.currentTimeMillis() - time);
        } catch (InterruptedException e) {
            log.info("Sync interrupted");
        }
    }

    public abstract void syncMovie(long id);

    protected abstract Tuple<Long, Long> doSync() throws InterruptedException;

    protected void checkIfNotInterrupted() throws InterruptedException {
        if (!execute) {
            throw new InterruptedException();
        }
    }

    protected final <T> T filterByBestTitleDistance(String title, List<T> films, Function<T, String> titleSupplier) {
        T best = null;
        int bestDist = Integer.MAX_VALUE;
        for (T res : films) {
            int dist = new LevenshteinDistance().apply(title, titleSupplier.apply(res));
            if (dist < bestDist) {
                best = res;
                bestDist = dist;
            }
        }
        return bestDist <= title.length() / 3 ? best : null;
    }
}
