//package ru.inf_fans.web_hockey.controller;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import ru.inf_fans.web_hockey.dto.UserDto;
//import ru.inf_fans.web_hockey.mapper.UserEntityMapper;
//import ru.inf_fans.web_hockey.service.UserServiceImpl;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/users")
//public class UserRestController {
//
//    private final UserServiceImpl userService;
//    private final UserEntityMapper userEntityMapper;
//
//    /**
//     * Метод для создания пользователя
//     *
//     * @param userDto пользователь
//     */
//    @PostMapping()
//    public ResponseEntity<?> createUser(
//            @RequestBody @Validated UserDto userDto
//    ) {
//        UserDto createdUserEntity = userService.createUser(userDto);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(createdUserEntity);
//    }
//
//    /**
//     * Метод, удаляющий пользователя
//     *
//     * @param userId id пользователя
//     */
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<String> deleteUser(
//            @PathVariable int userId
//    ) {
//        try {
//            userService.deleteUser(userId);
//            return ResponseEntity.ok("User deleted successfully");
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(e.getMessage());
//        }
//    }
//
//    /**
//     * Метод для удаления пользователя
//     *
//     * @param userId id пользователя
//     * @param rating новый рейтинг пользователя
//     */
//    @PatchMapping("/{userId}")
//    public ResponseEntity<String> updateUserRating(
//            @PathVariable int userId,
//            @RequestBody float rating
//    ) {
//        userService.updateUserRating(userId, rating);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("User rating updated successfully");
//    }
//
//
//}
