package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Room;
import com.quiz.model.StatutRoom;
import jakarta.persistence.NoResultException;
import jakarta.persistence.EntityManager;

public class RoomLobbyService {

    public Participant rejoindreRoom (EntityManager em, String code, String pseudo){
        Room room;
        try {
            room = em.createQuery("SELECT r FROM Room r WHERE r.code = :code", Room.class)
            .setParameter("code", code)
            .getSingleResult();
        }catch(NoResultException e){
            throw new RoomIntrouvableException("No room found whit code: " + code);
        }if (room.getStatut() != StatutRoom.EN_ATTENTE){
            throw new RoomNonRejoignableException ("Room with code :" + code + "dont accept another player (statut : "+ room.getStatut()+")");
        }
        Participant participant = new Participant(pseudo, false, true);
        room.ajouterParticipant(participant);
        em.persist(participant);

        return participant;
    }
    public static class RoomIntrouvableException extends RuntimeException{
        public RoomIntrouvableException(String message){
            super(message);
        }
    }
    public static class RoomNonRejoignableException extends RuntimeException{
        public RoomNonRejoignableException (String message){
            super(message);
        }
    }
}