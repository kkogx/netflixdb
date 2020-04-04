package pl.kogx.netflixdb.service.netflixsync;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.kogx.netflixdb.config.ApplicationProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
class NetflixRequestBuilder {

    @Autowired
    private final ApplicationProperties applicationProperties;

    @Autowired
    private final VelocityEngine velocityEngine;

    private MultiValueMap<String, String> body;

    private HttpHeaders headers;

    public NetflixRequestBuilder(ApplicationProperties applicationProperties, VelocityEngine velocityEngine) {
        this.applicationProperties = applicationProperties;
        this.velocityEngine = velocityEngine;
    }

    public HttpEntity<MultiValueMap<String, String>> build() {
        headers();
        return new HttpEntity<>(body, headers);
    }

    public NetflixRequestBuilder body(long id) {
        Map<String, Object> context = new HashMap<>();
        context.put("id", id);
        String tmp = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "netflix/quote_by_id.json.vm", context);
        body = toValueMap(tmp);
        return this;
    }

    public NetflixRequestBuilder body(String genreId, int from, int to) {
        Map<String, Object> context = new HashMap<>();
        context.put("genreid", genreId);
        context.put("from", from);
        context.put("to", to);
        String tmp = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "netflix/quote_by_genre_az.form_w_auth.vm", context);
        body = toValueMap(tmp);
        return this;
    }

    private MultiValueMap<String, String> toValueMap(String tmp) {
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        Arrays.stream(tmp.split("\n")).forEach(s -> {
            int idx = s.indexOf(":");
            result.add(s.substring(0, idx).trim(), s.substring(idx + 1));
        });
        return result;
    }

    private NetflixRequestBuilder headers() {
        this.headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "PostmanRuntime/7.24.0");
        headers.add(HttpHeaders.COOKIE, applicationProperties.getNetflixSync().getSessionCookie());
        headers.add(HttpHeaders.ACCEPT, "application/json, text/javascript, */*");
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en;q=0.5");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.add("DNT", "1");
        headers.add(HttpHeaders.ORIGIN, "https://www.netflix.com");
        return this;
    }
}
