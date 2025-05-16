package ru.inf_fans.web_hockey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.entity.Team;
import ru.inf_fans.web_hockey.repository.TeamRepository;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    public void CreateTeam(Team team) {
        teamRepository.save(team);
    }
}
