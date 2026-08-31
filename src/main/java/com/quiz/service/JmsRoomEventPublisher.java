package com.quiz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.model.Participant;
import com.quiz.model.Question;
import com.quiz.model.Room;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Publie les evenements de la room sur le topic JMS "room.{code}",
 * au format JSON : {"type": "...", "data": {...}}.
 */
public class JmsRoomEventPublisher implements RoomEventPublisher {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Inject
    private JMSContext context;

    @Override
    public void publierParticipantRejoint(Room room, Participant participant) {
        envoyer(room, "PARTICIPANT_REJOINT", Map.of(
                "participantId", participant.getId(),
                "pseudo", participant.getPseudo(),
                "estHost", participant.isEstHost(),
                "estJoueur", participant.isEstJoueur()
        ));
    }

    @Override
    public void publierNouvelleQuestion(Room room, Question question) {
        envoyer(room, "NOUVELLE_QUESTION", Map.of(
                "questionId", question.getId(),
                "enonce", question.getEnonce(),
                "dureeReponseMs", question.getDureeReponseMs(),
                "choix", question.getChoix().stream()
                        .map(c -> Map.of("id", c.getId(), "texte", c.getTexte()))
                        .collect(Collectors.toList())
                // volontairement : on n'envoie pas "estCorrect" au client avant qu'il reponde
        ));
    }

    @Override
    public void publierScoreMisAJour(Room room, Participant participant) {
        envoyer(room, "SCORE_MIS_A_JOUR", Map.of(
                "participantId", participant.getId(),
                "pseudo", participant.getPseudo(),
                "score", participant.getScore()
        ));
    }

    @Override
    public void publierClassementMisAJour(Room room, List<Participant> classement) {
        envoyer(room, "CLASSEMENT_MIS_A_JOUR", Map.of(
                "classement", versClassementJson(classement)
        ));
    }

    @Override
    public void publierFinPartie(Room room, List<Participant> classementFinal) {
        envoyer(room, "FIN_PARTIE", Map.of(
                "classementFinal", versClassementJson(classementFinal)
        ));
    }

    private List<Map<String, Object>> versClassementJson(List<Participant> classement) {
        return classement.stream()
                .map(p -> Map.<String, Object>of(
                        "participantId", p.getId(),
                        "pseudo", p.getPseudo(),
                        "score", p.getScore()
                ))
                .collect(Collectors.toList());
    }

    private void envoyer(Room room, String type, Map<String, Object> data) {
        try {
            String json = JSON.writeValueAsString(new EvenementJms(type, data));
            Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
            context.createProducer().send(topicDeLaRoom, json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erreur de serialisation JSON pour l'evenement " + type, e);
        }
    }
}