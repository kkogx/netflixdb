package pl.kogx.netflixdb.service.util;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.service.VideoService;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for exporting videos.
 */
@Service
@Transactional
public class VideoDiffService {

    private final Logger log = LoggerFactory.getLogger(VideoDiffService.class);

    private final VideoService videoService;

    private final VideoExportService exportService;

    public VideoDiffService(VideoService videoService, VideoExportService exportService) {
        this.videoService = videoService;
        this.exportService = exportService;
    }


    public void diffTop(List<Long> ids) throws IOException {
        log.info("diffTop ids=" + ids.toString());
        doDiff(ids.stream().map(id -> videoService.findOne(id).get())::iterator);
    }

    public void diff(Long id) throws IOException {
        this.doDiff(Collections.singleton(videoService.findOne(id).get()));
    }

    public void diffAll() throws IOException {
        this.doDiff(videoService.findAll());
    }

    private void doDiff(Iterable<Video> videos) throws IOException {
        log.info("doDiff");
        String fileToImport = getMostRecentJson("exported_");
        Map<Long, Video> truthMap = exportService.importVideos(fileToImport).stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        int count = 0;
        log.info("Comparing videos...");
        for (Video video : videos) {
            Video truth = truthMap.get(video.getId());
            if (truth != null) {
                System.out.print(".");
                String truthFwebTitle = StringUtils.defaultString(truth.getFwebTitle());
                String videoFwebTitle = StringUtils.defaultString(video.getFwebTitle());
                String truthImdbId = StringUtils.defaultString(truth.getImdbID());
                String videoImdbId = StringUtils.defaultString(video.getImdbID());
                long truthFwebId = Optional.ofNullable(truth.getFwebID()).orElse(0l);
                long videoFwebId = Optional.ofNullable(video.getFwebID()).orElse(0l);
                if (!truthFwebTitle.equalsIgnoreCase(videoFwebTitle) || !truthImdbId.equals(videoImdbId)
                    || truthFwebId != videoFwebId) {
                    log.info("Diff found id=" + truth.getId());
                    log.info("\t Truth=" + truth.toShortString());
                    log.info("\t Found=" + video.toShortString());
                }
            }
            ++count;
        }
        System.out.println();
        log.info("Compared videos count=" + count);
    }

    private String getMostRecentJson(String prefix) {
        List<File> files = Arrays.stream(new File(".").listFiles(file -> file.getName().startsWith(prefix))).sorted().collect(Collectors.toList());
        log.info("Comparing with " + files.get(0).getName());
        return files.get(0).getName();
    }

    public void exportVideos(String filename) throws IOException {
        exportService.exportVideos(filename);
    }
}
