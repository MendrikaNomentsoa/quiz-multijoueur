package com.quiz.websocket;

import jakarta.inject.Inject;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


/**
 * Passerelle WebSocket <-> JMS
 *
 * Chaque client React se connecte a une URL du type /ws/room/AB12CD.
 * A l'ouverture, on s'abonne directement au topic JMS de CETTE room
 * (room.AB12CD), puisque les topics sont crees dynamiquement par room
 *
 */

@ServerEndpoint("ws/room/{code}")
public class RoomWebSocketEndpoint {

    @Inject
    private JMSContext context;

    private JMSConsumer consumer;

    @OnOpen
    public void onOpen(Session session,@PathParam("code") String code){
        Topic topicDeLaRoom = context.createTopic("room."+code);
        consumer =context.createConsumer(topicDeLaRoom);

        consumer.setMessageListener(message ->{
            try{
                String texteJson = message.getBody(String.class);
                session.getBasicRemote().sendText(texteJson);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    @OnClose
    public void onClose(){
        if (consumer !=null){
            consumer.close();
        }
    }
    
}
