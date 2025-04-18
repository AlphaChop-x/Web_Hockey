package ru.inf_fans.web_hockey.service;

import ru.inf_fans.web_hockey.entity.user.User;

public interface UserService {

    User createUser(User user);

    User getUser(int userId);

    void deleteUser(int userId);
}
