package ru.inf_fans.web_hockey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.CompactUserDto;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.mapper.UserEntityMapper;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityMapper userMapper;
    private final UserEntityMapper userEntityMapper;

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

    public List<CompactUserDto> getAllCompactUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userEntityMapper::toCompactUserDto)
                .toList();
    }
}
