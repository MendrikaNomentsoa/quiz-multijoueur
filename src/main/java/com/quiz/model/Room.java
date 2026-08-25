package com.quiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Une partie en cours ou a venir, liee a un Quiz existant de la banque.
 * La Room ne cree jamais de contenu de quiz : elle y fait seulement reference.
 */
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code d'invitation partage aux joueurs, ex: "AB12CD" */
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRoom statut = StatutRoom.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    /** Index de la question en cours dans quiz.getQuestions() */
    @Column(nullable = false)
    private int questionCourante = 0;

    private LocalDateTime timestampDebutQuestion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public Room() {
    }

    public Room(String code, Quiz quiz) {
        this.code = code;
        this.quiz = quiz;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StatutRoom getStatut() {
        return statut;
    }

    public void setStatut(StatutRoom statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public int getQuestionCourante() {
        return questionCourante;
    }

    public void setQuestionCourante(int questionCourante) {
        this.questionCourante = questionCourante;
    }

    public LocalDateTime getTimestampDebutQuestion() {
        return timestampDebutQuestion;
    }

    public void setTimestampDebutQuestion(LocalDateTime timestampDebutQuestion) {
        this.timestampDebutQuestion = timestampDebutQuestion;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void ajouterParticipant(Participant p) {
        participants.add(p);
        p.setRoom(this);
    }

    /** Renvoie uniquement les participants qui jouent (excluant un host non-joueur). */
    @Transient
    public List<Participant> getParticipantsJoueurs() {
        return participants.stream().filter(Participant::isEstJoueur).toList();
    }

    /** Renvoie la Question actuellement active dans le quiz de cette room. */
    @Transient
    public Question getQuestionActive() {
        List<Question> questions = quiz.getQuestions();
        if (questionCourante < 0 || questionCourante >= questions.size()) {
            return null;
        }
        return questions.get(questionCourante);
    }

    @Override
    public String toString() {
        return "Room{id=" + id + ", code='" + code + "', statut=" + statut + "}";
    }
}
