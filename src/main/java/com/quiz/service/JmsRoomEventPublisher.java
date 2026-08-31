package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Question;
import com.quiz.model.Room;
import jakarta.jms.Topic;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;

import java.util.List;
import java.util.stream.Collectors;

public class JmsRoomEventPublisher implements RoomEventPublisher {

    @Inject
    private JMSContext context;

    @Override
    public void publierParticipantRejoint(Room room, Participant participant) {
        Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
        String messsage = "PARTICIPANT_REJOINT: " + participant.getPseudo();
        context.createProducer().send(topicDeLaRoom, messsage);
    }

    @Override
    public void publierNouvelleQuestion(Room room, Question question) {
        Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
        String message = "NOUVELLE_QUESTION: " + question.getEnonce()
                + " (duree=" + question.getDureeReponseMs() + "ms)";
        context.createProducer().send(topicDeLaRoom, message);
    }

    @Override
    public void publierScoreMisAJour(Room room, Participant participant) {
        Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
        String message = "SCORE_MIS_A_JOUR: " + participant.getPseudo() + "=" + participant.getScore();
        context.createProducer().send(topicDeLaRoom, message);
    }

    @Override
    public void publierClassementMisAJour(Room room, List<Participant> classement) {
        Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
        String message = "CLASSEMENT_MIS_A_JOUR: " + formaterClassement(classement);
        context.createProducer().send(topicDeLaRoom, message);
    }

    @Override
    public void publierFinPartie(Room room, List<Participant> classementFinal) {
        Topic topicDeLaRoom = context.createTopic("room." + room.getCode());
        String message = "FIN_PARTIE: " + formaterClassement(classementFinal);
        context.createProducer().send(topicDeLaRoom, message);
    }

    private String formaterClassement(List<Participant> classement) {
        return classement.stream()
                .map(p -> p.getPseudo() + "=" + p.getScore())
                .collect(Collectors.joining(", "));
    }
}