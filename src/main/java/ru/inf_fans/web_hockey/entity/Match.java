package ru.inf_fans.web_hockey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table()
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "first_team_id", nullable = false)
    private Team firstTeam;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "second_team_id", nullable = false)
    private Team secondTeam;

    @Column()
    private LocalDateTime startDate;
    @Column()
    private LocalDateTime endDate;

    @Column()
    private int firstTeamScore;

    @Column()
    private int secondTeamScore;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    public Match() {

    }

    public Match(List<User> team, List<User> opposingTeam, Tournament tournament) {

    }

    public Match(Tournament tournament, Team firstTeam, Team secondTeam) {
        this.tournament = tournament;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    @Override
    public String toString() {
        return "Match\n" +
                "tournament id: " + tournament.getId() +
                "\nfirstTeamId's=" + firstTeam.getPlayers().toString() +
                "\nsecondTeamId's=" + secondTeam.getPlayers().toString();
    }
}
