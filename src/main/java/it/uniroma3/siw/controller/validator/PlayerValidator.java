package it.uniroma3.siw.controller.validator;


import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PlayerValidator implements Validator {
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        if(player.getName() != null && player.getSurname() != null &&player.getYear() != null
                && playerRepository.existsByNameAndSurnameAndBirthDate(player.getName(),player.getSurname(), player.getBirthDate())){
            errors.reject("player.duplicate");
        }
    }
}
