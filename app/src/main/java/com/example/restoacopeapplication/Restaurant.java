package com.example.restoacopeapplication;

public class Restaurant {
    private String name;
    private String adresse;
    private String photos;
    private String restaurateurId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getRestaurateurId() {
        return restaurateurId;
    }

    public void setRestaurateurId(String restaurateurId) {
        this.restaurateurId = restaurateurId;
    }
}