package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.tournament.enums.MatchStatus;
import ru.inf_fans.web_hockey.entity.user.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
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

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @ManyToMany(mappedBy = "micromatches")
    private Set<PlayerPerformance> playerPerformances = new HashSet<>();

    public MicroMatch() {

    }

    public MicroMatch(List<User> team, List<User> opposingTeam, Tournament tournament) {

    }
}
