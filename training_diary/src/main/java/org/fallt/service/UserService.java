package org.fallt.service;

import org.fallt.model.User;
import org.fallt.repository.UserBase;

import java.util.List;

public class UserService {

    private UserBase userBase;

    public UserService(UserBase userBase) {
        this.userBase = userBase;
    }

    public User getUserByName(String name) {
        return userBase.getUserByName(name).orElseThrow();
    }

    public List<User> getAllUsers() {
        return userBase.getAllUser();
    }

}
