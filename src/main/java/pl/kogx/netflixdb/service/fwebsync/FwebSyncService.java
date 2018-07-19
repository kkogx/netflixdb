package pl.kogx.netflixdb.service.fwebsync;

import info.talacha.filmweb.api.FilmwebApi;
import info.talacha.filmweb.connection.FilmwebException;
import info.talacha.filmweb.models.Item;
import info.talacha.filmweb.search.models.FilmSearchResult;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FwebSyncService {

    private static final Logger log = LoggerFactory.getLogger(FwebSyncService.class);

    private static final int PAGE_SIZE = 50;

    private final VideoService videoService;

    private final ApplicationProperties applicationProperties;

    private final Map<String, String> genreByIdMap;

    private FilmwebApi fwebApi = new FilmwebApi();

    private boolean scheduled = false;

    @Autowired
    public FwebSyncService(VideoService videoService, ApplicationProperties applicationProperties) {
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
        this.genreByIdMap = ApplicationProperties.getGenreByIdMap(applicationProperties);
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void syncMovies() {
        Tuple<Long, Long> countTotal = Tuple.tuple(0L, 0L);
        long time = System.currentTimeMillis();
        log.info("Starting filmweb sync");
        List<String> keys = new ArrayList<>(genreByIdMap.keySet());
        Collections.shuffle(keys);
        for (String genreId : keys) {
            Tuple<Integer, Integer> count = syncByGenre(genreId.trim(), genreByIdMap.get(genreId).trim());
            countTotal = Tuple.tuple(countTotal.v1() + count.v1(), countTotal.v2() + count.v2());
        }
        log.info("Sync complete, syncedCount={}, failedCount={}, took {} millis",
            countTotal.v1(), countTotal.v2(), System.currentTimeMillis() - time);
    }

    public void syncMovie(Long id) {
        log.info("Starting filmweb sync");
        boolean result = false;
        VideoDTO video = videoService.findById(id);
        if (video != null) {
            result = syncVideo(video);
        }
        log.info("Sync complete, result=", result);
    }

    private Tuple<Integer, Integer> syncByGenre(String genreId, String genreName) {
        log.info("FWEB Fetching by genre id={}, name={} ...", genreId, genreName);

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
                        log.info("FWEB Fetched {} videos", syncedCount);
                    }
                } else {
                    failedCount += 1;
                }
            }
            pageNum += 1;
        } while (!page.isLast());

        log.info("FWEB Fetched {} videos, failed to sync {}", syncedCount, failedCount);

        return Tuple.tuple(syncedCount, failedCount);
    }

    private boolean syncVideo(VideoDTO video) {
        Item fwebVideoData = null;
        try {
            fwebVideoData = tryFindVideo(video);
        } catch (FilmwebException e) {
            log.warn("Exception code={}, msg={}, label={}", e.getCode(), e.getFilmwebMessage(), e.getLabel());
        }
        if (fwebVideoData == null) {
            return false;
        }
        video.setFwebAvailable(true);
        video.setFwebRating(fwebVideoData.getRate());
        video.setFwebVotes(Long.valueOf(fwebVideoData.getVotes()));
        video.setFwebID(fwebVideoData.getId());
        video.setFwebTitle(fwebVideoData.getPolishTitle());
        videoService.updateVideo(video);
        return true;
    }

    private static boolean isMovie(String type) {
        return "movie".equalsIgnoreCase(type);
    }

    private Item tryFindVideo(VideoDTO video) throws FilmwebException {
        Item result;
        if (video.getFwebID() == null || video.getFwebID() <= 0 || applicationProperties.getFwebSync().getForceQuerySearch()) {
            if (isMovie(video.getType())) {
                result = tryFindFilm(video.getTitle(), video.getReleaseYear());
            } else {
                result = tryFindSeries(video.getTitle(), video.getReleaseYear());
            }

        } else {
            // find by imdb
            result = fwebApi.getFilmData(video.getFwebID());
        }
        return result;
    }

    private Item tryFindSeries(String title, Integer releaseYear) throws FilmwebException {
        // show dates tend to be fucked up in Netflix, prefer search by title
        FilmSearchResult res = filterByTitleDistance(title, fwebApi.findFilm(title));
        if (res == null) {
            res = filterByTitleDistance(title, fwebApi.findFilm(title, releaseYear));
        }
        if (res == null) {
            return null;
        }
        return fwebApi.getFilmData(res.getId());
    }

    private Item tryFindFilm(String title, Integer releaseYear) throws FilmwebException {
        // with movies the approach is opposite, first try to find by title and year
        FilmSearchResult res = filterByTitleDistance(title, fwebApi.findFilm(title, releaseYear));
        if (res == null) {
            res = filterByTitleDistance(title, fwebApi.findFilm(title));
        }
        if (res == null) {
            return null;
        }
        return fwebApi.getFilmData(res.getId());
    }

    private FilmSearchResult filterByTitleDistance(String title, List<FilmSearchResult> films) {
        FilmSearchResult best = null;
        int bestDist = Integer.MAX_VALUE;
        for (FilmSearchResult res : films) {
            int dist = new LevenshteinDistance().apply(title, res.getTitle());
            if (dist < bestDist) {
                best = res;
                bestDist = dist;
            }
        }
        return bestDist < title.length() / 3 ? best : null;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if (scheduled) {
            syncMovies();
        }
    }
}
