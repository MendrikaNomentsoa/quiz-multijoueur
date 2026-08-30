package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Room;
import jakarta.jms.Topic;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;

public class JmsRoomEventPublisher implements RoomEventPublisher{

    @Inject
    private JMSContext context;

    @Override
    public void publierParticipantRejoint (Room room , Participant participant){
        Topic topicDeLaRoom =context.createTopic("room." +room.getCode());
        String messsage = "PARTICIPANT_REJOINT: " +participant.getPseudo();
        context.createProducer().send(topicDeLaRoom, messsage);
    }
    
}
