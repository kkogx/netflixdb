package pl.kogx.netflixdb.service.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.repository.search.VideoSearchRepository;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.StreamSupport;

/**
 * Service class for exporting videos.
 */
@Service
@Transactional
public class VideoExportService {

    private final Logger log = LoggerFactory.getLogger(VideoExportService.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final VideoSearchRepository videoSearchRepository;

    public VideoExportService(VideoSearchRepository videoSearchRepository) {
        this.videoSearchRepository = videoSearchRepository;
    }

    public void exportVideos(String filename) throws IOException {
        log.info("Exporting to " + filename);
        BufferedWriter out = new BufferedWriter(new FileWriter(filename, false));
        try {
            Iterable<Video> videos = videoSearchRepository.findAll(Sort.by("id"));
            Video[] array = StreamSupport.stream(videos.spliterator(), false).toArray(Video[]::new);
            gson.toJson(array, out);
            log.info("Export complete: " + array.length);
        } finally {
            out.close();
        }

        log.info("Counting...");
        this.importVideos(filename);
    }

    public Collection<Video> importVideos(String filename) throws IOException {
        log.info("Importing from " + filename);
        BufferedReader in = new BufferedReader(new FileReader(filename));
        try {
            Collection<Video> videos = Arrays.asList(gson.fromJson(in, Video[].class));
            log.info("Imported count=" + videos.size());
            return videos;
        } finally {
            in.close();
        }
    }
}
