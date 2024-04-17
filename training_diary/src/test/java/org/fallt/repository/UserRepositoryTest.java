package org.fallt.repository;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.userRepository = new UserRepository();
    }

    @Test
    @DisplayName("Test add user")
    void testAddUser() {
        User newUser = createUser();
        userRepository.addUser(newUser);
        assertThat(userRepository.getAllUser()).hasSize(1);
    }

    @Test
    @DisplayName("Test get user by name")
    void testGetUserByName() {
        User expectedUser = createUser();
        userRepository.addUser(expectedUser);
        User actualUser = userRepository.getUserByName("John").orElseThrow();
        assertThat(actualUser).isEqualTo(expectedUser);
    }


    private User createUser() {
        return new User(Role.USER, "John", "123", LocalDateTime.now(), new HashSet<>());
    }
}
