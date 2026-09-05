package com.quiz.jpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped 
public class EntityManagerProducer {

    @Produces 
    @ApplicationScoped 
    public EntityManagerFactory creerFactory(){
        return Persistence.createEntityManagerFactory("quizPU-local");

    }
    public void fermerFactory(@Disposes EntityManagerFactory emf){
        if(emf.isOpen()){
            emf.close();
        }
    }

    @Produces 
    @RequestScoped
    public EntityManager creerEntityManager(EntityManagerFactory emf){
        return emf.createEntityManager();
    }
    public void fermerEntityManager(@Disposes EntityManager em){
        if(em.isOpen()){
            em.close();
        }
    }
    
}
