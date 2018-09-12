package pl.kogx.netflixdb.service.netflixsync;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.sync.AbstractSyncService;
import pl.kogx.netflixdb.service.util.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NetflixSyncService extends AbstractSyncService {

    private final NetflixRequestBuilder requestBuilder;

    private final RestTemplate shaktiRestTemplate;

    @Autowired
    public NetflixSyncService(VideoService videoService, NetflixRequestBuilder requestBuilder, ApplicationProperties applicationProperties, RestTemplate shaktiRestTemplate) {
        super(videoService, applicationProperties);
        this.requestBuilder = requestBuilder;
        this.shaktiRestTemplate = shaktiRestTemplate;
    }

    @Override
    public Tuple<Long, Long> doSync() throws InterruptedException {
        Date timestamp = new Date();
        Tuple<Long, Long> countTotal = new Tuple(0l, 0l);
        try {
            for (Map.Entry<String, String> genreById : genreByIdMap.entrySet()) {
                int count = syncByGenre(genreById.getKey().trim(), genreById.getValue().trim());
                countTotal = Tuple.tuple(countTotal.v1() + count, 0l);
            }
        } catch (JsonObject.JsonUnmarshallException e) {
            log.error("Unable to process the response, API has changed?", e);
        }
        videoService.deleteByTimestampBefore(timestamp);
        return countTotal;
    }

    private void requestCooldown() {
        int rnd = new Random().nextInt(1000);
        try {
            Thread.sleep(applicationProperties.getNetflixSync().getRequestSleepMillis() + rnd);
        } catch (InterruptedException ignore) {
            log.warn("Exception when thread sleep", ignore);
        }
    }

    private int syncByGenre(String genreId, String genreName) throws JsonObject.JsonUnmarshallException, InterruptedException {
        log.info("Fetching by genre id={}, name={}", genreId, genreName);
        final int BLOCK_SIZE = applicationProperties.getNetflixSync().getRequestBlockSize();
        int from = 0, countTotal = 0, count;
        int to = BLOCK_SIZE;
        do {
            count = syncByGenre(genreId, genreName, from, to);
            from = to;
            to += BLOCK_SIZE;
            if (count > 0) {
                countTotal += count;
                log.info("Fetched {} titles", countTotal);
            }
            requestCooldown();
        } while (count > 0);
        if (countTotal == 0) {
            log.warn("No titles fetched for genre id={}, name={}", genreId, genreName);
        }
        return countTotal;
    }

    private int syncByGenre(String genreId, String genre, int from, int to) throws JsonObject.JsonUnmarshallException, InterruptedException {

        int count = 0;

        HttpEntity<String> request = requestBuilder.body(genreId, from, to).build();

        // Invoke Shakti endpoint
        ResponseEntity<String> response = shaktiRestTemplate.exchange(applicationProperties.getNetflixSync().getShaktiUrl(),
            HttpMethod.POST, request, String.class);

        // Process the result
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONArray videos = JsonPath.read(response.getBody(), "$..videos.*");
            for (Object video : videos) {
                super.checkIfNotInterrupted();
                JsonObject json = new JsonObject(video);
                Long id = json.get("summary").get("value").getLong("id");
                VideoDTO videoDTO = Optional.ofNullable(videoService.findById(id)).orElse(new VideoDTO());
                videoDTO.setTimestamp(new Date());
                videoDTO.setId(id);
                videoDTO.setType(json.get("summary").get("value").getString("type"));
                videoDTO.setOriginal(json.get("summary").get("value").getBool("isOriginal"));
                videoDTO.setTitle(json.get("title").getString("value"));
                videoDTO.setReleaseYear(json.get("releaseYear").getInt("value"));
                Collection<Long> genreIds = new ArrayList<>(videoDTO.getGenreIds());
                genreIds.add(Long.valueOf(genreId));
                videoDTO.setGenreIds(genreIds);
                videoDTO.setGenre(genre);
                videoDTO.setBoxart(json.get("boxarts").get("_260x146").get("jpg").get("value").getString("url"));
                videoService.updateVideo(videoDTO);
                count += 1;
            }
        } else {
            log.warn("Invalid HttpStatus retrieved when fetching by genre, status={}", response.getStatusCode());
        }

        return count;
    }

    @Override
    public void syncMovie(long id) {

        HttpEntity<String> request = requestBuilder.body(id).build();

        // Invoke Shakti endpoint
        ResponseEntity<String> response = shaktiRestTemplate.exchange(applicationProperties.getNetflixSync().getShaktiUrl(),
            HttpMethod.POST, request, String.class);

        // Process the result
        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONArray videos = JsonPath.read(response.getBody(), "$.." + id);
                VideoDTO videoDTO = new VideoDTO();
                videoDTO.setTimestamp(new Date());
                videoDTO.setId(id);
                JsonObject json = new JsonObject(videos.get(0));
                videoDTO.setType(json.get("summary").get("value").getString("type"));
                videoDTO.setOriginal(json.get("summary").get("value").getBool("isOriginal"));
                videoDTO.setTitle(json.get("title").getString("value"));
                videoDTO.setReleaseYear(json.get("releaseYear").getInt("value"));
                videoDTO.setGenreId(null);
                videoDTO.setGenre(null);
                videoDTO.setBoxart(json.get("boxarts").get("_260x146").get("jpg").get("value").getString("url"));
                videoService.updateVideo(videoDTO);
            } else {
                log.warn("Invalid HttpStatus retrieved when fetching by genre, status={}", response.getStatusCode());
            }
        } catch (JsonObject.JsonUnmarshallException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        log.info("Delete all");
        videoService.deleteAll();
    }
}
