package pl.kogx.netflixdb.service.netflixsync;

import com.google.common.base.Splitter;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.JsonObject;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Service
public class NetflixSyncService {

    private static final Logger log = LoggerFactory.getLogger(NetflixSyncService.class);

    private final VideoService videoService;

    private final NetflixRequestBuilder requestBuilder;

    private final ApplicationProperties applicationProperties;

    private final Map<String, String> genreByIdMap;

    private final RestTemplate shaktiRestTemplate;

    private boolean scheduled = false;

    @Autowired
    public NetflixSyncService(VideoService videoService, NetflixRequestBuilder requestBuilder, ApplicationProperties applicationProperties, RestTemplateBuilder restTemplateBuilder) {
        this.requestBuilder = requestBuilder;
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
        this.shaktiRestTemplate = restTemplateBuilder.build();
        this.genreByIdMap = Collections.unmodifiableMap(Splitter.on(",").withKeyValueSeparator("=")
            .split(applicationProperties.getNetflixSync().getGenreById()));
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void syncMovies() {
        try {
            for (Map.Entry<String, String> genreById : genreByIdMap.entrySet()) {
                syncByGenre(genreById.getKey().trim(), genreById.getValue().trim());
            }
        } catch (JsonObject.JsonUnmarshallException e) {
            log.error("Unable to process the response, API has changed?", e);
        }
    }

    private void requestCooldown() {
        int rnd = new Random().nextInt(1000);
        try {
            Thread.sleep(applicationProperties.getNetflixSync().getRequestSleepMillis() + rnd);
        } catch (InterruptedException ignore) {
            log.warn("Exception when thread sleep", ignore);
        }
    }

    private void syncByGenre(String genreId, String genreName) throws JsonObject.JsonUnmarshallException {
        log.info("Fetching by genre id={}, name={}", genreId, genreName);
        int from = 0, countTotal = 0, count;
        int to = applicationProperties.getNetflixSync().getRequestBlockSize();
        do {
            count = syncByGenre(genreId, from, to);
            countTotal += count;
            log.info("Fetched {} titles", countTotal);
            from = to;
            to += applicationProperties.getNetflixSync().getRequestBlockSize();
            requestCooldown();
        } while (count > 0);
        if (countTotal == 0) {
            log.warn("No titles fetched for genre id={}, name={}", genreId, genreName);
        }
    }

    private int syncByGenre(String genreId, int from, int to) throws JsonObject.JsonUnmarshallException {

        int count = 0;

        HttpEntity<String> request = requestBuilder.body(genreId, from, to).build();

        // Invoke Shakti endpoint
        ResponseEntity<String> response = shaktiRestTemplate.exchange(applicationProperties.getNetflixSync().getShaktiUrl(), HttpMethod.POST, request, String.class);
        log.info("Response: length={}", response.getBody().length());

        // Process the result
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONArray videos = JsonPath.read(response.getBody(), "$..videos.*");
            for (Object video : videos) {
                VideoDTO videoDTO = new VideoDTO();
                JsonObject json = new JsonObject(video);
                videoDTO.setId(json.get("summary").get("value").getLong("id"));
                videoDTO.setTitle(json.get("title").getString("value"));
                videoDTO.setReleaseYear(json.get("releaseYear").getInt("value"));
                videoService.updateVideo(videoDTO);
                count += 1;
            }
        } else {
            log.warn("Invalid HttpStaus retrieved when fetching by genre, status={}", response.getStatusCode());
        }

        return count;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if (scheduled) {
            syncMovies();
        }
    }
}
