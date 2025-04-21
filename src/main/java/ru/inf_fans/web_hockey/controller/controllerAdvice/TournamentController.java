package ru.inf_fans.web_hockey.controller.controllerAdvice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@AllArgsConstructor
@Controller
public class TournamentController {

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
}
