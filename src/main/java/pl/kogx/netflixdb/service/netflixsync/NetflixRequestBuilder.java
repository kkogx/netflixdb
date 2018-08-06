package pl.kogx.netflixdb.service.netflixsync;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import pl.kogx.netflixdb.config.ApplicationProperties;

import java.util.HashMap;
import java.util.Map;

@Component
class NetflixRequestBuilder {

    @Autowired
    private final ApplicationProperties applicationProperties;

    @Autowired
    private final VelocityEngine velocityEngine;

    private String body;

    private HttpHeaders headers;

    public NetflixRequestBuilder(ApplicationProperties applicationProperties, VelocityEngine velocityEngine) {
        this.applicationProperties = applicationProperties;
        this.velocityEngine = velocityEngine;
    }

    public HttpEntity<String> build() {
        headers();
        return new HttpEntity<>(body, headers);
    }

    public NetflixRequestBuilder body(long id) {
        Map<String, Object> context = new HashMap<>();
        context.put("id", id);
        this.body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "netflix/quote_by_id.json.vm", context);
        return this;
    }

    public NetflixRequestBuilder body(String genreId, int from, int to) {
        Map<String, Object> context = new HashMap<>();
        context.put("genreid", genreId);
        context.put("from", from);
        context.put("to", to);
        this.body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "netflix/quote_by_genre_az.json.vm", context);
        return this;
    }

    private NetflixRequestBuilder headers() {
        this.headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
        headers.add(HttpHeaders.COOKIE, applicationProperties.getNetflixSync().getSessionCookie());
        headers.add(HttpHeaders.ACCEPT, "application/json, text/javascript, */*");
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en;q=0.5");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add("DNT", "1");
        headers.add(HttpHeaders.REFERER, "https://www.netflix.com");
        return this;
    }
}
