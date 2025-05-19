package ru.inf_fans.web_hockey.dto;

import lombok.Data;

@Data
public class CompactUserDto {

    private Long id;
    private String name;
    private String surname;
    private Float rating;

}
