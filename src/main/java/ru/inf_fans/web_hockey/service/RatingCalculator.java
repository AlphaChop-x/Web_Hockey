package ru.inf_fans.web_hockey.service;

public class RatingCalculator {

    public static int updateRating(int previousRating, int newRating) {
        return previousRating + (int) (0.1 * (newRating - previousRating));
    }
}
