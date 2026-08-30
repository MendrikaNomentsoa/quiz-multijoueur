package com.quiz;

import com.quiz.model.*;
import com.quiz.service.RoomCreationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoomCreationServiceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;
    private final RoomCreationService service = new RoomCreationService();

    @BeforeAll
    static void ouvrirFactory() {
        emf = Persistence.createEntityManagerFactory("quizPU-local");
    }

    @AfterAll
    static void fermerFactory() {
        emf.close();
    }

    @BeforeEach
    void ouvrirEntityManager() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }

    @AfterEach
    void fermerEntityManager() {
        em.close();
    }

    @Test
    void leHostPeutCreerUneRoomEtDevientPremierParticipant() {
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();

        tx.begin();
        Room room = service.CreateRoom(em, quiz.getId(), "Alice", true);
        tx.commit();

        assertNotNull(room.getId());
        assertNotNull(room.getCode());
        assertEquals(6, room.getCode().length());
        assertEquals(StatutRoom.EN_ATTENTE, room.getStatut());
        assertEquals(1, room.getParticipants().size());

        Participant host = room.getParticipants().get(0);
        assertEquals("Alice", host.getPseudo());
        assertTrue(host.isEstHost());
        assertTrue(host.isEstJoueur());
    }

    @Test
    void creerUneRoomAvecUnQuizInexistantLeveUneException() {
        tx.begin();
        assertThrows(RoomCreationService.QuizIntrouvableException.class,
                () -> service.CreateRoom(em, 9999L, "Bob", false));
        tx.rollback();
    }
}