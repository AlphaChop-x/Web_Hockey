package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table
public class PlayerPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private User player;

    @ManyToMany
    @JoinTable(
            name = "performance_micromatch",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "micromatch_id")
    )
    private Set<MicroMatch> micromatches = new HashSet<>();

    @Column
    private int goals;

    @Column
    private int losses;
}
