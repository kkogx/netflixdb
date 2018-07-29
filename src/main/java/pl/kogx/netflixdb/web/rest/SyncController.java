package pl.kogx.netflixdb.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kogx.netflixdb.security.AuthoritiesConstants;
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;
import pl.kogx.netflixdb.service.omdbsync.OmdbSyncService;

/**
 * REST controller for managing sync service.
 */
@RestController
@RequestMapping("/api")
public class SyncController {

    private final Logger log = LoggerFactory.getLogger(SyncController.class);

    private final NetflixSyncService netflixSyncService;

    private final OmdbSyncService omdbSyncService;

    private final FwebSyncService fwebSyncService;

    public SyncController(NetflixSyncService netflixSyncService, OmdbSyncService omdbSyncService, FwebSyncService fwebSyncService) {
        this.netflixSyncService = netflixSyncService;
        this.omdbSyncService = omdbSyncService;
        this.fwebSyncService = fwebSyncService;
    }

    @PostMapping("/sync/netflix/all")
    @Async
    @Secured(AuthoritiesConstants.ADMIN)
    public Runnable syncNetflix() {
        log.debug("Netflix sync");
        return () -> netflixSyncService.sync();
    }

    @PostMapping("/sync/imdb/all")
    @Async
    @Secured(AuthoritiesConstants.ADMIN)
    public Runnable syncImdb() {
        log.debug("Imdb sync");
        return () -> omdbSyncService.sync();
    }

    @PostMapping("/sync/filmweb/all")
    @Async
    @Secured(AuthoritiesConstants.ADMIN)
    public Runnable syncFilmweb() {
        log.debug("Netflix sync");
        return () -> fwebSyncService.sync();
    }
}
