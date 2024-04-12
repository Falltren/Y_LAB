package org.fallt.repository;

import org.fallt.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище для пользователей
 */
public class UserBase {

    private final List<User> users = new ArrayList<>();

    /**
     * Добавление нового пользователя в хранилище
     * @param user Новый пользователь
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Получение пользователя из хранилища. Пользователь возвращается в обертке в виде Optional
     * @param name Имя пользователя
     * @return Optional
     */
    public Optional<User> getUserByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
    }

    /**
     * Метод возвращает список всех пользователей, находящихся в хранилище
     * @return Список пользователей
     */
    public List<User> getAllUser() {
        return new ArrayList<>(users);
    }
}
