package com.quiz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.service.EvenementJms;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifie le format JSON des evenements JMS, independamment de tout serveur JMS reel.
 * Sert de reference pour le developpeur front-end (structure exacte attendue).
 */
class EvenementJmsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void unEvenementSeSerialiseAvecTypeEtData() throws Exception {
        EvenementJms evenement = new EvenementJms(
                "SCORE_MIS_A_JOUR",
                Map.of("pseudo", "Alice", "score", 300)
        );

        String json = mapper.writeValueAsString(evenement);
        JsonNode node = mapper.readTree(json);

        assertEquals("SCORE_MIS_A_JOUR", node.get("type").asText());
        assertEquals("Alice", node.get("data").get("pseudo").asText());
        assertEquals(300, node.get("data").get("score").asInt());
    }

    @Test
    void onPeutReconstruireLObjetDepuisLeJson() throws Exception {
        String json = "{\"type\":\"PARTICIPANT_REJOINT\",\"data\":{\"pseudo\":\"Bob\",\"estJoueur\":true}}";

        EvenementJms evenement = mapper.readValue(json, EvenementJms.class);

        assertEquals("PARTICIPANT_REJOINT", evenement.getType());
        assertEquals("Bob", evenement.getData().get("pseudo"));
        assertEquals(true, evenement.getData().get("estJoueur"));
    }
}