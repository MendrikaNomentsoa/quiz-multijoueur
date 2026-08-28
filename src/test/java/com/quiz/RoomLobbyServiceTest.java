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
    private EntityTransaction tx;
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
        tx = em.getTransaction();
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

    @Test
    void rejoindreUnCodeInexistantLeveUneException (){
        tx.begin();
        assertThrows(RoomLobbyService.RoomIntrouvableException.class,
            () -> service.rejoindreRoom(em, "INCONNUE", "Alice"));
            tx.rollback();
    }
    @Test
    void rejoindreUneRoomDejaDemarrerEstRefuser (){
        Quiz quiz  = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
            .setMaxResults(1)
            .getSingleResult();

        tx.begin();
        Room room =new Room("STARTED", quiz);
        room.setStatut(StatutRoom.EN_COURS);
        em.persist(room);
        tx.commit();

        tx.begin();
        assertThrows(RoomLobbyService.RoomNonRejoignableException.class,
            () -> service.rejoindreRoom(em, "STARTED", "Eddys")
        );
        tx.rollback();
    }
}

