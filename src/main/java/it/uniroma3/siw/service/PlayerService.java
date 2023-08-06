package it.uniroma3.siw.service;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ClubRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.PlayerRepository;
import jakarta.transaction.Transactional;

@Service
public class PlayerService {
    @Autowired
    ClubRepository clubRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ImageRepository imageRepository;

    @Transactional
    public void createPlayer(Player player, MultipartFile image) throws IOException {
        Image playerImg = new Image(image.getBytes());
        this.imageRepository.save(playerImg);

        player.setFoto(playerImg);
        this.playerRepository.save(player);
    }

    @Transactional
    public List<Player> getSearchedPlayers(String name) {
        return this.playerRepository.findByName(name);
    }



    @Transactional
    public void setClubToPlayer(Player player, Long clubId) {
        Club club = this.clubRepository.findById(clubId).get();

        club.getStarredRomaPlayers().add(player);
        player.getClubs().add(club);

        this.clubRepository.save(club);
        this.playerRepository.save(player);
    }

    @Transactional
    public void removeClubToPlayer(Player player, Long clubId) {
        Club club = this.clubRepository.findById(clubId).get();

        club.getStarredRomaPlayers().remove(player);
        player.getClubs().remove(club);

        this.clubRepository.save(club);
        this.playerRepository.save(player);
    }

    public String function(Model model,Player player,UserDetails user){
        Set<Club> playerCareer = new HashSet<>();                 //player career intendo tipo movieCast
        if(player.getClubs() != null)
            playerCareer.addAll(player.getClubs());
        playerCareer.remove(null);
        model.addAttribute("playerCareer", playerCareer);
        model.addAttribute("player", player);
        if(user != null && this.alreadyReviewed(player.getReviews(),user.getUsername()))
            model.addAttribute("hasComment", true);
        else
            model.addAttribute("hasComment", false);
        model.addAttribute("review", new Review());
        model.addAttribute("reviews", player.getReviews());
        return "player.html";
    }

    @Transactional
    public boolean alreadyReviewed(Set<Review> reviews,String author){
        if(reviews != null)
            for(Review rev : reviews)
                if(rev.getAuthor().equals(author))
                    return true;
        return false;
    }
}