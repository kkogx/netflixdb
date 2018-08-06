package pl.kogx.netflixdb.web.rest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kogx.netflixdb.security.AuthoritiesConstants;
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;
import pl.kogx.netflixdb.service.omdbsync.OmdbSyncService;
import pl.kogx.netflixdb.service.sync.AbstractSyncService;

import java.util.Optional;

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

    private Optional<AbstractSyncService> getServiceForType(String type) {
        if (StringUtils.isNotEmpty(type)) {
            switch (type) {
                case "netflix":
                    return Optional.of(netflixSyncService);
                case "imdb":
                case "omdb":
                    return Optional.of(omdbSyncService);
                case "fweb":
                case "filmweb":
                    return Optional.of(fwebSyncService);
            }
        }
        return Optional.empty();
    }

    @PostMapping("/sync/{type}/{id}")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void sync(@PathVariable("type") String type, @PathVariable("id") Long id) {
        log.info("all");
        Optional<AbstractSyncService> service = getServiceForType(type);
        service.ifPresent(abstractSyncService -> abstractSyncService.syncMovie(id));
    }

    @Async
    @PostMapping("/sync/{type}/all")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void sync(@PathVariable("type") String type) {
        log.info("all");
        Optional<AbstractSyncService> service = getServiceForType(type);
        service.ifPresent(AbstractSyncService::sync);
    }

    @PostMapping("/sync/{type}/stop")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> stop(@PathVariable("type") String type) {
        log.info("stop");
        Optional<AbstractSyncService> service = getServiceForType(type);
        return stop(service);
    }

    @PostMapping("/sync/{type}/status")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> status(@PathVariable("type") String type) {
        log.info("status");
        Optional<AbstractSyncService> service = getServiceForType(type);
        return status(service);
    }

    private ResponseEntity<String> stop(Optional<AbstractSyncService> optionalService) {
        optionalService.ifPresent(AbstractSyncService::stop);
        return status(optionalService);
    }

    private ResponseEntity<String> status(Optional<AbstractSyncService> optionalService) {
        if (optionalService.isPresent()) {
            AbstractSyncService service = optionalService.get();
            if (service.isRunning()) {
                return ResponseEntity.ok("running");
            }
            return ResponseEntity.ok("not running");
        }
        return ResponseEntity.notFound().build();
    }
}
