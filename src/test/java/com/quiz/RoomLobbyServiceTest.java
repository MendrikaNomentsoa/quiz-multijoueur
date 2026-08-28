package com.quiz;

import com.quiz.model.*;
import com.quiz.service.RoomLobbyService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class RoomLobbyServiceTest {

    private static EntityManagerFactory  emf;
    private EntityManager em;
    private final RoomLobbyService service = new RoomLobbyService();

    @BeforeAll
    static void ouvrirFactory(){
        emf= Persistence.createEntityManagerFactory("quizPU-local");
    }

    @AfterAll
    static void fermerFactory(){
        emf.close();
    }
    @BeforeEach
    void ouvrirEntityManager(){
        em = emf.createEntityManager();
    }
    @AfterEach
    void fermerEntityManager(){
        em.close();
    }

    @Test
    void unJoueurPeutRejoindreUneRoomEnAttente(){
        //preparer situation de deart , Room efa misy
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q ", Quiz.class)// ty zan maka quiz fotsiny anaty base , on vas seulement utiliser son exisatnce
        .setMaxResults(1)
        .getSingleResult();

        EntityTransaction tx =em.getTransaction();
        tx.begin();
        Room room = new Room("ABC123", quiz);// quiz et ty le nalaina teo ambony mba hahafeno ny critere creation room oe code + quiz 
        em.persist(room);
        tx.commit();

        // atsoina le transaction commme un vrai appelant

        tx.begin();
        Participant participant = service.rejoindreRoom(em, "ABC123", "Bob" );//on appele la methode rejoindreRoom , elle vas chercher la room , et on creer Boob
        tx.commit();

        //verification raha corecte

        assertEquals("Bob", participant.getPseudo());
        assertFalse(participant.isEstHost());
        assertTrue(participant.isEstJoueur());
        assertNotNull(participant.getId());
    }
}

