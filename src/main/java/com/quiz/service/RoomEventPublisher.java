package com.quiz.service;

import com.quiz.model.Participant;
import com.quiz.model.Question;
import com.quiz.model.Room;

import java.util.List;

/**
 * Contrat de publication des evenements temps reel d'une room (voir conception, section 6.6).
 *
 * Une room = un topic JMS (room.{code}) cote implementation reelle (JmsRoomEventPublisher).
 * Le NoOpRoomEventPublisher sert d'implementation par defaut pour les tests, sans avoir
 * besoin d'un serveur JMS.
 */
public interface RoomEventPublisher {

    /** PARTICIPANT_REJOINT : un participant vient de rejoindre le lobby. */
    void publierParticipantRejoint(Room room, Participant participant);

    /** NOUVELLE_QUESTION : une question est diffusee aux participants joueurs. */
    void publierNouvelleQuestion(Room room, Question question);

    /** SCORE_MIS_A_JOUR : le score d'un participant vient de changer. */
    void publierScoreMisAJour(Room room, Participant participant);

    /** CLASSEMENT_MIS_A_JOUR : classement intermediaire apres cloture d'une question. */
    void publierClassementMisAJour(Room room, List<Participant> classement);

    /** FIN_PARTIE : le quiz est termine, classement final. */
    void publierFinPartie(Room room, List<Participant> classementFinal);
}