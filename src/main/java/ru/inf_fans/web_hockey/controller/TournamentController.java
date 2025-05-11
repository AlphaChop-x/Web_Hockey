package ru.inf_fans.web_hockey.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.service.UserServiceImpl;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@AllArgsConstructor
@Controller
public class TournamentController {

    private final UserServiceImpl userServiceImpl;
    TournamentServiceImpl tournamentService;

    @RequestMapping("/tournaments")
    public String tournaments(
            Model model
    ) {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", tournaments);
        return "tournaments";
    }

    @RequestMapping(value = "/tournaments", method = RequestMethod.POST)
    public String addTournament(
            @ModelAttribute("tournament") Tournament tournament,
            Model model
    ) {
        tournamentService.createTournament(tournament);

        List<Tournament> tournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", tournaments);
        return "tournaments";
    }

    @RequestMapping(value = "/tournaments/{tournamentId}", method = RequestMethod.GET)
    public String showTournamentPage(
            @PathVariable(name = "tournamentId") Long tournamentId,
            Model model
    ) {
        TournamentApiDto tournament = tournamentService.findTournamentApiDtoById(tournamentId);
        model.addAttribute("tournament", tournament);
        return "tournamentPage";
    }

    @RequestMapping(value = "/tournaments/{tournamentId}/register")
    public String registerOnTournament(
            Model model,
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        int userId = userServiceImpl.getUserIdByName(email);
        tournamentService.registerUserToTournament(tournamentId, userId);
        return "/tournaments";
    }
}
