package pl.kogx.netflixdb.service;

import com.sun.xml.internal.ws.util.CompletedFuture;
import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import pl.kogx.netflixdb.config.ApplicationProperties;

import java.util.concurrent.CompletableFuture;

@Service
public class NetflixSyncService {

    private static final Logger log = LoggerFactory.getLogger(NetflixSyncService.class);

    @Autowired
    private final ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate shaktiRestTemplate;

    private boolean scheduled = false;

    public NetflixSyncService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void syncMovies() {
        String genres="0,\"to\":1";
        String rmax="5";
        String base = "[[\"newarrivals\",{\"from\":"+genres+"},{\"from\":0,\"to\":"+rmax+"},[\"title\",\"availability\"]],[\"newarrivals\",{\"from\":"+genres+"},{\"from\":0,\"to\":"+rmax+"},\"boxarts\",\"_342x192\",\"jpg\"]]";
        String data="{\"paths\":"+base+"}";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
        headers.add(HttpHeaders.COOKIE, applicationProperties.getNetflixSync().getSessionCookie());
        headers.add(HttpHeaders.ACCEPT, "application/json, text/javascript, */*");
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en;q=0.5");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add("DNT", "1");
        headers.add(HttpHeaders.REFERER, "https://www.netflix.com");
        HttpEntity<String> request = new HttpEntity<>(data, headers);

        String url = String.format("https://www.netflix.com/api/shakti/%s/pathEvaluator?withSize=true&materialize=true&model=harris",
            applicationProperties.getNetflixSync().getShaktiBuildId());

        ResponseEntity<String> response = shaktiRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        System.out.println(response);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if(scheduled) {
            syncMovies();
        }
    }

    @Bean
    public RestTemplate shaktiRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
