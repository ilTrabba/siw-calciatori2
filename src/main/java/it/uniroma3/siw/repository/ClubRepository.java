package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Club;
import it.uniroma3.siw.model.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClubRepository extends CrudRepository<Club,Long> {

    public boolean existsByName(String name);

    public boolean existsByNameAndFoundationYear(String name, Integer year);

    public List<Club> findByName(String name);

    public List<Club> getClubsByStarredRomaPlayersNotContaining(Player player);

}
