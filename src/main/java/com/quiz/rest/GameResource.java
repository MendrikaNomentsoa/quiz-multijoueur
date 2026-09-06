package com.quiz.rest;

import com.quiz.model.Question;
import com.quiz.rest.dto.QuestionDto;
import com.quiz.rest.dto.ReponseResultDto;
import com.quiz.rest.dto.SoumettreReponseRequest;
import com.quiz.service.QuizRunnerService;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path ("/room")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class GameResource {

    @Inject 
    private EntityManager em;

    private final QuizRunnerService quizRunnerService = new QuizRunnerService();


    @POST 
    @Path("/{roomId}/questions/next")
    public Response lancerQuestionSuivante(@PathParam("roomId") Long roomId){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Question question = quizRunnerService.lancerQuestionSuivante(em, roomId);
            tx.commit();
              return Response.ok(QuestionDto.depuis(question)).build();
        }catch (QuizRunnerService.RoomIntrouvableException e) {
            tx.rollback();
            throw new NotFoundException(e.getMessage());
        } catch (QuizRunnerService.PlusDeQuestionException e) {
            tx.rollback();
            throw new ClientErrorException(e.getMessage(), Response.Status.CONFLICT);
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @POST 
    @Path ("/{roomId}/questions/advance")
    public Response passerQuestionSuivante(@PathParam ("roomId") Long roomId){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Question suivante = quizRunnerService.passerQuestionSuivante(em, roomId);
            tx.commit();
            if (suivante == null){
                return Response.ok("{\"statut\":\"TERMINEE\"}").build();
            }
            return Response.ok(QuestionDto.depuis(suivante)).build();
        } catch (QuizRunnerService.RoomIntrouvableException e) {
            tx.rollback();
            throw new NotFoundException(e.getMessage());
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }

        }
        @POST
    @Path("/reponses")
    public Response soumettreReponse(SoumettreReponseRequest requete) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            int points = quizRunnerService.soumettreReponse(
                    em, requete.participantId, requete.questionId, requete.choixId);
            tx.commit();
            return Response.ok(new ReponseResultDto(points)).build();
        } catch (QuizRunnerService.ParticipantIntrouvableException
                 | QuizRunnerService.QuestionIntrouvableException e) {
            tx.rollback();
            throw new NotFoundException(e.getMessage());
        } catch (QuizRunnerService.ParticipantNonJoueurException
                 | QuizRunnerService.TempsExpireException
                 | QuizRunnerService.ChoixInvalideException e) {
            tx.rollback();
            throw new ClientErrorException(e.getMessage(), Response.Status.CONFLICT);
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    }
