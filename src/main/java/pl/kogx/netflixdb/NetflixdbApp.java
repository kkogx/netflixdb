package pl.kogx.netflixdb;

import io.github.jhipster.config.JHipsterConstants;
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
import pl.kogx.netflixdb.service.fwebsync.FwebSyncService;
import pl.kogx.netflixdb.service.netflixsync.NetflixSyncService;
import pl.kogx.netflixdb.service.omdbsync.OmdbSyncService;
import pl.kogx.netflixdb.service.util.VideoDiffService;
import pl.kogx.netflixdb.service.util.VideoExportService;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class NetflixdbApp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NetflixdbApp.class);

    private final NetflixSyncService netflixSyncService;

    private final OmdbSyncService omdbSyncService;

    private final FwebSyncService fwebSyncService;

    private final VideoExportService exportService;

    private final VideoDiffService diffService;

    private final Environment env;

    private boolean scheduled = false;

    @Autowired
    public NetflixdbApp(Environment env, NetflixSyncService netflixSyncService, OmdbSyncService omdbSyncService, FwebSyncService fwebSyncService, VideoExportService exportService, VideoDiffService diffService) {
        this.env = env;
        this.netflixSyncService = netflixSyncService;
        this.omdbSyncService = omdbSyncService;
        this.fwebSyncService = fwebSyncService;
        this.exportService = exportService;
        this.diffService = diffService;
    }

    /**
     * Initializes netflixdb.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
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
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            hostAddress,
            env.getProperty("server.port"),
            env.getActiveProfiles());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application was executed with args: " + Arrays.toString(args.getSourceArgs()));
        for (String opt : args.getSourceArgs()) {
            switch (opt) {
                case "scheduled": {
                    scheduled = getBooleanOptionValue(args, opt);
                    break;
                }
                case "sync": {
                    doSync(null);
                    break;
                }
                case "export": {
                    exportService.exportVideos("exported_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".json");
                    break;
                }
                case "diff": {
                    Long id = getLongOptionValue(args, opt);
                    //          doSync(id);
                    diffService.diff(id);
                }
            }
        }
    }

    private void doSync(Long id) {
        netflixSyncService.sync(id);
        omdbSyncService.sync(id);
        fwebSyncService.sync(id);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10 /* 10 minutes */)
    public void syncMoviesScheduled() {
        if (scheduled) {
            doSync(null);
        }
    }

    private boolean getBooleanOptionValue(ApplicationArguments args, String opt) {
        return (args.getOptionValues(opt) == null || args.getOptionValues(opt).isEmpty()) ? false : Boolean.valueOf(args.getOptionValues(opt).get(0));
    }

    private Long getLongOptionValue(ApplicationArguments args, String opt) {
        return (args.getOptionValues(opt) == null || args.getOptionValues(opt).isEmpty()) ? null : Long.valueOf(args.getOptionValues(opt).get(0));
    }
}
