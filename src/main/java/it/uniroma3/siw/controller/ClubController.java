package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ClubValidator;
import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.repository.ClubRepository;
import it.uniroma3.siw.repository.ImageRepository;
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
public class ClubController {
    @Autowired
    ClubRepository clubRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ClubValidator clubValidator;

    @GetMapping("/admin/indexClub")
    public String indexClub(){
        return "/admin/indexClub.html";
    }

    @GetMapping("/admin/formNewClub")
    public String newClub(Model model){
        model.addAttribute("club",new Club());
        return "/admin/formNewClub.html";
    }

    @PostMapping("/admin/uploadClub")
    public String newclub(Model model, @Valid @ModelAttribute("club") Club club, BindingResult bindingResult, @RequestParam("file") MultipartFile image) throws IOException {
        this.clubValidator.validate(club,bindingResult);
        if(!bindingResult.hasErrors()){
            Image picture = new Image(image.getBytes());
            this.imageRepository.save(picture);
            club.setLogo(picture);

            this.clubRepository.save(club);

            model.addAttribute("club",club);
            return "club.html";
        } else {
            return "/admin/formNewClub.html";
        }
    }

    @GetMapping("/clubs")
    public String showAllClubs(Model model){
        model.addAttribute("clubs",this.clubRepository.findAll());
        return "clubs.html";
    }

    @GetMapping("/clubs/{clubId}")
    public String getClubs(Model model,@PathVariable("clubId") Long id){
        Club club = this.clubRepository.findById(id).get();
        model.addAttribute("club", this.clubRepository.findById(id).get());

        model.addAttribute("starredRomaPlayers", club.getStarredRomaPlayers());
        return "club.html";
    }

    @PostMapping("/searchClub")
    public String searchClub(Model model, @RequestParam String name) {
        if (name.length() == 0) {
            model.addAttribute("clubs", this.clubRepository.findAll());
        } else {
            model.addAttribute("clubs", this.clubRepository.findByName(name));
        }
        return "clubs.html";
    }

    @GetMapping("/admin/deleteClub/{clubId}")
    public String deleteClub(Model model, @PathVariable("clubId") Long clubId){
        Club club = this.clubRepository.findById(clubId).get();

        for (Player player : club.getStarredRomaPlayers()) {
            player.getClubs().remove(club);
        }

        this.clubRepository.delete(club);
        return this.showAllClubs(model);
    }
}
