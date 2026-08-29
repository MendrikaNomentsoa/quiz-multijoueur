package com.quiz;

import com.quiz.model.*;
import com.quiz.service.QuizRunnerService;
import com.quiz.service.ScoreService;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests du deroulement de partie : lancer une question, soumettre une reponse,
 * calculer le classement, passer a la question suivante.
 */
class QuizRunnerServiceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private final QuizRunnerService quizRunnerService = new QuizRunnerService();
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

    @Test
    void deroulementCompletDUnePartie() {
        Quiz quiz = em.createQuery(
                        "SELECT q FROM Quiz q WHERE q.titre = :titre", Quiz.class)
                .setParameter("titre", "Culture generale - Niveau facile")
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("TEST01", quiz);
        em.persist(room);

        Participant joueur = new Participant("Damien", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);

        tx.commit();

        // Lancer la premiere question
        tx.begin();
        Question q1 = quizRunnerService.lancerQuestionSuivante(em, room.getId());
        tx.commit();
        assertNotNull(q1);

        // Repondre correctement et rapidement
        Choix bonneReponse = q1.getBonneReponse();
        tx.begin();
        int points = quizRunnerService.soumettreReponse(em, joueur.getId(), q1.getId(), bonneReponse.getId());
        tx.commit();

        assertTrue(points > 0);
        assertEquals(points, joueur.getScore());

        // Le classement doit contenir le joueur
        List<Participant> classement = scoreService.calculerClassement(em, room.getId());
        assertEquals(1, classement.size());
        assertEquals("Damien", classement.get(0).getPseudo());

        // Passer a la question suivante
        tx.begin();
        Question q2 = quizRunnerService.passerQuestionSuivante(em, room.getId());
        tx.commit();
        assertNotNull(q2);
        assertNotEquals(q1.getId(), q2.getId());
    }

    @Test
    void unHostNonJoueurNePeutPasRepondre() {
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("TEST02", quiz);
        em.persist(room);

        Participant hostNonJoueur = new Participant("Host", true, false);
        room.ajouterParticipant(hostNonJoueur);
        em.persist(hostNonJoueur);

        Question q1 = quizRunnerService.lancerQuestionSuivante(em, room.getId());
        tx.commit();

        Choix choix = q1.getChoix().get(0);

        // soumettreReponse utilise un verrou optimiste (em.find avec LockModeType),
        // qui exige une transaction active -> on en ouvre une pour ce test,
        // et on annule (rollback) puisque l'exception empeche toute ecriture.
        tx.begin();
        assertThrows(QuizRunnerService.ParticipantNonJoueurException.class, () ->
                quizRunnerService.soumettreReponse(em, hostNonJoueur.getId(), q1.getId(), choix.getId()));
        tx.rollback();
    }

    @Test
    void uneReponseHorsDelaiEstRejetee() {
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("TEST03", quiz);
        em.persist(room);

        Participant joueur = new Participant("Chloe", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);

        Question q1 = quizRunnerService.lancerQuestionSuivante(em, room.getId());

        // Simule un temps de debut de question tres ancien -> le delai est forcement depasse
        room.setTimestampDebutQuestion(java.time.LocalDateTime.now().minusHours(1));
        tx.commit();

        Choix choix = q1.getChoix().get(0);

        tx.begin();
        assertThrows(QuizRunnerService.TempsExpireException.class, () ->
                quizRunnerService.soumettreReponse(em, joueur.getId(), q1.getId(), choix.getId()));
        tx.rollback();
    }

    @Test
    void uneDoubleReponseALaMemeQuestionEstBloquee() {
        Quiz quiz = em.createQuery("SELECT q FROM Quiz q", Quiz.class)
                .setMaxResults(1)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Room room = new Room("TEST04", quiz);
        em.persist(room);

        Participant joueur = new Participant("Marc", false, true);
        room.ajouterParticipant(joueur);
        em.persist(joueur);

        Question q1 = quizRunnerService.lancerQuestionSuivante(em, room.getId());
        tx.commit();

        Choix bonneReponse = q1.getBonneReponse();

        // Premiere reponse : acceptee
        tx.begin();
        quizRunnerService.soumettreReponse(em, joueur.getId(), q1.getId(), bonneReponse.getId());
        tx.commit();

        // Deuxieme reponse a la MEME question : doit etre rejetee par la contrainte
        // d'unicite (participant_id, question_id) au moment du commit
        tx.begin();
        assertThrows(Exception.class, () -> {
            quizRunnerService.soumettreReponse(em, joueur.getId(), q1.getId(), bonneReponse.getId());
            tx.commit();
        });
        if (tx.isActive()) {
            tx.rollback();
        }
    }

    @Test
    void lorsqueLeQuizEstTermineLaRoomPasseAuStatutTermine() {
        Quiz quiz = em.createQuery(
                        "SELECT q FROM Quiz q WHERE q.titre = :titre", Quiz.class)
                .setParameter("titre", "Culture generale - Niveau facile")
                .getSingleResult();
        int nombreQuestions = quiz.getNombreQuestions();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Room room = new Room("TEST05", quiz);
        em.persist(room);
        tx.commit();

        // Avance jusqu'a la derniere question
        tx.begin();
        room.setQuestionCourante(nombreQuestions - 1);
        tx.commit();

        // Une question de plus -> plus de question disponible, la room doit se terminer
        tx.begin();
        Question apresLaDerniere = quizRunnerService.passerQuestionSuivante(em, room.getId());
        tx.commit();

        assertNull(apresLaDerniere);
        assertEquals(StatutRoom.TERMINEE, room.getStatut());
    }
}