package com.lnf.sentinel;

import com.lnf.sentinel.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


/**
 * Base class for integration tests. Spins up a real PostgreSQL via Testcontainers,
 * lets Flyway build the schema + seed data, and rolls back each test's writes.
 *
 * <p>Seed data available in every test (from V1__init_schema.sql):
 * tenants ACME=1, GLOBEX=2, INITECH=3; users ops.lead=1, eng1=2, eng2=3, support1=4.
 *
 * <p>Requires a running Docker daemon.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
public abstract class AbstractIntegrationTest {

    protected static final long ACME = 1L;
    protected static final long GLOBEX = 2L;
    protected static final long ENG1 = 2L;

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }
}
