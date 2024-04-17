package org.fallt.repository;

import org.fallt.model.User;

import java.util.*;

/**
 * Хранилище для пользователей
 */
public class UserRepository {

    private final Map<String, User> users = new HashMap<>();

    /**
     * Добавление нового пользователя в хранилище
     *
     * @param user Новый пользователь
     */
    public void addUser(User user) {
        users.put(user.getName(), user);
    }

    /**
     * Получение пользователя из хранилища. Пользователь возвращается в обертке в виде Optional
     *
     * @param name Имя пользователя
     * @return Optional
     */
    public Optional<User> getUserByName(String name) {
        return Optional.ofNullable(users.get(name));
    }

    /**
     * Метод возвращает список всех пользователей, находящихся в хранилище
     *
     * @return Список пользователей
     */
    public Collection<User> getAllUser() {
        return users.values();
    }
}
