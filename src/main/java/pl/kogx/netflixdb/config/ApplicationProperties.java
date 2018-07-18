package pl.kogx.netflixdb.config;

import com.google.common.base.Splitter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * Properties specific to Netflixdb.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    public final NetflixSync netflixSync = new NetflixSync();

    public final OmdbSync omdbSync = new OmdbSync();

    public NetflixSync getNetflixSync() {
        return netflixSync;
    }

    public OmdbSync getOmdbSync() {
        return omdbSync;
    }

    public static class OmdbSync {

        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public static class NetflixSync {

        private String sessionCookie;

        private String shaktiUrl;

        private String genreById;

        private int requestSleepMillis;

        private int requestBlockSize;

        public int getRequestBlockSize() {
            return requestBlockSize;
        }

        public void setRequestBlockSize(int requestBlockSize) {
            this.requestBlockSize = requestBlockSize;
        }

        public int getRequestSleepMillis() {
            return requestSleepMillis;
        }

        public void setRequestSleepMillis(int requestSleepMillis) {
            this.requestSleepMillis = requestSleepMillis;
        }

        public String getShaktiUrl() {
            return shaktiUrl;
        }

        public void setShaktiUrl(String shaktiUrl) {
            this.shaktiUrl = shaktiUrl;
        }

        public String getGenreById() {
            return genreById;
        }

        public void setGenreById(String genreById) {
            this.genreById = genreById;
        }

        public String getSessionCookie() {
            return sessionCookie;
        }

        public void setSessionCookie(String sessionCookie) {
            this.sessionCookie = sessionCookie;
        }
    }

    public static Map<String, String> getGenreByIdMap(ApplicationProperties applicationProperties) {
        return Collections.unmodifiableMap(Splitter.on(",").withKeyValueSeparator("=")
            .split(applicationProperties.getNetflixSync().getGenreById()));
    }
}
