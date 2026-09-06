package com.quiz.rest.dto;

import com.quiz.model.Question;
import java.util.List;
import java.util.stream.Collectors;

    public class QuestionDto{
        public Long id;
        public String enonce;
        public int dureeReponseMs;
        public List<ChoixDto> choix;

        public static QuestionDto depuis(Question q){
            QuestionDto dto = new QuestionDto();
            dto.id = q.getId();
            dto.enonce = q.getEnonce();
            dto.dureeReponseMs = q.getDureeReponseMs();
            dto.choix = q.getChoix().stream().map(ChoixDto::depuis).collect(Collectors.toList());
            return dto;

        }
        public static class ChoixDto{
            public Long id;
            public String texte;

            public static ChoixDto depuis (com.quiz.model.Choix c ){
                ChoixDto dto = new ChoixDto();
                dto.id = c.getId();
                dto.texte = c.getTexte();
                return dto;
            }
        }

    }
    
