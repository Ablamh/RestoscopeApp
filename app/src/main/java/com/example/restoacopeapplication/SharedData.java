package com.example.restoacopeapplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedData {

    private static SharedData instance;

    // Champs pour stocker les données
    private String userId;
    private List<String> photos;
    private Map<String, Boolean> mobilitePhysique;
    private Map<String, Boolean> deficienceVisuelle;
    private Map<String, Boolean> deficienceAuditive;

    // Constructeur privé pour le singleton
    private SharedData() {
        mobilitePhysique = new HashMap<>();
        deficienceVisuelle = new HashMap<>();
        deficienceAuditive = new HashMap<>();
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    // Getter et setter pour les données
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photosArray) {
        this.photos = Arrays.asList(photosArray);
    }


    public Map<String, Boolean> getMobilitePhysique() {
        return mobilitePhysique;
    }

    public void setMobilitePhysique(Map<String, Boolean> mobilitePhysique) {
        this.mobilitePhysique = mobilitePhysique;
    }

    public Map<String, Boolean> getDeficienceVisuelle() {
        return deficienceVisuelle;
    }

    public void setDeficienceVisuelle(Map<String, Boolean> deficienceVisuelle) {
        this.deficienceVisuelle = deficienceVisuelle;
    }

    public Map<String, Boolean> getDeficienceAuditive() {
        return deficienceAuditive;
    }

    public void setDeficienceAuditive(Map<String, Boolean> deficienceAuditive) {
        this.deficienceAuditive = deficienceAuditive;
    }
}
