package com.quiz.model;

import jakarta.persistence.*;

/**
 * Une proposition de reponse pour une Question donnee.
 */
@Entity
@Table(name = "choix")
public class Choix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String texte;

    @Column(nullable = false)
    private boolean estCorrect;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Choix() {
    }

    public Choix(String texte, boolean estCorrect) {
        this.texte = texte;
        this.estCorrect = estCorrect;
    }

    public Long getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public boolean isEstCorrect() {
        return estCorrect;
    }

    public void setEstCorrect(boolean estCorrect) {
        this.estCorrect = estCorrect;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Choix{id=" + id + ", texte='" + texte + "', estCorrect=" + estCorrect + "}";
    }
}
