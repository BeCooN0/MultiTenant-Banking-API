package com.kote.banking.config;

import com.kote.banking.repository.TenantRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class FlywayConfig {

    private final DataSource dataSource;
    private final TenantRepository tenantRepository;


    @PostConstruct
    public void flywayConfig() {
         Flyway.configure()
                .dataSource(dataSource)
                .locations("/db/path/" + "db/migration")
                .schemas("public")
                .baselineOnMigrate(true)
                .load()
                .migrate();


        List<String> allIds = tenantRepository.findAllIds();
        for (String tenant : allIds) {
            log.info("Migrating schema: {}", tenant);
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations("/db/path/" + "db/migration")
                    .schemas(tenant)
                    .baselineOnMigrate(true)
                    .load()
                    .migrate();
        }
        log.info("flyway migration finished");
    }
}
