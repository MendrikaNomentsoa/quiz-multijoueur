package com.quiz.rest.dto;

import com.quiz.model.Participant;
public class ParticipantDto {
    public Long id;
    public String pseudo;
    public boolean estHost;
    public boolean estJoueur;

    public static ParticipantDto depuis(Participant participant){
        ParticipantDto dto = new ParticipantDto();
        dto.id = participant.getId();
        dto.pseudo = participant.getPseudo();
        dto.estHost = participant.isEstHost();
        dto.estJoueur = participant.isEstJoueur();  
        return dto;
    }

    
}
