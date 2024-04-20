package org.fallt.config;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ContainerEnvironment {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"))
                    .withUsername("testUser")
                    .withPassword("testSecret")
                    .withDatabaseName("testDatabase")
                    .withInitScript("init_script_test.sql");

}
