package com.quiz.service;
//acces lect only dans la banque de quiz
//0 transaction 0 ecriture

import java.util.List;

import com.quiz.model.Quiz;
import com.quiz.model.Theme;

import jakarta.persistence.EntityManager;

public class CatalogueService {
    public  List<Theme>listerTheme(EntityManager em){
        return em.createQuery("SELECT t FROM Theme t", Theme.class)
        .getResultList();
    }

    public List<Quiz> listerQuizParTheme (EntityManager em , Long themeId){
        return em.createQuery("SELECT q FROM Quiz q WHERE q.theme.id = :themeId", Quiz.class)
        .setParameter("themeId", themeId)
        .getResultList();

    }
}
