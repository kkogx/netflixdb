package pl.kogx.netflixdb.service.netflixsync;

import com.google.common.base.Splitter;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
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

import java.util.Map;

@Service
public class NetflixSyncService {

    private static final Logger log = LoggerFactory.getLogger(NetflixSyncService.class);

    @Autowired
    private final VideoService videoService;

    @Autowired
    private final NetflixRequestBuilder requestBuilder;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate shaktiRestTemplate;

    private boolean scheduled = false;

    public NetflixSyncService(VideoService videoService, NetflixRequestBuilder requestBuilder, ApplicationProperties applicationProperties) {
        this.requestBuilder = requestBuilder;
        this.videoService = videoService;
        this.applicationProperties = applicationProperties;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void syncMovies() {
        Map<String, String> genreByIdMap = Splitter.on(",").withKeyValueSeparator("=").split(applicationProperties.getNetflixSync().getGenreById());
        for (Map.Entry<String, String> genreById : genreByIdMap.entrySet()) {
            syncByGenre(genreById.getKey(), genreById.getValue());
            try {
                Thread.sleep(applicationProperties.getNetflixSync().getRequestSleepMillis());
            } catch (InterruptedException ignore) {
            }
        }

    }

    private void syncByGenre(String genreId, String genreName) {
        log.info("Fetching by genre id={} name={}", genreId, genreName);
        HttpEntity<String> request = requestBuilder.body(genreId).build();
        ResponseEntity<String> response = shaktiRestTemplate.exchange(applicationProperties.getNetflixSync().getShaktiUrl(), HttpMethod.POST, request, String.class);
        log.info("Response: length={}", response.getBody().length());

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONArray videos = JsonPath.read(response.getBody(), "$..videos.*");
            for (Object video : videos) {
                VideoDTO videoDTO = new VideoDTO();
                JsonObject videoJson = new JsonObject(video);
                videoDTO.setId(videoJson.get("summary").get("value").getLong("id"));
                videoDTO.setTitle(videoJson.get("title").getString("value"));
                videoService.updateVideo(videoDTO);
            }
        } else {
            log.warn("Invalid HttpStaus retrieved when fetching by genre, status={}", response.getStatusCode());
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if(scheduled) {
            syncMovies();
        }
    }

    @Bean
    public RestTemplate shaktiRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
