package ru.inf_fans.web_hockey.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.entity.user.User;
import ru.inf_fans.web_hockey.service.UserServiceImpl;

@RestController
@RequestMapping("/main")
public class UserRestController {

    private final UserServiceImpl userService;

    @Autowired
    public UserRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * Метод для создания пользователя
     *
     * @param user пользователь
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @RequestBody User user
    ) {
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    /**
     * Метод, удаляющий пользователя
     *
     * @param userId id пользователя
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable int userId
    ) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Метод для удаления пользователя
     *
     * @param userId id пользователя
     * @param rating новый рейтинг пользователя
     */
    @PatchMapping("/users/{userId}")
    public ResponseEntity<String> updateUserRating(
            @PathVariable int userId,
            @RequestBody float rating
    ) {
        userService.updateUserRating(userId, rating);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User rating updated successfully");
    }


}
