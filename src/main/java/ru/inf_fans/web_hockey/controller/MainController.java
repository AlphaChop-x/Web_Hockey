package ru.inf_fans.web_hockey.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.service.UserServiceImpl;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserServiceImpl userService;

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "processLogin";
    }

    @PostMapping("/login")
    public String processLogin() {
        return "home";
    }

    @PostMapping("/logout")
    public String processLogout(
            HttpSession httpSession,
            Model model
    ) {
        model.addAttribute("message", "You have successfully logged out!");
        httpSession.invalidate();
        return "processLogin";
    }

    @GetMapping("/register")
    public String showRegister(
            @Autowired Model model
    ) {
        model.addAttribute("userDto", new UserDto(
                null, null, null, null, null, null,
                null, null, null, null));

        return "processRegistration";
    }

    @PostMapping("/register")
    public String processRegister(
            @Validated @ModelAttribute("userDto") UserDto userDto,
            BindingResult bindingResult,
            @ModelAttribute("retypedPassword") String retypedPassword,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            return "processRegistration";
        }

        if (!retypedPassword.equals(userDto.password())) {
            model.addAttribute("passwordsNotEquals", true);
        }

        try {
            userService.createUser(userDto);
            model.addAttribute("registered", true);
            return "processLogin";
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("email")) {
                bindingResult.rejectValue("email", "email.duplicate", "Пользователь с такой почтой уже существует!");
            }
        }
        return "processRegistration";
    }

    @GetMapping("/users/")
    public String showUser(
            Model model,
            Authentication authentication
    ) {
        UserDto userDto = userService.getUserDtoByUserEmail(authentication.getName());
        model.addAttribute("userDto", userDto);
        return "userPage";
    }

    @GetMapping("/users/{userId}")
    public String showUserById(
            Model model,
            @PathVariable int userId
    ) {
        UserDto userDto = userService.getUser(userId);
        model.addAttribute("userDto", userDto);
        return "userPage";
    }
}
