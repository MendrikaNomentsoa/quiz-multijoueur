package com.quiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * La soumission d'un Participant joueur a une Question donnee.
 * Contrainte d'unicite (participant_id, question_id) : empeche de repondre
 * deux fois a la meme question, meme en cas de double clic ou requete dupliquee.
 */
@Entity
@Table(
        name = "reponse",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reponse_participant_question",
                columnNames = {"participant_id", "question_id"}
        )
)
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long tempsReponseMs;

    @Column(nullable = false)
    private boolean correcte;

    @Column(nullable = false)
    private int pointsObtenus;

    @Column(nullable = false)
    private LocalDateTime dateReponse = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(optional = false)
    @JoinColumn(name = "choix_id", nullable = false)
    private Choix choixSelectionne;

    public Reponse() {
    }

    public Reponse(Participant participant, Question question, Choix choixSelectionne,
                    long tempsReponseMs, boolean correcte, int pointsObtenus) {
        this.participant = participant;
        this.question = question;
        this.choixSelectionne = choixSelectionne;
        this.tempsReponseMs = tempsReponseMs;
        this.correcte = correcte;
        this.pointsObtenus = pointsObtenus;
    }

    public Long getId() {
        return id;
    }

    public long getTempsReponseMs() {
        return tempsReponseMs;
    }

    public void setTempsReponseMs(long tempsReponseMs) {
        this.tempsReponseMs = tempsReponseMs;
    }

    public boolean isCorrecte() {
        return correcte;
    }

    public void setCorrecte(boolean correcte) {
        this.correcte = correcte;
    }

    public int getPointsObtenus() {
        return pointsObtenus;
    }

    public void setPointsObtenus(int pointsObtenus) {
        this.pointsObtenus = pointsObtenus;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Choix getChoixSelectionne() {
        return choixSelectionne;
    }

    public void setChoixSelectionne(Choix choixSelectionne) {
        this.choixSelectionne = choixSelectionne;
    }

    @Override
    public String toString() {
        return "Reponse{id=" + id + ", correcte=" + correcte + ", points=" + pointsObtenus + "}";
    }
}
