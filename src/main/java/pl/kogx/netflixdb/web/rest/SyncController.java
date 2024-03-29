package pl.kogx.netflixdb.web.rest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.security.AuthoritiesConstants;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;
import pl.kogx.netflixdb.service.omdbsync.OmdbSyncService;
import pl.kogx.netflixdb.service.sync.AbstractSyncService;
import pl.kogx.netflixdb.service.sync.AllowedByIdPolicy;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing syncAll service.
 */
@RestController
@RequestMapping("/api")
public class SyncController {

    private final Logger log = LoggerFactory.getLogger(SyncController.class);

    private final NetflixSyncService netflixSyncService;

    private final OmdbSyncService omdbSyncService;

    private final FwebSyncService fwebSyncService;

    private final VideoService videoService;

    public SyncController(NetflixSyncService netflixSyncService, OmdbSyncService omdbSyncService, FwebSyncService fwebSyncService, VideoService videoService) {
        this.netflixSyncService = netflixSyncService;
        this.omdbSyncService = omdbSyncService;
        this.fwebSyncService = fwebSyncService;
        this.videoService = videoService;
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

    @DeleteMapping("/sync/all")
    @Secured(AuthoritiesConstants.ADMIN)
    public void delete() {
        netflixSyncService.deleteAll();
    }

    @PostMapping("/sync/{type}/{id}")
    @Secured(AuthoritiesConstants.ADMIN)
    public void sync(@PathVariable("type") String type, @PathVariable("id") Long id) {
        log.info("all type={} id={}", type, id);
        Optional<AbstractSyncService> service = getServiceForType(type);
        service.ifPresent(abstractSyncService -> abstractSyncService.doSync(id));
    }

    @Async
    @PostMapping("/sync/all")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void sync() {
        log.info("all");
        netflixSyncService.deleteAll();
        this.doSync(AllowedByIdPolicy.ALL_ALLOWED);
    }

    @Async
    @PostMapping("/sync/new")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void syncNew() {
        log.info("new");
        Set<Long> ids = new HashSet<>(videoService.getAllIds());
        this.doSync(id -> !ids.contains(id));
    }

    private void doSync(AllowedByIdPolicy policy) {
        netflixSyncService.syncAll(policy);
        omdbSyncService.syncAll(policy);
        fwebSyncService.syncAll(policy);
    }

    @Async
    @PostMapping("/sync_empty/all")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void syncEmpty() {
        log.info("sync_empty");
        videoService.findByEmptyRating().stream().map(Video::getId).forEach(id -> {
            netflixSyncService.sync(id);
            omdbSyncService.sync(id);
            fwebSyncService.sync(id);
        });
    }

    @Async
    @PostMapping("/sync/{type}/all")
    //@Secured(AuthoritiesConstants.ADMIN)
    public void sync(@PathVariable("type") String type) {
        log.info("all type={}", type);
        Optional<AbstractSyncService> service = getServiceForType(type);
        service.ifPresent(AbstractSyncService::syncAll);
    }

    @PostMapping("/sync/{type}/stop")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> stop(@PathVariable("type") String type) {
        log.info("stop " + type);
        Optional<AbstractSyncService> service = getServiceForType(type);
        return stop(service);
    }

    @PostMapping("/sync/{type}/status")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> status(@PathVariable("type") String type) {
        log.info("status " + type);
        Optional<AbstractSyncService> service = getServiceForType(type);
        return status(service);
    }

    private ResponseEntity<String> stop(Optional<AbstractSyncService> optionalService) {
        optionalService.ifPresent(AbstractSyncService::stop);
        return status(optionalService);//test
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
