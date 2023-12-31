package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.PlayerValidator;
import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ClubRepository;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.PlayerService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class PlayerController {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerValidator playerValidator;
    @Autowired
    private GlobalController globalController;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin/formNewPlayer")
    public String newPlayer(Model model) {
        model.addAttribute("player", new Player());
        return "admin/formNewPlayer.html";
    }

    @PostMapping("/admin/uploadPlayer")
    public String newPlayer(Model model, @Valid @ModelAttribute("player") Player player, BindingResult bindingResult,
                           @RequestParam("file") MultipartFile image) throws IOException {
        this.playerValidator.validate(player, bindingResult);
        if (!bindingResult.hasErrors()) {
            /*
             * for(MultipartFile image : images){
             * Image picture = new Image(image.getBytes());
             * this.imageRepository.save(picture);
             * playerImgs.add(picture);
             * }
             */
            /*
             * Image playerImg = new Image(image.getBytes());
             * this.imageRepository.save(playerImg);
             *
             * player.setImage(playerImg);
             * this.playerRepository.save(player);
             */

            this.playerService.createPlayer(player, image);
            return this.playerService.function(model, player, globalController.getUser());
        } else {
            return "/admin/formNewPlayer.html";
        }
    }

    @GetMapping("/admin/indexPlayer")
    public String indexPlayer(Model model) {
        return "admin/indexPlayer.html";
    }

    @GetMapping("/players")
    public String showAllPlayers(Model model) {
        model.addAttribute("players", this.playerRepository.findAll());
        return "players.html";
    }

    @GetMapping("/orderedPlayers")
    public String showAllPlayersOrdered(Model model) {
        model.addAttribute("players", this.playerService.getPlayersOrderedByAverageRating());
        return "players.html";
    }

    @GetMapping("/favoritePlayers")
    public String showFavoritePlayers(Model model) {
        User user = this.credentialsService.getCredentials(this.globalController.getUser().getUsername()).getUser();
        model.addAttribute("players", this.playerService.findUserPlayers(user));
        return "players.html";
    }

    @GetMapping("/players/{playerId}")
    public String getPlayer(Model model, @PathVariable("playerId") Long id) {
        Player player = this.playerRepository.findById(id).get();

        return this.playerService.function(model, player, this.globalController.getUser());
    }

    @PostMapping("/searchPlayer")
    public String searchPlayer(Model model, @RequestParam String name) {
        if (name.length() == 0) {
            model.addAttribute("players", this.playerRepository.findAll());
        } else {
            model.addAttribute("players", this.playerService.getSearchedPlayers(name));
        }
        return "players.html";
    }

    @GetMapping("/admin/manage/{playerId}")
    public String updatePlayer(Model model, @PathVariable("playerId") Long id) {
        Player player = this.playerRepository.findById(id).get();
        model.addAttribute("player", player);
        model.addAttribute("clubs", player.getClubs());
        return "admin/formUpdatePlayer.html";
    }


    @GetMapping("/admin/manage/addClub/{playerId}")
    public String updatePlayerClub(Model model, @PathVariable("playerId") Long playerId) {
        Player player = this.playerRepository.findById(playerId).get();
        model.addAttribute("player", player);
        model.addAttribute("choices", this.clubRepository.getClubsByStarredRomaPlayersNotContaining(player));
        return "admin/formAddClubs.html";
    }

    @GetMapping("/admin/manage/setClub/{playerId}/{clubId}")
    public String setPlayerClub(Model model, @PathVariable("playerId") Long playerId,
                                @PathVariable("clubId") Long clubId) {
        Player player = this.playerRepository.findById(playerId).get();
        this.playerService.setClubToPlayer(player, clubId);

        model.addAttribute("player", player);
        model.addAttribute("clubs", player.getClubs());

        return "admin/formUpdatePlayer.html";
    }

    @GetMapping("/setFavoritePlayer/{playerId}")
    public String setFavoritePlayerToUser(Model model, @PathVariable("playerId") Long playerId) {
        Player player = this.playerRepository.findById(playerId).get();
        User user = this.credentialsService.getCredentials(this.globalController.getUser().getUsername()).getUser();
        this.userService.setPlayerToUser(user, player);

        return this.playerService.function(model, player, this.globalController.getUser());
    }

    @GetMapping("/admin/manage/removeClub/{playerId}/{clubId}")
    public String removePlayerClub(Model model, @PathVariable("playerId") Long playerId,
                                   @PathVariable("clubId") Long clubId) {
        Player player = this.playerRepository.findById(playerId).get();
        this.playerService.removeClubToPlayer(player, clubId);
        model.addAttribute("player", player);
        model.addAttribute("clubs", player.getClubs());

        return "admin/formUpdatePlayer.html";
    }

    @GetMapping("/removeUserPlayer/{playerId}")
    public String removeUserPlayer(Model model, @PathVariable("playerId") Long playerId) {
        Player player = this.playerRepository.findById(playerId).get();
        User user = this.credentialsService.getCredentials(this.globalController.getUser().getUsername()).getUser();
        this.userService.removePlayerToUser(player,user);

        return this.playerService.function(model, player, this.globalController.getUser());
    }

    @GetMapping("/admin/deletePlayer/{playerId}")
    public String deletePlayer(Model model, @PathVariable("playerId") Long playerId){
        Player player = this.playerRepository.findById(playerId).get();

        for (Club club : player.getClubs()) {
            club.getStarredRomaPlayers().remove(player);
        }

        this.playerRepository.delete(player);
        return this.showAllPlayers(model);
    }
}
