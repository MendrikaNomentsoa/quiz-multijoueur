package com.quiz;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quiz.model.Quiz;
import com.quiz.model.Theme;
import com.quiz.service.CatalogueService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class CatalogueServiceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    
    private final CatalogueService service = new CatalogueService();

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
    void onPeutListerLesThemes(){
        //verifie qu'il ya les theme
    List<Theme> themes = service.listerTheme(em);
    assertEquals(2, themes.size());

    }

    @Test
    void onPeutListerLesQuizzesDUnTheme(){
        Theme theme = em.createQuery("SELECT t FROM Theme t WHERE t.nom = :nom", Theme.class)
        .setParameter("nom", "Culture generale")
        .getSingleResult();

        List <Quiz> quizs = service.listerQuizParTheme(em, theme.getId());

        assertEquals(1, quizs.size());

        // recup un theme et verifie qu'il ya 1 quiiz a associer
    }
}
