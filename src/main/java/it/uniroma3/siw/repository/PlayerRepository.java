package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    public boolean existsByNameAndBirthDate(String name, LocalDate birthDate);

    public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);

    public List<Player> findByName(String name);

    public List<Player> findBySurname(String surname);

    public List<Player> findByNameAndSurname(String name, String surname);

    public List<Player> findByBirthDate(LocalDate birthDate);

    public List<Player> findByYear(Integer year);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.reviewedPlayer = :player")
    public Float getAvgRatingByPlayer(@Param("player") Player player);


}
