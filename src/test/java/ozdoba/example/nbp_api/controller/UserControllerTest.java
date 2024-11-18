package ozdoba.example.nbp_api.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ozdoba.example.nbp_api.model.AppUser;
import ozdoba.example.nbp_api.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        AppUser appUser = new AppUser();
        appUser.setUsername("john.doe@example.com");
        appUser.setPassword("password123");
        appUser.setFirstName("John");
        appUser.setLastName("Doe");

        doNothing().when(userService).addUser(appUser);

        // When
        ResponseEntity<?> response = userController.register(appUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("User registered successfully");
        verify(userService, times(1)).addUser(appUser);
    }

    @Test
    void shouldReturnBadRequestWhenUsernameAlreadyExists() {
        // Given
        AppUser appUser = new AppUser();
        appUser.setUsername("john.doe@example.com");

        doThrow(new IllegalArgumentException("Username is already in use"))
                .when(userService).addUser(appUser);

        // When
        ResponseEntity<?> response = userController.register(appUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username is already in use");
        verify(userService, times(1)).addUser(appUser);
    }
}