package pl.kogx.netflixdb.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.kogx.netflixdb.config.ApplicationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GenreResolver {

    private final Map<String, String> showGenreByIdMap;

    private final Map<String, String> filmGenreByIdMap;

    private final Map<String, String> genreByIdMap;

    @Autowired
    public GenreResolver(ApplicationProperties applicationProperties) {
        this.showGenreByIdMap = ApplicationProperties.getShowGenreByIdMap(applicationProperties);
        this.filmGenreByIdMap = ApplicationProperties.getFilmGenreByIdMap(applicationProperties);
        this.genreByIdMap = new HashMap<>();
        this.genreByIdMap.putAll(filmGenreByIdMap);
        this.genreByIdMap.putAll(showGenreByIdMap);
    }

    public Map<String, String> getFilmGenreByIdMap() {
        return Collections.unmodifiableMap(filmGenreByIdMap);
    }

    public Map<String, String> getShowGenreByIdMap() {
        return Collections.unmodifiableMap(showGenreByIdMap);
    }

    public Map<String, String> getGenreByIdMap() {
        return Collections.unmodifiableMap(genreByIdMap);
    }

    public String getGenreById(String genreId) {
        return genreByIdMap.get(genreId);
    }
}
