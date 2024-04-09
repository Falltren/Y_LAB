package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBase {

    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public Optional<User> getUserByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
    }
}