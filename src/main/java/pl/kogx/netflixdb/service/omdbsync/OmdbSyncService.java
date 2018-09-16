package pl.kogx.netflixdb.service.omdbsync;

import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoBasic;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yamj.api.common.exception.ApiExceptionType;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.sync.AbstractRepoSyncService;
import pl.kogx.netflixdb.service.util.GenreResolver;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@Service
public class OmdbSyncService extends AbstractRepoSyncService {

    private static final NumberFormat NUMBER_FORMAT_US = NumberFormat.getInstance(Locale.US);

    private static final String OMDB_UNAVAILABLE_TAG = "N/A";

    private final OmdbApi omdbApi;

    @Autowired
    public OmdbSyncService(VideoService videoService, ApplicationProperties applicationProperties, GenreResolver genreResolver) {
        super(videoService, applicationProperties, genreResolver);
        this.omdbApi = new OmdbApi(applicationProperties.getOmdbSync().getApiKey());
    }

    @Override
    protected boolean doSyncVideo(VideoDTO video, String title) {
        OmdbVideoFull omdbVideo = null;
        try {
            omdbVideo = tryFindVideo(video, title);
        } catch (OMDBException e) {
            if (e.getExceptionType() != ApiExceptionType.ID_NOT_FOUND) {
                log.warn("Exception type={}, code={}, video={}", e.getExceptionType(), e.getResponseCode(), video);
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

    private Long toLong(String value) {
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

    private static boolean isMovie(String type) {
        return "movie".equalsIgnoreCase(type);
    }

    private OmdbVideoFull tryFindVideo(VideoDTO video, String title) throws OMDBException {
        OmdbVideoBasic result;
        if (StringUtils.isEmpty(video.getImdbID()) || applicationProperties.getOmdbSync().getForceQuerySearch()) {
            if (isMovie(video.getType())) {
                // first try to find by title and year
                result = tryFindVideo(video.getType(), title, video.getReleaseYear());
                if (result == null) {
                    result = tryFindVideo(video.getType(), title, null);
                }
            } else {
                // show dates tend to be fucked up in Netflix, prefer search by title
                result = tryFindVideo(video.getType(), video.getTitle(), null);
                if (result == null) {
                    result = tryFindVideo(video.getType(), video.getTitle(), video.getReleaseYear());
                }
            }

        } else {
            // find by imdb
            result = doTryFindVideo(video.getImdbID());
        }
        if (result == null) {
            return null;
        }
        return omdbApi.getInfo(new OmdbBuilder().setImdbId(result.getImdbID()).build());
    }

    private OmdbBuilder createOmdbBuilder() {
        OmdbBuilder omdbBuilder = new OmdbBuilder();
        omdbBuilder.setTomatoes(true);
        return omdbBuilder;
    }

    private OmdbVideoBasic tryFindVideo(String type, String title, Integer releaseYear) throws OMDBException {
        OmdbVideoBasic omdbVideoBasic = doTryFindVideo(type,  title, null, releaseYear);
        if (omdbVideoBasic == null) {
            // try with searchTerm instead of title
            omdbVideoBasic = doTryFindVideo(type,  null, title, releaseYear);
        }
        return omdbVideoBasic;
    }

    private OmdbVideoBasic doTryFindVideo(String imdbID) throws OMDBException {
        OmdbBuilder omdbBuilder = createOmdbBuilder();
        omdbBuilder.setImdbId(imdbID);
        return omdbApi.getInfo(omdbBuilder.build());
    }

    private OmdbVideoBasic doTryFindVideo(String type, String title, String searchTerm, Integer releaseYear) throws OMDBException {
        String keyTitle = null;
        OmdbBuilder omdbBuilder = createOmdbBuilder();
        if (StringUtils.isNotEmpty(title)) {
            keyTitle = title;
            omdbBuilder.setTitle(title);
        } else if (StringUtils.isNotEmpty(searchTerm)) {
            keyTitle = searchTerm;
            omdbBuilder.setSearchTerm(searchTerm);
        }
        if (releaseYear != null) {
            omdbBuilder.setYear(releaseYear);
        }
        if (isMovie(type)) {
            omdbBuilder.setTypeMovie();
        }
        List<OmdbVideoBasic> result = omdbApi.search(omdbBuilder.build()).getResults();
        if (result == null || result.isEmpty()) {
            return null;
        }
        if (keyTitle == null) {
            return result.get(0);
        } else {
            return filterByBestTitleDistance(keyTitle, result, OmdbVideoBasic::getTitle);
        }
    }
}
