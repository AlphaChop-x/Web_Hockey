package ru.inf_fans.web_hockey.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticationResponseDto {
    private final String accessToken;
    private final String refreshToken;
}
