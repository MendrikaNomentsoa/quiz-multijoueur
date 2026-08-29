package com.quiz.service;

import java.util.Random;

import com.quiz.model.Participant;
import com.quiz.model.Quiz;
import com.quiz.model.Room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class RoomCreationService {
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int LONGEUR_CODE = 6;
    private final Random random = new Random();

    public Room CreateRoom(EntityManager em, Long quizId, String  pseudoHost, boolean hostJoue){
        Quiz quiz;
        try{
            quiz = em.find(Quiz.class, quizId);
            if(quiz == null){
                throw new QuizIntrouvableException("Aucun avec l'ID " + quizId);
            }
        }catch (NoResultException e){
            throw new QuizIntrouvableException("Aucun quiz avec l'ID " + quizId);
        }
        String code = genererCodeUnique(em);

        Room room = new Room (code, quiz);
        em.persist(room);

        Participant host = new Participant(pseudoHost, true, hostJoue);
        room.ajouterParticipant(host);
        em.persist(host);

        return room ;
    }

    private String genererCodeUnique(EntityManager em) {
        String code;
        long nombreRoomsAvecCode;

        do {
            //generer code aleatoir
            code = genererCodeAleatoire();
            nombreRoomsAvecCode = em.createQuery("SELECT COUNT(r) FROM Room r WHERE r.code = :code", Long.class)
            .setParameter("code", code)
            .getSingleResult();
        }while (nombreRoomsAvecCode>0);

        return code;
    }

    // func generer code aleatoire
    private String genererCodeAleatoire(){
        StringBuilder sb = new StringBuilder(LONGEUR_CODE);
        for (int i=0 ; i< LONGEUR_CODE ; i++){
            int index = random.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }
        return sb.toString();
    }

    public static class QuizIntrouvableException extends RuntimeException{
        public QuizIntrouvableException(String message) {
            super(message);
        }
    }
    
}
