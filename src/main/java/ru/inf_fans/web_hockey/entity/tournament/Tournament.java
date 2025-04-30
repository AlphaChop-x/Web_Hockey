package ru.inf_fans.web_hockey.entity.tournament;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.tournament.enums.TournamentFormat;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "tournament")
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

    @ManyToMany()
    @JoinTable(
            name = "app_user_tournament",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_entity_id")
    )
    private List<UserEntity> players = new ArrayList<>();

    @Transactional
    public void addUser(UserEntity userEntity) {
        this.players.add(userEntity);
    }

    @Transactional
    public void removeUser(UserEntity userEntity) {
        this.players.remove(userEntity);
    }
}
