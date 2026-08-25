package com.quiz;

import com.quiz.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de fumee : verifie que le modele JPA fonctionne de bout en bout
 * (banque de quiz pre-remplie + creation d'une room + reponse a une question).
 *
 * Sert aussi d'exemple d'utilisation pour les developpeurs B (JMS/React) et C (EJB) :
 * les memes requetes/operations pourront etre reprises telles quelles dans les
 * services EJB (avec @PersistenceContext a la place d'un EntityManagerFactory manuel).
 */
class JpaSmokeTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

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

    @Test
    void laBanqueDeQuizEstChargee() {
        List<Theme> themes = em.createQuery("SELECT t FROM Theme t", Theme.class).getResultList();
        assertEquals(2, themes.size());

        Quiz quiz = em.createQuery(
                        "SELECT q FROM Quiz q WHERE q.titre = :titre", Quiz.class)
                .setParameter("titre", "Culture generale - Niveau facile")
                .getSingleResult();

        assertEquals(3, quiz.getQuestions().size());
        assertEquals(4, quiz.getQuestions().get(0).getChoix().size());
    }

    @Test
    void onPeutCreerUneRoomEtRejoindre() {
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Creation de la room par un host qui choisit aussi de jouer
        Room room = new Room("AB12CD", quiz);
        em.persist(room);

        Participant host = new Participant("Alice", true, true);
        room.ajouterParticipant(host);
        em.persist(host);

        // Un joueur rejoint via le code
        Participant joueur = new Participant("Bob", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);

        tx.commit();

        Room roomRelue = em.createQuery(
                        "SELECT r FROM Room r WHERE r.code = :code", Room.class)
                .setParameter("code", "AB12CD")
                .getSingleResult();

        assertEquals(2, roomRelue.getParticipants().size());
        assertEquals(StatutRoom.EN_ATTENTE, roomRelue.getStatut());
    }

    @Test
    void onPeutSoumettreUneReponseEtMettreAJourLeScore() {
        Quiz quiz = em.createQuery(
                        "SELECT q FROM Quiz q WHERE q.titre = :titre", Quiz.class)
                .setParameter("titre", "Culture generale - Niveau facile")
                .getSingleResult();
        Question question = quiz.getQuestions().get(0); // "Capitale de la France ?"
        Choix bonneReponse = question.getBonneReponse();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("XY99ZZ", quiz);
        em.persist(room);
        room.setStatut(StatutRoom.EN_COURS);
        room.setTimestampDebutQuestion(java.time.LocalDateTime.now());

        Participant joueur = new Participant("Chloe", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);

        // Simule une reponse correcte, rapide -> beaucoup de points
        long tempsReponseMs = 3000;
        int points = calculerPoints(tempsReponseMs, question.getDureeReponseMs());

        Reponse reponse = new Reponse(joueur, question, bonneReponse, tempsReponseMs, true, points);
        em.persist(reponse);

        joueur.ajouterPoints(points);

        tx.commit();

        assertTrue(joueur.getScore() > 0);

        // Verifie que la contrainte d'unicite (participant, question) fonctionne :
        // une deuxieme reponse a la MEME question doit etre rejetee.
        tx.begin();
        Reponse doublon = new Reponse(joueur, question, bonneReponse, 5000, true, 100);
        em.persist(doublon);
        assertThrows(Exception.class, tx::commit);
    }

    /** Meme formule que celle documentee dans la conception (QuizRunnerService). */
    private int calculerPoints(long tempsMs, int dureeMaxMs) {
        double ratio = 1 - ((double) tempsMs / dureeMaxMs);
        return (int) (1000 * Math.max(0, ratio));
    }
}
