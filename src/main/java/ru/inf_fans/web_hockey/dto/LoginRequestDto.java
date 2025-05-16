package ru.inf_fans.web_hockey.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;
}