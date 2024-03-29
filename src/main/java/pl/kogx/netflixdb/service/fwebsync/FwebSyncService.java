package pl.kogx.netflixdb.service.fwebsync;

import info.talacha.filmweb.api.FilmwebApi;
import info.talacha.filmweb.connection.FilmwebException;
import info.talacha.filmweb.models.Film;
import info.talacha.filmweb.models.Item;
import info.talacha.filmweb.search.models.FilmSearchResult;
import info.talacha.filmweb.search.models.ItemSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.sync.AbstractRepoSyncService;
import pl.kogx.netflixdb.service.util.GenreResolver;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FwebSyncService extends AbstractRepoSyncService {

    private static final Logger log = LoggerFactory.getLogger(FwebSyncService.class);

    private final FilmwebApi fwebApi = new FilmwebApi();

    @Autowired
    public FwebSyncService(VideoService videoService, ApplicationProperties applicationProperties, GenreResolver genreResolver) {
        super(videoService, applicationProperties, genreResolver);
    }

    @Override
    protected boolean doSyncVideo(VideoDTO video, String title) {
        Item fwebVideoData = null;
        try {
            fwebVideoData = tryFindVideo(video, title);
        } catch (FilmwebException e) {
            log.warn("Exception code={}, msg={}, label={}, video={}", e.getCode(), e.getFilmwebMessage(), e.getLabel(), video);
        } catch (RuntimeException e) {
            log.warn("Exception msg={}, label={}, video={}", e.getMessage(), video);
        }
        if (fwebVideoData == null) {
            return false;
        }
        video.setFwebAvailable(true);
        video.setFwebRating(fwebVideoData.getRate());
        video.setFwebVotes(Long.valueOf(fwebVideoData.getVotes()));
        video.setFwebID(fwebVideoData.getId());
        video.setFwebTitle(fwebVideoData.getPolishTitle());
        video.setFwebPlot(fwebVideoData.getPlot());
        videoService.updateVideo(video);
        return true;
    }

    private static boolean isMovie(String type) {
        return "movie".equalsIgnoreCase(type);
    }

    private Item tryFindVideo(VideoDTO video, String title) throws FilmwebException {
        Item result;
        if (video.getFwebID() == null || video.getFwebID() <= 0 || applicationProperties.getFwebSync().getForceQuerySearch()) {
            if (isMovie(video.getType())) {
                result = tryFindFilm(title, video.getReleaseYear());
            } else {
                result = tryFindSeries(title, video.getReleaseYear());
            }

        } else {
            // find by fweb id
            result = fwebApi.getFilmData(video.getFwebID());
        }
        return result;
    }

    private Item tryFindSeries(String title, Integer releaseYear) throws FilmwebException {
        // show dates tend to be fucked up in Netflix, prefer search by title without year
        ItemSearchResult res = filterByBestTitleDistance(title, findSeries(title), ItemSearchResult::getTitle);
        if (res == null) {
            res = filterByBestTitleDistance(title, findSeries(title, releaseYear), ItemSearchResult::getTitle);
        }
        if (res == null) {
            return null;
        }
        return tryGetFilmData(res);
    }

    private Item tryFindFilm(String title, Integer releaseYear) throws FilmwebException {
        // with movies the approach is opposite, first try to find by title and year
        List<ItemSearchResult> filmsByTitleAndYear = filterByYear(fwebApi.findFilm(title, releaseYear), releaseYear);

        ItemSearchResult res = filmsByTitleAndYear.stream().filter(i -> title.equals(i.getTitle())).findFirst().orElse(null);
        if (res == null) {
            res = filmsByTitleAndYear.stream().filter(i -> title.equals(i.getPolishTitle())).findFirst().orElse(null);
        }
        if (res == null) {
            filterByBestTitleDistance(title, filmsByTitleAndYear, ItemSearchResult::getTitle);
        }
        if (res == null) {
            List<ItemSearchResult> filmsByTitle = filterByYear(fwebApi.findFilm(title), releaseYear);
            res = filterByBestTitleDistance(title, filmsByTitle, ItemSearchResult::getTitle);
        }
        if (res == null) {
            return null;
        }
        return tryGetFilmData(res);
    }

    private Film tryGetFilmData(ItemSearchResult res) throws FilmwebException {
        try {
            return fwebApi.getFilmData(res.getId());
        } catch (IndexOutOfBoundsException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<ItemSearchResult> findSeries(String title) {
        return Stream.concat(fwebApi.findSeries(title).stream(), fwebApi.findProgram(title).stream()).collect(Collectors.toList());
    }

    private List<ItemSearchResult> findSeries(String title, Integer releaseYear) {
        return Stream.concat(fwebApi.findSeries(title, releaseYear).stream(), fwebApi.findProgram(title, releaseYear).stream()).collect(Collectors.toList());
    }

    private List<ItemSearchResult> filterByYear(List<FilmSearchResult> list, int releaseYear) {
        return list.stream().filter(f -> releaseYear == f.getYear()).collect(Collectors.toList());
    }
}
