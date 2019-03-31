package pl.kogx.netflixdb.config;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Properties specific to Netflixdb.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String aliasByTitle;

    public String getAliasByTitle() {
        return aliasByTitle;
    }

    public void setAliasByTitle(String aliasByTitle) {
        this.aliasByTitle = aliasByTitle;
    }

    public final NetflixSync netflixSync = new NetflixSync();

    public final OmdbSync omdbSync = new OmdbSync();

    public final Sendgrid sendgrid = new Sendgrid();

    public final FwebSync fwebSync = new FwebSync();

    public final Przelewy24 przelewy24 = new Przelewy24();

    public Przelewy24 getPrzelewy24() {
        return przelewy24;
    }

    public FwebSync getFwebSync() {
        return fwebSync;
    }

    public NetflixSync getNetflixSync() {
        return netflixSync;
    }

    public OmdbSync getOmdbSync() {
        return omdbSync;
    }

    public Sendgrid getSendgrid() {
        return sendgrid;
    }

    public static class Przelewy24 {

        private String host;

        private String crc;

        private String merchantId;

        private String statusUrl;

        public String getStatusUrl() {
            return statusUrl;
        }

        public void setStatusUrl(String statusUrl) {
            this.statusUrl = statusUrl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getCrc() {
            return crc;
        }

        public void setCrc(String crc) {
            this.crc = crc;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }
    }

    public static class FwebSync {

        private Boolean forceQuerySearch;

        public Boolean getForceQuerySearch() {
            return forceQuerySearch;
        }

        public void setForceQuerySearch(Boolean forceQuerySearch) {
            this.forceQuerySearch = forceQuerySearch;
        }
    }

    public static class OmdbSync {

        private String apiKey;

        private Boolean forceQuerySearch;

        public Boolean getForceQuerySearch() {
            return forceQuerySearch;
        }

        public void setForceQuerySearch(Boolean forceQuerySearch) {
            this.forceQuerySearch = forceQuerySearch;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public static class Sendgrid {

        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public static class NetflixSync {

        private ProxySettings proxySettings;

        private String sessionCookie;

        private String shaktiUrl;

        private String filmGenreById;

        private String showGenreById;

        private int requestSleepMillis;

        private int requestBlockSize;

        public String getFilmGenreById() {
            return filmGenreById;
        }

        public void setFilmGenreById(String filmGenreById) {
            this.filmGenreById = filmGenreById;
        }

        public String getShowGenreById() {
            return showGenreById;
        }

        public void setShowGenreById(String showGenreById) {
            this.showGenreById = showGenreById;
        }

        public ProxySettings getProxySettings() {
            return proxySettings;
        }

        public void setProxySettings(ProxySettings proxySettings) {
            this.proxySettings = proxySettings;
        }

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

        public String getSessionCookie() {
            return sessionCookie;
        }

        public void setSessionCookie(String sessionCookie) {
            this.sessionCookie = sessionCookie;
        }

        public static class ProxySettings {

            private boolean enabled;

            private String host;

            private short port;

            private String user;

            private String password;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public short getPort() {
                return port;
            }

            public void setPort(short port) {
                this.port = port;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }
        }
    }

    public static Map<String, String> getFilmGenreByIdMap(ApplicationProperties applicationProperties) {
        return split(applicationProperties.getNetflixSync().getFilmGenreById());
    }

    public static Map<String, String> getShowGenreByIdMap(ApplicationProperties applicationProperties) {
        return split(applicationProperties.getNetflixSync().getShowGenreById());
    }

    public static Map<String, String> getAliasByTitleMap(ApplicationProperties applicationProperties) {
        return split(applicationProperties.getAliasByTitle());
    }

    public static Map<String, String> getGenreByIdMap(ApplicationProperties applicationProperties) {
        Map<String, String> result = new HashMap<>();
        result.putAll(getFilmGenreByIdMap(applicationProperties));
        result.putAll(getShowGenreByIdMap(applicationProperties));
        return result;
    }

    private static Map<String, String> split(String keySepValue) {
        if (StringUtils.isEmpty(keySepValue)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(Splitter.on(",").withKeyValueSeparator("=").split(keySepValue));
    }
}
