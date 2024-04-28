package org.fallt.security;

import org.fallt.dto.request.RegisterRq;
import org.fallt.dto.response.RegisterRs;
import org.fallt.exception.BadRequestException;
import org.fallt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        RegisterRq request = createRequest("123");
        RegisterRs response = registration.register(request);
        assertThat(response.getName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Test registration when user input different password")
    void testRegisterWithDifferentPassword() {
        RegisterRq request = createRequest("321");
        assertThatThrownBy(() ->
                registration.register(request)
        ).isInstanceOf(BadRequestException.class);
    }

    private RegisterRq createRequest(String confirmPassword) {
        return RegisterRq.builder()
                .name("John")
                .password("123")
                .confirmPassword(confirmPassword)
                .build();
    }
}
