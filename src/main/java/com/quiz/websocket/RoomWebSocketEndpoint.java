package com.quiz.websocket;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
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
 *On cree notre propre JMSContext (via la ConnectionFactory injectee)
 * car le JMSContext injecte (@Inject) est RequestScoped et n'existe
 * PAS dans @OnOpen (hors requete HTTP).
 */

@ServerEndpoint("/ws/room/{code}")
public class RoomWebSocketEndpoint {

    @Resource(lookup = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
   
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
