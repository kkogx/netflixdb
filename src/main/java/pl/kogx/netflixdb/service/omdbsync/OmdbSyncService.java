package pl.kogx.netflixdb.service.omdbsync;

import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yamj.api.common.exception.ApiExceptionType;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class OmdbSyncService {

    private static final Logger log = LoggerFactory.getLogger(OmdbSyncService.class);

    private static final int PAGE_SIZE = 50;

    private final VideoService videoService;

    private final ApplicationProperties applicationProperties;

    private final Map<String, String> genreByIdMap;

    private final RestTemplate shaktiRestTemplate;

    private final OmdbApi omdbApi;

    private boolean scheduled = false;

    @Autowired
    public OmdbSyncService(VideoService videoService, ApplicationProperties applicationProperties, RestTemplateBuilder restTemplateBuilder) {
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
        this.shaktiRestTemplate = restTemplateBuilder.build();
        this.genreByIdMap = ApplicationProperties.getGenreByIdMap(applicationProperties);
        this.omdbApi = new OmdbApi(applicationProperties.getOmdbSync().getApiKey());
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void syncMovies() {
        long time = System.currentTimeMillis();
        log.info("Starting sync");
        List<String> keys = new ArrayList<>(genreByIdMap.keySet());
        Collections.shuffle(keys);
        for (String genreId : keys) {
            syncByGenre(genreId.trim(), genreByIdMap.get(genreId).trim());
        }
        log.info("Sync complete, took {} millis", System.currentTimeMillis() - time);
    }

    private void syncByGenre(String genreId, String genreName) {
        log.info("OMDB Fetching by genre id={}, name={} ...", genreId, genreName);

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
                    if (syncedCount % 100 == 0) {
                        log.info("OMDB fetched {} videos", syncedCount);
                    }
                } else {
                    failedCount += 1;
                }
            }
            pageNum += 1;
        } while (!page.isLast());

        log.info("OMDB Fetched {} videos, failed to sync {}", syncedCount, failedCount);
    }

    private boolean syncVideo(VideoDTO video) {
        OmdbVideoFull omdbVideo = null;
        try {
            omdbVideo = tryFindVideo(video);
        } catch (OMDBException e) {
            if (e.getExceptionType() != ApiExceptionType.ID_NOT_FOUND) {
                log.warn("Exception type={}, code={}", e.getExceptionType(), e.getResponseCode());
            }
        }
        if (omdbVideo == null) {
            return false;
        }
        video.setOmdbAvailable(true);
        video.setImdbRating(omdbVideo.getImdbRating());
        video.setImdbVotes(omdbVideo.getImdbVotes());
        video.setMetascore(omdbVideo.getMetascore());
        video.setTomatoRating(omdbVideo.getTomatoRating());
        video.setTomatoUserRating(omdbVideo.getTomatoUserRating());
        video.setImdbID(omdbVideo.getImdbID());
        videoService.updateVideo(video);
        return true;
    }

    private OmdbVideoFull tryFindVideo(VideoDTO video) throws OMDBException {
        OmdbBuilder omdbBuilder = new OmdbBuilder();
        if (StringUtils.isEmpty(video.getImdbID())) {
            omdbBuilder.setTitle(video.getTitle()).setYear(video.getReleaseYear());
        } else {
            omdbBuilder.setImdbId(video.getImdbID());
        }
        omdbBuilder.setTomatoes(true);
        return omdbApi.getInfo(omdbBuilder.build());
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if (scheduled) {
            syncMovies();
        }
    }
}
