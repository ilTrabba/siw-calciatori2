package it.uniroma3.siw.controller.validator;


import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ClubValidator implements Validator {
    @Autowired
    private ClubRepository clubRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Club.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Club club = (Club) target;
        if (club.getName() != null && club.getFoundationYear() != null &&
                this.clubRepository.existsByNameAndFoundationYear(club.getName(), club.getFoundationYear())) {
            errors.reject("club.duplicate");
        }
    }
}