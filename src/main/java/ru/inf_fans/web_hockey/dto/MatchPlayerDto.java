package ru.inf_fans.web_hockey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Дто для использования в микро матчах")
public class MatchPlayerDto {
    public String name;
    public String surname;
    public String email;
    public Date born;
    public Float rating;
}
