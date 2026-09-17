package com.quiz.rest;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider 
public class CorsFilter implements ContainerResponseFilter{

    @Override 
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException{
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");        
    }
}
//permet a une app front (sur autre serveur ) de consommer l'api rest sans blocage par les navigateur (CORS)