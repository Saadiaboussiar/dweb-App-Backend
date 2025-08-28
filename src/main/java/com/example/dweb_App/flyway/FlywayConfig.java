package com.example.dweb_App.flyway;


import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    private final DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Flyway flyway() {
        try {
            logger.info("Configuring Flyway with custom DataSource...");
            Flyway flyway = Flyway.configure()
                    .dataSource(this.dataSource)
                    .locations("classpath:db/migration")
                    .schemas("public")
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load();

            logger.info("Starting Flyway migration...");
            flyway.migrate();
            logger.info("Flyway migration completed successfully.");

            return flyway;
        } catch (Exception e) {
            logger.error("Flyway configuration failed: " + e.getMessage(), e);
            throw e;
        }
    }
}