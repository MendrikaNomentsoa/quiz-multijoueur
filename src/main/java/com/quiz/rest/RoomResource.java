package com.quiz.rest;

import com.quiz.model.Participant;
import com.quiz.model.Room;
import com.quiz.rest.dto.CreerRoomRequest;
import com.quiz.rest.dto.ParticipantDto;
import com.quiz.rest.dto.RejoindreRoomRequest;
import com.quiz.rest.dto.RoomDto;
import com.quiz.service.RoomCreationService;
import com.quiz.service.RoomLobbyService;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path ("/room")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)

public class RoomResource {
    @Inject 
    private EntityManager em;

    // NoOpRoomEventPublisher par defaut : a remplacer par JmsRoomEventPublisher
    // quand l'injection CDI de JMSContext sera disponible sur cette ressource.

    private final RoomCreationService roomCreationService = new RoomCreationService();
    private final RoomLobbyService roomLobbyService = new RoomLobbyService();

    @POST
    public Response creerRoom(CreerRoomRequest requete){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            Room room = roomCreationService.CreateRoom(em, requete.quizId, requete.pseudoHost, requete.hostJoue);
            tx.commit();
            return Response.status(Response.Status.CREATED)
            .entity(RoomDto.depuis(room))
            .build();
        }catch (RoomCreationService.QuizIntrouvableException e){
            tx.rollback();
            throw new NotFoundException(e.getMessage());
        }catch (RuntimeException e){
            if (tx.isActive()){
                tx.rollback();
            }
            throw e;
        }
    }
 @POST
    @Path("/{code}/participants")
    public Response rejoindreRoom(@PathParam("code") String code, RejoindreRoomRequest requete) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Participant participant = roomLobbyService.rejoindreRoom(em, code, requete.pseudo);
            tx.commit();
            return Response.status(Response.Status.CREATED)
                    .entity(ParticipantDto.depuis(participant))
                    .build();
        } catch (RoomLobbyService.RoomIntrouvableException e) {
            tx.rollback();
            throw new NotFoundException(e.getMessage());
        } catch (RoomLobbyService.RoomNonRejoignableException e) {
            tx.rollback();
            throw new ClientErrorException(e.getMessage(), Response.Status.CONFLICT);
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    
}
