package org.fallt.security;

import org.fallt.model.Role;
import org.fallt.model.User;
import org.fallt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.Mockito.*;

class RegistrationTest {

    private Registration registration;

    private UserService userService;

    @BeforeEach
    public void setup() {
        userService = Mockito.mock(UserService.class);
        registration = new Registration(userService);
    }

    @Test
    @DisplayName("Test registration")
    void testRegister() {
        registration.register("John", "123", "123");
        User user = new User(Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
        verify(userService, times(1)).addUser(user);
    }

    @Test
    @DisplayName("Test registration when user input different password")
    void testRegisterWithDifferentPassword() {
        registration.register("John", "123", "321");
        User user = new User(Role.ROLE_USER, "John", "123", LocalDateTime.now(), new HashSet<>());
        verify(userService, times(0)).addUser(user);
    }
}
