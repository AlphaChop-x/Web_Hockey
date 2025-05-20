package ru.inf_fans.web_hockey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Компактное DTO для пользователя")
@Data
public class CompactUserDto {

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;
    @Schema(description = "Фамилия пользователя", example = "Фамилия")
    private String surname;
    @Schema(description = "Рейтинг пользователя", example = "2200")
    private Float rating;

}
