package org.fallt.service;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.repository.UserDao;
import org.fallt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserService userService;

    private UserRepository userRepository;

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDao = Mockito.mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    @DisplayName("Get user by name")
    void testGetUserByName() {
        User user = createUser();
        when(userRepository.getUserByName("John")).thenReturn(Optional.of(user));
        User actual = userService.getUserByName("John");
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Get all users")
    void testGetAllUsers() {
        when(userRepository.getAllUser()).thenReturn(List.of(
                new User(1L, Role.ROLE_USER, "Mike", "111", LocalDateTime.now(), new HashSet<>()),
                new User(2L, Role.ROLE_USER, "Tom", "222", LocalDateTime.now(), new HashSet<>()),
                new User(3L, Role.ROLE_USER, "Anna", "333", LocalDateTime.now(), new HashSet<>())
        ));
        Collection<User> actual = userService.getAllUsers();
        assertThat(actual).hasSize(3);
    }

    @Test
    @DisplayName("Add new user")
    void testAddUser() {
        User user = createUser();
        userService.addUser(user);
        when(userRepository.getUserByName("John")).thenReturn(Optional.of(user));
        User actual = userService.getUserByName("John");
        assertThat(actual).isEqualTo(user);
    }

    private User createUser() {
        return new User(1L, Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
    }
}
