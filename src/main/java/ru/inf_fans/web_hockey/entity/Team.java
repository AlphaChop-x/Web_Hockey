package ru.inf_fans.web_hockey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
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

    public Team(String s, List<MatchPlayerDto> team1) {
    }

    public int size() {
        return players.size();
    }

    public void add(User player) {
        players.add(player);
    }

    @Override
    public String toString() {
        return String.format("Team{players=%s}",
                players.stream().map(User::toString).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
