package pl.kogx.netflixdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import pl.kogx.netflixdb.service.NetflixSyncService;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class NetflixdbApp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NetflixdbApp.class);

    private final NetflixSyncService netflixSyncService;

    private final Environment env;

    @Autowired
    public NetflixdbApp(Environment env, NetflixSyncService netflixSyncService) {
        this.env = env;
        this.netflixSyncService = netflixSyncService;
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

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("Netflixdb-");
        executor.initialize();
        return executor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application was executed with args:");
        for(String opt: args.getOptionNames()) {
            log.info("{}={}", opt, args.getOptionValues(opt));
        }

        for(String opt : args.getOptionNames()) {
            switch(opt) {
                case "scheduled": {
                    netflixSyncService.setScheduled(getBooleanOptionValue(args, opt));
                    break;
                }
                case "sync": {
                    netflixSyncService.syncMovies();
                    break;
                }
            }
        }
    }

    private boolean getBooleanOptionValue(ApplicationArguments args, String opt) {
        return args.getOptionValues(opt).isEmpty() ? false : Boolean.valueOf(args.getOptionValues(opt).get(0));
    }
}
