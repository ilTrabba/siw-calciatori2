package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Player;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    public boolean existsByNameAndBirthDate(String name, LocalDate birthDate);

    public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);

    public List<Player> findByName(String name);

    public List<Player> findByNameAndSurname(String name, String surname);

    public List<Player> findByBirthDate(LocalDate birthDate);

    public List<Player> findByYear(Integer year);

}
