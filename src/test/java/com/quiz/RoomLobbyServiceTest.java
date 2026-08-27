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
        // Apiko aveo fa valaka be 
    }
}

