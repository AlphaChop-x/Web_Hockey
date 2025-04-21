package ru.inf_fans.web_hockey.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ru.inf_fans.web_hockey.entity.tournament.Team;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.enums.Gender;
import ru.inf_fans.web_hockey.entity.user.enums.Role;
import ru.inf_fans.web_hockey.entity.user.enums.RussianHockeyRank;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@ToString
@Table(name = "app_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int", updatable = false, nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = true)
    private String patronymic;

    @Column(
            nullable = false,
            unique = true
    )
    private String phoneNumber;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date born;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column
    private Float rating;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            name = "skill_level"
    )
    private RussianHockeyRank rank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToMany(mappedBy = "players", fetch = FetchType.EAGER)
    private Set<Team> teams = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    private Set<Tournament> tournament = new HashSet<>();

    public List<Role> getRole() {
        return List.of(this.role);
    }
}
