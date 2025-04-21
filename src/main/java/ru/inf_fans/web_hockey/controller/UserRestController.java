//package ru.inf_fans.web_hockey.controller;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.inf_fans.web_hockey.entity.user.UserEntity;
//import ru.inf_fans.web_hockey.service.UserServiceImpl;
//
//@RestController
//@RequestMapping("/rest")
//public class UserRestController {
//
//    private final UserServiceImpl userService;
//
//    @Autowired
//    public UserRestController(UserServiceImpl userService) {
//        this.userService = userService;
//    }
//
//    /**
//     * Метод для создания пользователя
//     *
//     * @param userEntity пользователь
//     */
////    @PostMapping("/users")
////    public ResponseEntity<UserEntity> createUser(
////            @RequestBody UserEntity userEntity
////    ) {
////        UserEntity createdUserEntity = userService.createUser(userEntity);
////        return ResponseEntity
////                .status(HttpStatus.CREATED)
////                .body(createdUserEntity);
////    }
//
//    /**
//     * Метод, удаляющий пользователя
//     *
//     * @param userId id пользователя
//     */
//    @DeleteMapping("/users/{userId}")
//    public ResponseEntity<String> deleteUser(
//            @PathVariable int userId
//    ) {
//        try {
//            userService.deleteUser(userId);
//            return ResponseEntity.ok("UserEntity deleted successfully");
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
//    @PatchMapping("/users/{userId}")
//    public ResponseEntity<String> updateUserRating(
//            @PathVariable int userId,
//            @RequestBody float rating
//    ) {
//        userService.updateUserRating(userId, rating);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("UserEntity rating updated successfully");
//    }
//
//
//}
