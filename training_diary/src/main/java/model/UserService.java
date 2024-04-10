package model;

import lombok.Setter;

import java.util.List;

public class UserService {

    private UserBase userBase;

    @Setter
    private User currentUser;

    public UserService(UserBase userBase) {
        this.userBase = userBase;
    }

    public User getUserByName(String name) {
        return userBase.getUserByName(name).orElseThrow();
    }

    public List<User> getAllUsers() {
        return userBase.getAllUser();
    }

    public List<Training> getTrainingStatistic(User user) {
        return user.getTrainings();
    }

    public void deleteTraining() {

    }

}
