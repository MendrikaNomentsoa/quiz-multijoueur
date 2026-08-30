package com.quiz.service;

import com.quiz.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.time.Duration;
import java.time.LocalDateTime;

public class QuizRunnerService {

    public Question lancerQuestionSuivante(EntityManager em, Long roomId) {
        Room room = em.find(Room.class, roomId);
        if (room == null) {
            throw new RoomIntrouvableException("Aucune room avec l'ID " + roomId);
        }

        Question question = room.getQuestionActive();
        if (question == null) {
            throw new PlusDeQuestionException("Le quiz de la room " + room.getCode() + " est termine");
        }

        room.setStatut(StatutRoom.EN_COURS);
        room.setTimestampDebutQuestion(LocalDateTime.now());

        return question;
    }

    public int soumettreReponse(EntityManager em, Long participantId, Long questionId, Long choixId) {
        Participant participant = em.find(Participant.class, participantId, LockModeType.OPTIMISTIC);
        if (participant == null) {
            throw new ParticipantIntrouvableException("Aucun participant avec l'ID " + participantId);
        }
        if (!participant.isEstJoueur()) {
            throw new ParticipantNonJoueurException(participant.getPseudo() + " n'est pas joueur dans cette room");
        }

        Room room = participant.getRoom();
        Question question = em.find(Question.class, questionId);
        if (question == null) {
            throw new QuestionIntrouvableException("Aucune question avec l'ID " + questionId);
        }

        long tempsEcouleMs = Duration.between(room.getTimestampDebutQuestion(), LocalDateTime.now()).toMillis();
        if (tempsEcouleMs > question.getDureeReponseMs()) {
            throw new TempsExpireException("Temps ecoule pour la question " + questionId);
        }

        Choix choix = em.find(Choix.class, choixId);
        if (choix == null || !choix.getQuestion().getId().equals(questionId)) {
            throw new ChoixInvalideException("Le choix " + choixId + " n'appartient pas a la question " + questionId);
        }

        boolean correcte = choix.isEstCorrect();
        int points = correcte ? calculerPoints(tempsEcouleMs, question.getDureeReponseMs()) : 0;

        Reponse reponse = new Reponse(participant, question, choix, tempsEcouleMs, correcte, points);
        em.persist(reponse); // la contrainte unique (participant_id, question_id) bloque une double reponse

        participant.ajouterPoints(points);

        return points;
    }

    public Question passerQuestionSuivante(EntityManager em, Long roomId) {
        Room room = em.find(Room.class, roomId);
        if (room == null) {
            throw new RoomIntrouvableException("Aucune room avec l'ID " + roomId);
        }

        room.setQuestionCourante(room.getQuestionCourante() + 1);
        Question suivante = room.getQuestionActive();

        if (suivante == null) {
            room.setStatut(StatutRoom.TERMINEE);
            return null;
        }

        room.setTimestampDebutQuestion(LocalDateTime.now());
        return suivante;
    }

    private int calculerPoints(long tempsMs, int dureeMaxMs) {
        double ratio = 1 - ((double) tempsMs / dureeMaxMs);
        return (int) (1000 * Math.max(0, ratio));
    }

    public static class RoomIntrouvableException extends RuntimeException {
        public RoomIntrouvableException(String message) { super(message); }
    }

    public static class ParticipantIntrouvableException extends RuntimeException {
        public ParticipantIntrouvableException(String message) { super(message); }
    }

    public static class ParticipantNonJoueurException extends RuntimeException {
        public ParticipantNonJoueurException(String message) { super(message); }
    }

    public static class QuestionIntrouvableException extends RuntimeException {
        public QuestionIntrouvableException(String message) { super(message); }
    }

    public static class ChoixInvalideException extends RuntimeException {
        public ChoixInvalideException(String message) { super(message); }
    }

    public static class TempsExpireException extends RuntimeException {
        public TempsExpireException(String message) { super(message); }
    }

    public static class PlusDeQuestionException extends RuntimeException {
        public PlusDeQuestionException(String message) { super(message); }
    }
}