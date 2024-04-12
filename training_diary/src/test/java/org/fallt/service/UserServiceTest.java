package org.fallt.service;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.repository.UserBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;

    private UserBase userBase;

    @BeforeEach
    public void setUp() {
        userBase = Mockito.mock(UserBase.class);
        userService = new UserService(userBase);
    }

    @Test
    @DisplayName("Get user by name")
    void testGetUserByName() {
        User user = createUser();
        when(userBase.getUserByName("John")).thenReturn(Optional.of(user));
        User actual = userService.getUserByName("John");
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Get all users")
    void testGetAllUsers() {
        when(userBase.getAllUser()).thenReturn(List.of(
                new User(Role.USER, "Mike", "111", LocalDateTime.now(), new ArrayList<>()),
                new User(Role.USER, "Tom", "222", LocalDateTime.now(), new ArrayList<>()),
                new User(Role.USER, "Anna", "333", LocalDateTime.now(), new ArrayList<>())
        ));
        List<User> actual = userService.getAllUsers();
        assertThat(actual).hasSize(3);
    }

    @Test
    @DisplayName("Add new user")
    void testAddUser() {
        User user = createUser();
        userService.addUser(user);
        when(userBase.getUserByName("John")).thenReturn(Optional.of(user));
        User actual = userService.getUserByName("John");
        assertThat(actual).isEqualTo(user);
    }

    private User createUser() {
//        List<String> name
        return new User(Role.USER, "John", "123", LocalDateTime.now(), new ArrayList<>());
    }
}
