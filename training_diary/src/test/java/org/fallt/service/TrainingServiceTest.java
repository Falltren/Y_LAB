package org.fallt.service;

import org.fallt.model.Role;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.fallt.model.User;
import org.fallt.repository.TrainingDao;
import org.fallt.repository.TrainingTypeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingService trainingService;

    private UserService userService;

    private TrainingDao trainingDao;

    private TrainingTypeDao trainingTypeDao;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(UserService.class);
        trainingDao = Mockito.mock(TrainingDao.class);
        trainingTypeDao = Mockito.mock(TrainingTypeDao.class);
        trainingService = new TrainingService(trainingDao, trainingTypeDao);
    }

    @Test
    @DisplayName("Test add new training")
    void testAddTraining() {
        User user = createUser();
        LocalDate date = LocalDate.of(2023, 10, 20);
        TrainingType trainingType = new TrainingType(1, "бег");
        Training training = new Training(1L, trainingType, date, 100, 300, "", user);
        when(trainingDao.findTrainingById(1L, "бег", date)).thenReturn(Optional.empty());
        when(trainingTypeDao.findByType("бег")).thenReturn(Optional.of(trainingType));
        when(trainingDao.findAllUserTrainings(user.getId())).thenReturn(List.of(training));
        trainingService.addNewTraining(user, "бег", date, 100, 300, "");
        assertThat(trainingService.getTrainings(user)).hasSize(1);
        Training existedTraining = trainingService.getTrainings(user).get(0);
        assertThat(existedTraining.getDate()).isEqualTo(LocalDate.of(2023, 10, 20));
        assertThat(existedTraining.getDuration()).isEqualTo(100);
        assertThat(existedTraining.getSpentCalories()).isEqualTo(300);
        assertThat(existedTraining.getDescription()).isBlank();
    }

    @Test
    @DisplayName("Test add same training in one day")
    void testAddSameTraining() {
        User user = createUser();
        TrainingType trainingType = new TrainingType(1, "бег");
        LocalDate date = LocalDate.of(2024, 4, 20);
        Training training = new Training(1L, trainingType, date, 200, 400, "", user);
        when(trainingDao.findTrainingById(user.getId(), "бег", date)).thenReturn(Optional.of(training));
        when(trainingTypeDao.findByType("бег")).thenReturn(Optional.of(trainingType));
        trainingService.addNewTraining(user, "бег", LocalDate.now(), 100, 300, "");
        assertThat(user.getTrainings()).isEmpty();
    }

    @Test
    @DisplayName("Watch user`s training")
    void testWatchTrainings() {
        User user = createUser();
        LocalDate date = LocalDate.of(2023, 4, 20);
        Training training1 = new Training(1L, new TrainingType(1, "кроссфит"), date, 60, 400, "", user);
        Training training2 = new Training(2L, new TrainingType(2, "бег"), date, 50, 300, "", user);
        when(trainingTypeDao.findByType("кроссфит")).thenReturn(Optional.of(new TrainingType(1, "кроссфит")));
        when(trainingTypeDao.findByType("бег")).thenReturn(Optional.of(new TrainingType(2, "бег")));
        when(trainingDao.findTrainingById(1L, "кроссфит", date)).thenReturn(Optional.empty());
        when(trainingDao.findTrainingById(2L, "бег", date)).thenReturn(Optional.empty());
        when(trainingDao.findAllUserTrainings(user.getId())).thenReturn(List.of(training1, training2));
        trainingService.addNewTraining(user, "кроссфит", date, 60, 400, "");
        trainingService.addNewTraining(user, "бег", date, 50, 300, "");
        List<Training> actual = trainingService.getTrainings(user);
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getDate()).isEqualTo(date);
        assertThat(actual.get(1).getDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("Watch user`s training for the day")
    void testWatchTrainingsForDay() {
        User user = createUser();
        LocalDate date = LocalDate.of(2024, 3, 22);
        TrainingType trainingType = new TrainingType(1, "бег");
        Training training = new Training(1L, trainingType, date, 60, 400, "", user);
        when(userService.getUserByName("John")).thenReturn(user);
        when(trainingDao.findUserTrainingsByDay(user.getId(), date)).thenReturn(List.of(training));
        List<Training> actual = trainingService.getTrainings(user, "22/03/2024");
        assertThat(actual).hasSize(1);
        verify(trainingDao, times(1)).findUserTrainingsByDay(user.getId(), date);
    }

    @Test
    @DisplayName("Edit training")
    void testEditTraining() {
        User user = createUser();
        LocalDate date = LocalDate.of(2023, 10, 20);
        TrainingType trainingType = new TrainingType(1, "кроссфит");
        Training training = new Training(1L, trainingType, date, 60, 400, "", user);
        when(trainingDao.findTrainingById(1L, "кроссфит", date)).thenReturn(Optional.of(training));
        when(trainingTypeDao.findByType("кроссфит")).thenReturn(Optional.of(trainingType));
        trainingService.addNewTraining(user, "кроссфит", LocalDate.of(2024, 3, 22), 60, 400, "");
        Map<String, String> newValues = Map.of(
                "1", "бег",
                "2", "15/02/2024",
                "3", "120",
                "4", "350",
                "5", "Новое описание");
        trainingService.editTraining(user, "кроссфит", date, newValues);
        verify(trainingDao, times(1)).update(training);
    }

    @Test
    @DisplayName("Delete training")
    void testDeleteTraining() {
        User user = createUser();
        LocalDate date = LocalDate.of(2023, 10, 20);
        TrainingType trainingType = new TrainingType(1, "бег");
        Training training = new Training(1L, trainingType, date, 100, 300, "", user);
        when(trainingDao.findTrainingById(1L, "бег", date)).thenReturn(Optional.of(training));
        trainingService.deleteTraining(user, "бег", date);
        verify(trainingDao, times(1)).delete(isA(Long.class));
    }

    private User createUser() {
        return new User(1L, Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
    }
}
