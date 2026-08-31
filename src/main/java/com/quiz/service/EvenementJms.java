package com.quiz.service;

import java.util.Map;

/**
 * Enveloppe generique pour un evenement diffuse sur un topic JMS.
 * Serialisee en JSON avant envoi, ex :
 * {"type":"SCORE_MIS_A_JOUR","data":{"pseudo":"Alice","score":300}}
 *
 * Cote client (React), il suffit de faire JSON.parse(message) et de
 * regarder le champ "type" pour savoir comment traiter "data".
 */
public class EvenementJms {

    private String type;
    private Map<String, Object> data;

    public EvenementJms() {
        // constructeur vide requis par Jackson pour la (de)serialisation
    }

    public EvenementJms(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}