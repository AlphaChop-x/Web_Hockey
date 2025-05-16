package ru.inf_fans.web_hockey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "tournament")
@ToString(exclude = "players")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column()
    private String name;
    @Column()
    private LocalDate startDate;
    @Column()
    private LocalDate endDate;

    @Column
    private String location;

    @OneToMany(mappedBy = "tournament")
    @Column()
    private Set<MicroMatch> microMatches = new HashSet<>();

    @JsonIgnore
    @ManyToMany()
    @JoinTable(
            name = "app_user_tournament",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> players = new ArrayList<>();

    @Transactional
    public void addUser(User user) {
        this.players.add(user);
    }

    @Transactional
    public void removeUser(User user) {
        this.players.remove(user);
    }
}
