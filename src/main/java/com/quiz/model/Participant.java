package com.quiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Une personne presente dans une Room precise.
 *
 * estHost   : controle la partie (lance les questions, etc.)
 * estJoueur : repond aux questions et apparait au classement
 *
 * Un host classique choisit a la creation s'il est aussi joueur ou non.
 * Un joueur invite a toujours estHost = false et estJoueur = true.
 */
@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String pseudo;

    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private boolean estHost = false;

    @Column(nullable = false)
    private boolean estJoueur = true;

    @Column(nullable = false)
    private LocalDateTime dateArrivee = LocalDateTime.now();

    /** Verrouillage optimiste : evite qu'une mise a jour de score en ecrase une autre. */
    @Version
    private int version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Participant() {
    }

    public Participant(String pseudo, boolean estHost, boolean estJoueur) {
        this.pseudo = pseudo;
        this.estHost = estHost;
        this.estJoueur = estJoueur;
    }

    public Long getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void ajouterPoints(int points) {
        this.score += points;
    }

    public boolean isEstHost() {
        return estHost;
    }

    public void setEstHost(boolean estHost) {
        this.estHost = estHost;
    }

    public boolean isEstJoueur() {
        return estJoueur;
    }

    public void setEstJoueur(boolean estJoueur) {
        this.estJoueur = estJoueur;
    }

    public LocalDateTime getDateArrivee() {
        return dateArrivee;
    }

    public int getVersion() {
        return version;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Participant{id=" + id + ", pseudo='" + pseudo + "', score=" + score
                + ", estHost=" + estHost + ", estJoueur=" + estJoueur + "}";
    }
}
