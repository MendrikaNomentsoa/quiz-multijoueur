package com.quiz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Un quiz appartenant a un theme, compose d'une liste ordonnee de questions.
 * Donnee de reference : un meme Quiz peut etre joue par plusieurs Room en parallele.
 */
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Question> questions = new ArrayList<>();

    public Quiz() {
    }

    public Quiz(String titre, Theme theme) {
        this.titre = titre;
        this.theme = theme;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void ajouterQuestion(Question question) {
        question.setOrdre(questions.size());
        questions.add(question);
        question.setQuiz(this);
    }

    public int getNombreQuestions() {
        return questions.size();
    }

    @Override
    public String toString() {
        return "Quiz{id=" + id + ", titre='" + titre + "'}";
    }
}
