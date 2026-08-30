package com.quiz;

import com.quiz.model.*;
import com.quiz.service.ScoreService;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests dedies au calcul du classement (ScoreService), independamment
 * du deroulement complet d'une partie.
 */
class ScoreServiceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private final ScoreService scoreService = new ScoreService();

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
    }

    @AfterEach
    void fermerEntityManager() {
        em.close();
    }

    private Quiz recupererUnQuiz() {
        return em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Test
    void leClassementEstTrieParScoreDecroissant() {
        Quiz quiz = recupererUnQuiz();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("SCORE1", quiz);
        em.persist(room);

        Participant alice = new Participant("Alice", false, true);
        room.ajouterParticipant(alice);
        em.persist(alice);
        alice.setScore(300);

        Participant bob = new Participant("Bob", false, true);
        room.ajouterParticipant(bob);
        em.persist(bob);
        bob.setScore(900);

        Participant chloe = new Participant("Chloe", false, true);
        room.ajouterParticipant(chloe);
        em.persist(chloe);
        chloe.setScore(600);

        tx.commit();

        List<Participant> classement = scoreService.calculerClassement(em, room.getId());

        assertEquals(3, classement.size());
        assertEquals("Bob", classement.get(0).getPseudo());   // 900
        assertEquals("Chloe", classement.get(1).getPseudo()); // 600
        assertEquals("Alice", classement.get(2).getPseudo()); // 300
    }

    @Test
    void unHostNonJoueurNApparaitJamaisDansLeClassement() {
        Quiz quiz = recupererUnQuiz();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("SCORE2", quiz);
        em.persist(room);

        Participant host = new Participant("Animateur", true, false); // estJoueur = false
        room.ajouterParticipant(host);
        em.persist(host);
        host.setScore(0);

        Participant joueur = new Participant("Diane", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);
        joueur.setScore(150);

        tx.commit();

        List<Participant> classement = scoreService.calculerClassement(em, room.getId());

        assertEquals(1, classement.size());
        assertEquals("Diane", classement.get(0).getPseudo());
        assertTrue(classement.stream().noneMatch(Participant::isEstHost));
    }

    @Test
    void unHostQuiJoueApparaitDansLeClassement() {
        Quiz quiz = recupererUnQuiz();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("SCORE3", quiz);
        em.persist(room);

        Participant hostJoueur = new Participant("HostJoueur", true, true); // estHost ET estJoueur
        room.ajouterParticipant(hostJoueur);
        em.persist(hostJoueur);
        hostJoueur.setScore(500);

        tx.commit();

        List<Participant> classement = scoreService.calculerClassement(em, room.getId());

        assertEquals(1, classement.size());
        assertEquals("HostJoueur", classement.get(0).getPseudo());
    }

    @Test
    void uneRoomSansJoueurRenvoieUnClassementVide() {
        Quiz quiz = recupererUnQuiz();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("SCORE4", quiz);
        em.persist(room);
        // Aucun participant ajoute

        tx.commit();

        List<Participant> classement = scoreService.calculerClassement(em, room.getId());

        assertTrue(classement.isEmpty());
    }

    @Test
    void uneRoomInexistanteLeveUneException() {
        assertThrows(ScoreService.RoomIntrouvableException.class, () ->
                scoreService.calculerClassement(em, 999_999L));
    }
}