package com.quiz.rest;
import com.quiz.model.Quiz;
import com.quiz.model.Theme;
import com.quiz.service.CatalogueService;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/catalogue")
@Produces(MediaType.APPLICATION_JSON)

public class CatalogueResource {

    @Inject
    private EntityManager em;

    private final CatalogueService catalogueService = new CatalogueService();

    @GET
    @Path ("/themes")
    public List<ThemeDto> listerThemes() {
        return catalogueService.listerTheme(em).stream().map(ThemeDto::depuis).toList();
    }
    @GET
    @Path("/themes/{themeId}/quiz")
    public List<QuizDto> listerQuizDuTheme(@PathParam("themeId") Long themeId) {
        List<Quiz> quizList = catalogueService.listerQuizParTheme(em, themeId);
        if (quizList.isEmpty()){
            throw new NotFoundException();
        }
        return quizList.stream().map(QuizDto::depuis).toList();
    }
    public static class ThemeDto{
        public Long id;
        public String nom;
        public String description;

        public static ThemeDto depuis(Theme t){
            ThemeDto dto = new ThemeDto();
            dto.id = t.getId();
            dto.nom = t.getNom();
            dto.description = t.getDescription();
            return dto;
        }
    }
    public static class QuizDto{
        public Long id;
        public String titre;
        public Long themeId;
        public int nombreQuestions;

        public static QuizDto depuis(Quiz q){
            QuizDto dto = new QuizDto();
            dto.id = q.getId();
            dto.titre = q.getTitre();
            dto.themeId = q.getTheme().getId();
            dto.nombreQuestions = q.getQuestions().size();
            return dto;
        }
    }
}
