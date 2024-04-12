package org.fallt.repository;

import org.fallt.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBase {

    private final List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public Optional<User> getUserByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
    }

    public List<User> getAllUser() {
        return new ArrayList<>(users);
    }
}
