package ru.inf_fans.web_hockey.entity.tournament;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.tournament.enums.TournamentFormat;
import ru.inf_fans.web_hockey.entity.user.User;

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

    @Enumerated(EnumType.STRING)
    @Column()
    private TournamentFormat format;

    @Column
    private String location;

    @OneToMany(mappedBy = "tournament")
    @Column()
    private Set<MicroMatch> microMatches = new HashSet<>();

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
