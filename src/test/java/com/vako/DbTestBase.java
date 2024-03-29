package com.vako;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ContextConfiguration(classes = {WebConfig.class, FirebaseConfig.class})
public abstract class DbTestBase {

    protected static final String UID_1 = "1rvwaZpH31MP58GplDTIkb7ygh43";
    protected static final String UID_2 = "I3l2ACPuQWd0tHNhuc1kNFGVUCU2";

    protected static final String UID_3 = "ECpSUB6mHtPdvUWmTGyEZwcAkKi2";

    protected static final String API_PATH = "/api/v1";
    protected static String token;

    @Container
    private static final MSSQLServerContainer<?> SQLSERVER_CONTAINER = new MSSQLServerContainer<>(
            "mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", SQLSERVER_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", SQLSERVER_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", SQLSERVER_CONTAINER::getPassword);
    }

    static {
        SQLSERVER_CONTAINER.start();
    }
}
