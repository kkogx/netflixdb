package pl.kogx.netflixdb.service.omdbsync;

import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.collect.Tuple;
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Service
public class OmdbSyncService {

    private static final Logger log = LoggerFactory.getLogger(OmdbSyncService.class);

    private static final NumberFormat NUMBER_FORMAT_US = NumberFormat.getInstance(Locale.US);

    private static final int PAGE_SIZE = 50;

    private static final String OMDB_UNAVAILABLE_TAG = "N/A";

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
        Tuple<Long, Long> countTotal = Tuple.tuple(0L, 0L);
        long time = System.currentTimeMillis();
        log.info("Starting sync");
        List<String> keys = new ArrayList<>(genreByIdMap.keySet());
        Collections.shuffle(keys);
        for (String genreId : keys) {
            Tuple<Integer, Integer> count = syncByGenre(genreId.trim(), genreByIdMap.get(genreId).trim());
            countTotal = Tuple.tuple(countTotal.v1() + count.v1(), countTotal.v2() + count.v2());
        }
        log.info("Sync complete, syncedCount={}, failedCount={}, took {} millis",
            countTotal.v1(), countTotal.v2(), System.currentTimeMillis() - time);
    }

    private Tuple<Integer, Integer> syncByGenre(String genreId, String genreName) {
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

        return Tuple.tuple(syncedCount, failedCount);
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
        video.setImdbRating(toFloat(omdbVideo.getImdbRating()));
        video.setImdbVotes(toLong(omdbVideo.getImdbVotes()));
        video.setMetascore(omdbVideo.getMetascore());
        video.setTomatoRating(toFloat(omdbVideo.getTomatoRating()));
        video.setTomatoUserRating(toFloat(omdbVideo.getTomatoUserRating()));
        video.setImdbID(omdbVideo.getImdbID());
        videoService.updateVideo(video);
        return true;
    }

    private static Float toFloat(String value) {
        return StringUtils.isEmpty(value) ? 0f : value.equalsIgnoreCase(OMDB_UNAVAILABLE_TAG) ? 0f : Float.valueOf(value);
    }

    private static Long toLong(String value) {
        if (StringUtils.isEmpty(value) || value.equalsIgnoreCase(OMDB_UNAVAILABLE_TAG)) {
            return 0L;
        }
        try {
            return NUMBER_FORMAT_US.parse(value).longValue();
        } catch (RuntimeException | ParseException e) {
            log.warn("Cant parse {} as long", value);
        }
        return 0L;
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
