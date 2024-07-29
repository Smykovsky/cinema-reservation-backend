package pl.smyk.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Genre {
    ACTION("Akcja"),
    ADVENTURE("Przygoda"),
    ANIMATED("Animacja"),
    COMEDY("Komedia"),
    CRIMINAL("Kryminalny"),
    DRAMA("Dramat"),
    HORROR("Horror"),
    ROMANTIC("Romantyczny"),
    SCIENCE_FICTION("Science fiction"),
    THRILLER("Dreszczowiec"),
    SPORT("Sport");


    private final String genre;
}
