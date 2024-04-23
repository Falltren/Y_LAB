package org.fallt.repository;

import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.fallt.repository.impl.TrainingDaoImpl;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class TrainingDaoTest {

    private TrainingDao trainingDao;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"))
            .withUsername("testUser")
            .withPassword("testSecret")
            .withDatabaseName("testDatabase")
            .withInitScript("init_script_test.sql");

    @Test
    void testFindAllUserTrainings_thenGetListTrainings() throws Exception {
        trainingDao = new TrainingDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
        List<Training> trainings = trainingDao.findAllUserTrainings(1L);
        assertThat(trainings).hasSize(3);
        assertThat(trainings.get(0).getDuration()).isEqualTo(60);
    }

    @Test
    void testFindUserTrainingsByDay_thenGetListTrainingsByDay() throws Exception {
        trainingDao = new TrainingDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
        LocalDate day = LocalDate.of(2023, 10, 11);
        List<Training> trainings = trainingDao.findUserTrainingsByDay(1L, day);
        assertThat(trainings).hasSize(2);
        assertThat(trainings.get(0).getDuration()).isEqualTo(50);
        assertThat(trainings.get(1).getDuration()).isEqualTo(120);
    }

    @Test
    void testFindTrainingById_thenGetOptionalTraining() throws Exception {
        trainingDao = new TrainingDaoImpl(DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword()));
        LocalDate day = LocalDate.of(2023, 10, 11);
        String type = "плавание";
        Training training = trainingDao.findTrainingById(1L, type, day).orElse(new Training());
        assertThat(training.getId()).isEqualTo(2L);
        assertThat(training.getDuration()).isEqualTo(50);
        assertThat(training.getSpentCalories()).isEqualTo(250);
    }

    @Test
    void testSave_thenAddNewTraining() throws Exception {
        Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        trainingDao = new TrainingDaoImpl(connection);
        connection.setAutoCommit(false);
        User user = createUser();
        TrainingType trainingType = new TrainingType(2, "дартс");
        Training training = new Training(4L, trainingType, LocalDate.of(2024, 1, 1), 110, 150, "just text", user);
        trainingDao.save(training);
        Training createdTraining = trainingDao.findAllUserTrainings(1L).get(3);
        assertThat(createdTraining.getDescription()).isEqualTo("just text");
        assertThat(createdTraining.getDuration()).isEqualTo(110);
        assertThat(createdTraining.getSpentCalories()).isEqualTo(150);
    }

    @Test
    void testDelete_thenDeleteOneTraining() throws Exception {
        Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        trainingDao = new TrainingDaoImpl(connection);
        connection.setAutoCommit(false);
        trainingDao.delete(2L);
        List<Training> trainings = trainingDao.findAllUserTrainings(1L);
        assertThat(trainings).hasSize(2);
        assertThat(trainings.get(0).getId()).isEqualTo(1L);
        assertThat(trainings.get(1).getId()).isEqualTo(3L);
    }

    @Test
    void testUpdate_thenChangeTrainingData() throws Exception {
        Connection connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        trainingDao = new TrainingDaoImpl(connection);
        connection.setAutoCommit(false);
        User user = createUser();
        TrainingType trainingType = new TrainingType(2, "дартс");
        Training changedTraining = new Training(1L, trainingType, LocalDate.of(2020, 8, 8), 200, 500, "", user);
        trainingDao.update(changedTraining);
        Training actual = trainingDao.findTrainingById(1L, "дартс", LocalDate.of(2020, 8, 8)).orElse(new Training());
        assertThat(actual.getDuration()).isEqualTo(200);
        assertThat(actual.getSpentCalories()).isEqualTo(500);
    }

    private User createUser() {
        return new User(
                1L,
                Role.ROLE_USER,
                "John",
                "pass1",
                LocalDateTime.of(LocalDate.of(2024, 4, 10), LocalTime.of(0, 0, 0)),
                new HashSet<>());
    }
}
