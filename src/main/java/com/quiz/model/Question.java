package com.quiz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Une question appartenant a un Quiz, avec ses choix de reponse possibles.
 * Donnee de reference : jamais modifiee pendant une partie.
 */
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String enonce;

    /** Position de la question dans le quiz (0, 1, 2...) */
    @Column(nullable = false)
    private int ordre;

    /** Duree accordee pour repondre, en millisecondes */
    @Column(nullable = false)
    private int dureeReponseMs = 20_000;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choix> choix = new ArrayList<>();

    public Question() {
    }

    public Question(String enonce, int dureeReponseMs) {
        this.enonce = enonce;
        this.dureeReponseMs = dureeReponseMs;
    }

    public Long getId() {
        return id;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getDureeReponseMs() {
        return dureeReponseMs;
    }

    public void setDureeReponseMs(int dureeReponseMs) {
        this.dureeReponseMs = dureeReponseMs;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Choix> getChoix() {
        return choix;
    }

    public void ajouterChoix(Choix c) {
        choix.add(c);
        c.setQuestion(this);
    }

    /** Renvoie le choix marque comme correct pour cette question (utile pour la correction). */
    @Transient
    public Choix getBonneReponse() {
        return choix.stream().filter(Choix::isEstCorrect).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Question{id=" + id + ", enonce='" + enonce + "'}";
    }
}
