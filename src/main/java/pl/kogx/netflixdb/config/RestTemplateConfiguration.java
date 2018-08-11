package pl.kogx.netflixdb.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    private final Logger log = LoggerFactory.getLogger(RestTemplateConfiguration.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, ApplicationProperties applicationProperties) {
        ApplicationProperties.NetflixSync.ProxySettings proxySettings = applicationProperties.getNetflixSync().getProxySettings();
        if (proxySettings != null && proxySettings.isEnabled()) {
            log.info("Proxy enabled host={} port={} user={}", proxySettings.getHost(), proxySettings.getUser(), proxySettings.getPort());
            Credentials credentials = new UsernamePasswordCredentials(proxySettings.getUser(), proxySettings.getPassword());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            AuthScope authScope = new AuthScope(proxySettings.getHost(), proxySettings.getPort());
            credsProvider.setCredentials(authScope, credentials);
            HttpClient httpClient = HttpClientBuilder.create().setProxy(new HttpHost(proxySettings.getHost(), proxySettings.getPort())).setDefaultCredentialsProvider(credsProvider).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            restTemplateBuilder = restTemplateBuilder.requestFactory(() -> requestFactory);
        }
        return restTemplateBuilder.build();
    }
}
