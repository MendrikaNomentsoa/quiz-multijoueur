package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Room;
import jakarta.persistence.EntityManager;

import java.util.Comparator;
import java.util.List;

public class ScoreService {

    public List<Participant> calculerClassement(EntityManager em, Long roomId) {
        Room room = em.find(Room.class, roomId);
        if (room == null) {
            throw new RoomIntrouvableException("Aucune room avec l'ID " + roomId);
        }

        return room.getParticipantsJoueurs().stream()
                .sorted(Comparator.comparingInt(Participant::getScore).reversed())
                .toList();
    }

    public static class RoomIntrouvableException extends RuntimeException {
        public RoomIntrouvableException(String message) { super(message); }
    }
}