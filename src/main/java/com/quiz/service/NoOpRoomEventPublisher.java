package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Room;

public class NoOpRoomEventPublisher implements RoomEventPublisher {

    @Override
    public void publierParticipantRejoint(Room room, Participant participant) {
        // Intentionnellement vide : rien a faire hors du conteneur WildFly.
    }
}