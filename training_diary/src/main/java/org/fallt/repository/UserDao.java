package org.fallt.repository;

import org.fallt.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    void create(User user);

    Optional<User> getUserByName(String name);

    List<User> findAll();

}
