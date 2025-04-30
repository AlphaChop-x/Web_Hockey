package ru.inf_fans.web_hockey.service;

import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUser(int userId);

    void deleteUser(int userId);
}
