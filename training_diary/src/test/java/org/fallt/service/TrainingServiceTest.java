package org.fallt.service;

import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TrainingServiceTest {

    private TrainingService trainingService;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        trainingService = new TrainingService(userService);
    }

    @Test
    @DisplayName("Test add new training")
    void testAddTraining() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "бег", LocalDate.of(2023, 10, 20), 100, 300, "");
        assertThat(user.getTrainings()).hasSize(1);
        Training training = user.getTrainings().stream().findAny().get();
        assertThat(training.getDate()).isEqualTo(LocalDate.of(2023, 10, 20));
        assertThat(training.getDuration()).isEqualTo(100);
        assertThat(training.getSpentCalories()).isEqualTo(300);
        assertThat(training.getDescription()).isBlank();
    }

    @Test
    @DisplayName("Test add same training in one day")
    void testAddSameTraining() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "бег", LocalDate.now(), 100, 300, "");
        trainingService.addNewTraining(user, "бег", LocalDate.now(), 200, 400, "");
        assertThat(user.getTrainings()).hasSize(1);
    }

    @Test
    @DisplayName("Watch user`s training")
    void testWatchTrainings() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), 60, 400, "");
        trainingService.addNewTraining(user, "бег", LocalDate.of(2023, 10, 15), 50, 300, "");
        trainingService.addNewTraining(user, "йога", LocalDate.of(2024, 2, 10), 120, 250, "");
        List<Training> actual = trainingService.getTrainings(user);
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).getDate()).isEqualTo(LocalDate.of(2023, 10, 15));
        assertThat(actual.get(1).getDate()).isEqualTo(LocalDate.of(2024, 2, 10));
        assertThat(actual.get(2).getDate()).isEqualTo(LocalDate.of(2024, 3, 22));
    }

    @Test
    @DisplayName("Watch user`s training for the day")
    void testWatchTrainingsForDay() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), 60, 400, "");
        trainingService.addNewTraining(user, "бег", LocalDate.of(2023, 10, 15), 50, 300, "");
        trainingService.addNewTraining(user, "йога", LocalDate.of(2024, 2, 10), 120, 250, "");
        List<Training> actual = trainingService.getTrainings(user, "22/03/2024");
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getDuration()).isEqualTo(60);
        assertThat(actual.get(0).getSpentCalories()).isEqualTo(400);
    }

    @Test
    @DisplayName("Edit training")
    void testEditTraining() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), 60, 400, "");
        trainingService.addNewTraining(user, "йога", LocalDate.of(2024, 3, 22), 60, 400, "");
        Map<String, String> newValues = Map.of(
                "1", "бег",
                "2", "15/02/2024",
                "3", "120",
                "4", "350",
                "5", "Новое описание");
        trainingService.editTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), newValues);
        Training editedTraining = user.getTrainings().stream().filter(t -> t.getType().getType().equals("бег")).findFirst().get();
        assertThat(editedTraining.getType()).isEqualTo(new TrainingType("бег"));
        assertThat(editedTraining.getDate()).isEqualTo(LocalDate.of(2024, 2, 15));
        assertThat(editedTraining.getDuration()).isEqualTo(120);
        assertThat(editedTraining.getSpentCalories()).isEqualTo(350);
        assertThat(editedTraining.getDescription()).isEqualTo("Новое описание");
    }

    @Test
    @DisplayName("Delete training")
    void testDeleteTraining() {
        User user = createUser();
        when(userService.getUserByName("John")).thenReturn(user);
        trainingService.addNewTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), 60, 400, "");
        trainingService.addNewTraining(user, "бег", LocalDate.of(2024, 3, 22), 50, 300, "");
        trainingService.addNewTraining(user, "йога", LocalDate.of(2024, 2, 10), 120, 250, "");
        trainingService.deleteTraining(user, "кроссфит", LocalDate.of(2024, 3, 22));
        Set<Training> actual = user.getTrainings();
        assertThat(actual).hasSize(2);
    }

    private User createUser() {
        return new User(Role.USER, "John", "123", LocalDateTime.now(), new HashSet<>());
    }
}
