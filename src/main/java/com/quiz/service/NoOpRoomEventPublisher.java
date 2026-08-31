package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Question;
import com.quiz.model.Room;

import java.util.List;

public class NoOpRoomEventPublisher implements RoomEventPublisher {

    @Override
    public void publierParticipantRejoint(Room room, Participant participant) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }

    @Override
    public void publierNouvelleQuestion(Room room, Question question) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }

    @Override
    public void publierScoreMisAJour(Room room, Participant participant) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }

    @Override
    public void publierClassementMisAJour(Room room, List<Participant> classement) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }

    @Override
    public void publierFinPartie(Room room, List<Participant> classementFinal) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }
}