package ru.inf_fans.web_hockey.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.inf_fans.web_hockey.entity.Match;

@Getter
public class MatchEvent extends ApplicationEvent {
    private final Match match;

    public MatchEvent(Object source, Match match) {
        super(source);
        this.match = match;
    }
}