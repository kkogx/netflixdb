package pl.kogx.netflixdb.service.sync;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.GenreResolver;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractRepoSyncService extends AbstractSyncService {

    private static final int PAGE_SIZE = 50;

    private static final String NO_SYNC_KEY = "SKIP";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Map<String, String> aliasByTitle;

    @Autowired
    public AbstractRepoSyncService(VideoService videoService, ApplicationProperties applicationProperties, GenreResolver genreResolver) {
        super(videoService, applicationProperties, genreResolver);
        this.aliasByTitle = ApplicationProperties.getAliasByTitleMap(applicationProperties);
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

    @Override
    public void doSync(long id) {
        log.info("Starting sync by id=" + id);
        boolean result = false;
        VideoDTO video = videoService.findById(id);
        if (video != null) {
            try {
                result = syncVideo(video);
            } catch (InterruptedException ignored) {
            }
        }
        log.info("Sync complete, result={}", result);
    }

    private boolean syncVideo(VideoDTO video) throws InterruptedException {
        super.checkIfNotInterrupted();
        String title = aliasByTitle.getOrDefault(video.getTitle(), video.getTitle());
        if(NO_SYNC_KEY.equalsIgnoreCase(title)) {
            return false;
        }
        return doSyncVideo(video, title);
    }

    protected abstract boolean doSyncVideo(VideoDTO video, String title);

    @Override
    protected Tuple<Integer, Integer> syncByGenre(String genreId, String genreName) throws InterruptedException {
        log.info("Fetching by genre id={}, name={} ...", genreId, genreName);

        int syncedCount = 0;
        int failedCount = 0;

        int pageNum = 0;
        Page<VideoDTO> page;
        do {
            page = videoService.findByGenreId(Long.valueOf(genreId), PageRequest.of(pageNum, PAGE_SIZE));
            for (VideoDTO video : page) {
                boolean result = syncVideo(video);
                if (result) {
                    syncedCount += 1;
                    if (syncedCount % 25 == 0) {
                        log.info("Fetched {} videos", syncedCount);
                    }
                } else {
                    failedCount += 1;
                }
            }
            pageNum += 1;
        } while (!page.isLast());

        log.info("Fetched {} videos, failed to sync {}", syncedCount, failedCount);

        return Tuple.tuple(syncedCount, failedCount);
    }
}
