package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.tournament.enums.MatchStatus;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table()
public class MicroMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "first_team_id", nullable = false)
    private Team firstTeam;

    @ManyToOne
    @JoinColumn(name = "second_team_id", nullable = false)
    private Team secondTeam;

    @Column()
    private LocalDate startDate;
    @Column()
    private LocalDate endDate;

    @Column()
    private int firstTeamScore;

    @Column()
    private int secondTeamScore;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    public MicroMatch() {

    }

    public MicroMatch(List<UserEntity> team, List<UserEntity> opposingTeam, Tournament tournament) {

    }

    public MicroMatch(Tournament tournament, Team firstTeam, Team secondTeam) {
        this.tournament = tournament;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    @Override
    public String toString() {
        return "MicroMatch\n" +
                "tournament id: " + tournament.getId() +
                "\nfirstTeamId's=" + firstTeam.getPlayers().toString() +
                "\nsecondTeamId's=" + secondTeam.getPlayers().toString();
    }
}
