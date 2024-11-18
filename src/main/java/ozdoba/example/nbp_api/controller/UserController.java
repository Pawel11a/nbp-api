package ozdoba.example.nbp_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ozdoba.example.nbp_api.model.AppUser;
import ozdoba.example.nbp_api.service.UserService;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody AppUser appUser) {
        try {
            log.info("Registering user: {}", appUser.getUsername());
            userService.addUser(appUser);
            log.info("User registered successfully: {}", appUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            log.warn("User registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}