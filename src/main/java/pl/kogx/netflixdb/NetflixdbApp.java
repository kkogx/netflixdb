package pl.kogx.netflixdb;

import io.github.jhipster.config.JHipsterConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.config.DefaultProfileUtil;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.service.VideoService;
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;
import pl.kogx.netflixdb.service.omdbsync.OmdbSyncService;
import pl.kogx.netflixdb.service.util.VideoDiffService;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class NetflixdbApp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NetflixdbApp.class);

    private final NetflixSyncService netflixSyncService;

    private final OmdbSyncService omdbSyncService;

    private final FwebSyncService fwebSyncService;

    private final VideoDiffService diffService;

    private final VideoService videoService;

    private final Environment env;

    private boolean scheduled = false;

    @Autowired
    public NetflixdbApp(Environment env, NetflixSyncService netflixSyncService, OmdbSyncService omdbSyncService, FwebSyncService fwebSyncService, VideoDiffService diffService, VideoService videoService) {
        this.env = env;
        this.netflixSyncService = netflixSyncService;
        this.omdbSyncService = omdbSyncService;
        this.fwebSyncService = fwebSyncService;
        this.diffService = diffService;
        this.videoService = videoService;
    }

    /**
     * Initializes netflixdb.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); //allows for security context inheritance in async methods
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NetflixdbApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

    private enum Options {
        DIFF, DIFF_TOP, EXPORT, SCHEDULED, SYNC, SYNC_EMPTY

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application was executed with args: " + Arrays.toString(args.getSourceArgs()));
        if (containsOption(args, Options.SCHEDULED)) {
            scheduled = getBooleanOptionValue(args, Options.SCHEDULED);
        }
        if (containsOption(args, Options.SYNC)) {
            List<Long> ids = getLongListOptionValue(args, Options.SYNC);
            doDelete(ids);
            doSync(ids);
        }
        if (containsOption(args, Options.SYNC_EMPTY)) {
            List<Long> ids = videoService.findByEmptyRating().stream().map(Video::getId).collect(Collectors.toList());
            doSync(ids);
        }
        if (containsOption(args, Options.EXPORT)) {
            diffService.exportVideos("exported_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".json");
        }
        if (containsOption(args, Options.DIFF)) {
            List<Long> ids = getLongListOptionValue(args, Options.DIFF);
            doDiff(ids);
        }
        if (containsOption(args, Options.DIFF_TOP)) {
            Integer count = getIntOptionValue(args, Options.DIFF_TOP);
            List<Long> ids = syncTop(count);
            diffService.diffTop(ids);
        }
    }

    private boolean containsOption(ApplicationArguments args, Options opt) {
        return args.getOptionNames().contains(opt.name().toLowerCase());
    }

    private List<Long> syncTop(int count) {
        log.info("Syncing top count=" + count);
        List<Long> ids = StreamSupport.stream(videoService.findAll().spliterator(), false)
            .map(Video::getId).collect(Collectors.toList()).subList(0, count);
        doSync(ids);
        return ids;
    }

    private void doDiff(List<Long> ids) {
        log.info("Diff ids=" + ids);
        if (ids.isEmpty()) {
            diffService.diffAll();
        } else {
            ids.forEach(diffService::diff);
        }
    }

    private void doSync(List<Long> ids) {
        log.info("Syncing ids size={} content={}", ids.size(), ids);
        if (ids.isEmpty()) {
            netflixSyncService.syncAll();
            omdbSyncService.syncAll();
            fwebSyncService.syncAll();
        } else {
            ids.forEach(id -> {
                netflixSyncService.sync(id);
                omdbSyncService.sync(id);
                fwebSyncService.sync(id);
            });
        }
    }

    private void doDelete(List<Long> ids) {
        log.info("Deleting ids=" + ids);
        if (ids.isEmpty()) {
            videoService.deleteAll();
        } else {
            ids.forEach(videoService::delete);
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if (scheduled) {
            doSync(null);
        }
    }

    private boolean getBooleanOptionValue(ApplicationArguments args, Options optVo) {
        String opt = optVo.name().toLowerCase();
        return (args.getOptionValues(opt) == null || args.getOptionValues(opt).isEmpty()) ? false : Boolean.valueOf(args.getOptionValues(opt).get(0));
    }

    private List<Long> getLongListOptionValue(ApplicationArguments args, Options optVo) {
        String opt = optVo.name().toLowerCase();
        return (args.getOptionValues(opt) == null || args.getOptionValues(opt).isEmpty()) ?
            Collections.emptyList() : args.getOptionValues(opt).stream().map(Long::valueOf).collect(Collectors.toList());
    }

    private Integer getIntOptionValue(ApplicationArguments args, Options optVo) {
        String opt = optVo.name().toLowerCase();
        return (args.getOptionValues(opt) == null || args.getOptionValues(opt).isEmpty()) ? null : Integer.valueOf(args.getOptionValues(opt).get(0));
    }
}
