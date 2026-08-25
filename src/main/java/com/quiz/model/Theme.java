package com.quiz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Categorie regroupant plusieurs quiz (ex : "Cinema", "Sport", "Sciences").
 * Donnee de reference : pre-remplie en base, partagee par toutes les rooms.
 */
@Entity
@Table(name = "theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes = new ArrayList<>();

    public Theme() {
    }

    public Theme(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void ajouterQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setTheme(this);
    }

    @Override
    public String toString() {
        return "Theme{id=" + id + ", nom='" + nom + "'}";
    }
}
