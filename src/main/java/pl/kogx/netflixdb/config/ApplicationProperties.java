package pl.kogx.netflixdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

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

        private String shaktiBuildId;

        public String getSessionCookie() {
            return sessionCookie;
        }

        public void setSessionCookie(String sessionCookie) {
            this.sessionCookie = sessionCookie;
        }

        public String getShaktiBuildId() {
            return shaktiBuildId;
        }

        public void setShaktiBuildId(String shaktiBuildId) {
            this.shaktiBuildId = shaktiBuildId;
        }
    }
}
