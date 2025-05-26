package ru.inf_fans.web_hockey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundUserException;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.entity.enums.Role;
import ru.inf_fans.web_hockey.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(User user, Role role) {
        try {
            user.setRole(role);
            userRepository.save(user);
        } catch (NotFoundUserException e) {
            throw new NotFoundUserException(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public void blockUser(User user) {
//        user.setRole(Role.COACH);
    }

}
