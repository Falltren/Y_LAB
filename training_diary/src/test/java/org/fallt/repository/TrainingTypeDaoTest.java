package org.fallt.repository;

import org.fallt.config.ContainerEnvironment;
import org.fallt.model.TrainingType;
import org.fallt.repository.impl.TrainingTypeDaoImpl;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class TrainingTypeDaoTest {

    private TrainingTypeDao trainingTypeDao;

    @Test
    void testFindByType_thenGetOptionalType() throws Exception {
        try (PostgreSQLContainer<?> container = ContainerEnvironment.postgreSQLContainer) {
            container.start();
            trainingTypeDao = new TrainingTypeDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
            TrainingType trainingType = trainingTypeDao.findByType("лыжи").orElse(new TrainingType());
            assertThat(trainingType.getId()).isEqualTo(3);
            container.stop();
        }
    }

    @Test
    void testSave_thenAddNewTrainingType() throws Exception {
        try (PostgreSQLContainer<?> container = ContainerEnvironment.postgreSQLContainer) {
            container.start();
            Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
            trainingTypeDao = new TrainingTypeDaoImpl(connection);
            connection.setAutoCommit(false);
            TrainingType trainingType = new TrainingType();
            trainingType.setType("коньки");
            trainingTypeDao.save(trainingType);
            TrainingType createdTrainingType = trainingTypeDao.findByType("коньки").orElse(new TrainingType());
            assertThat(createdTrainingType).isEqualTo(trainingType);
            assertThat(createdTrainingType.getId()).isEqualTo(4);
            container.stop();
        }
    }

}
