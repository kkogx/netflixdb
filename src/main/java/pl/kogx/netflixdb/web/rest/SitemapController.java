package pl.kogx.netflixdb.web.rest;

import org.elasticsearch.common.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
public class SitemapController {

    @Inject
    public SitemapController() {

    }

    @RequestMapping(path = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String create() {
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream("sitemap.xml");
        return new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));

    }
}
