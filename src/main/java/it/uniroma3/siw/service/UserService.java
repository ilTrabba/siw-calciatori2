package it.uniroma3.siw.service;


import it.uniroma3.siw.controller.PlayerController;
import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PlayerRepository playerRepository;

    @Transactional
    public User getUser(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Transactional
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findAll();
        for(User user : iterable)
            result.add(user);
        return result;
    }

    @Transactional
    public void setPlayerToUser(User user, Player player) {

        user.getPlayers().add(player);
        player.getUsers().add(user);

        this.userRepository.save(user);
        this.playerRepository.save(player);
    }
}
