package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @Min(1800)
    @Max(2023)
    private Integer foundationYear;

    @OneToOne
    private Image logo;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Player> starredRomaPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFoundationYear() {
        return foundationYear;
    }

    public void setFoundationYear(Integer foundationYear) {
        this.foundationYear = foundationYear;
    }

    public Image getLogo() {
        return logo;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public Set<Player> getStarredRomaPlayer() {
        return starredRomaPlayer;
    }

    public void setStarredRomaPlayer(Set<Player> starredRomaPlayer) {
        this.starredRomaPlayer = starredRomaPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return Objects.equals(name, club.name) && Objects.equals(foundationYear, club.foundationYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, foundationYear);
    }
}
