package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

import java.util.HashSet;
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

    @ManyToMany
    @JoinTable(
            name = "team_player",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<UserEntity> players = new HashSet<>();

    public Team(Set<UserEntity> players) {
        this.players = players;
    }

    public Team() {

    }

    public int size() {
        return players.size();
    }

    public void add(UserEntity player) {
        players.add(player);
    }

    @Override
    public String toString() {
        return String.format("Team{players=%s}",
                players.stream().map(UserEntity::toString).collect(Collectors.toList()));
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
