package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.user.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToMany
    @JoinTable(
            name = "team_player",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<User> players = new HashSet<>();

    public Team(Set<User> players) {
        this.players = players;
    }

    public Team() {

    }
}
