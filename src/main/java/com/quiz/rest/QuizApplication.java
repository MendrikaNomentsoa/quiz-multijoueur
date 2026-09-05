package com.quiz.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath ("/api")
public class QuizApplication extends Application {
    // Cette classe est vide, mais elle est necessaire pour que le serveur WildFly sache que l'application expose des services REST
}