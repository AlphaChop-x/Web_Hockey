package ru.inf_fans.web_hockey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с почтой " + email + " не найден"));
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByPhonenumber(String phonenumber) {
        User user = userRepository.findByPhoneNumber(phonenumber).orElse(null);
        if (user != null) {
            return true;
        }
        return false;
    }
}
