package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Room;

public interface RoomEventPublisher {
    void publierParticipantRejoint(Room room, Participant participant);
}