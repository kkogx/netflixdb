package pl.kogx.netflixdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Netflixdb.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    public final NetflixSync netflixSync = new NetflixSync();

    public NetflixSync getNetflixSync() {
        return netflixSync;
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
}
