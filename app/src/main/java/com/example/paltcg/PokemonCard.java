package com.example.paltcg;

public class PokemonCard {
    String name;
    Integer image;
    Boolean isEnabled;

    public PokemonCard(String name, Integer image, Boolean isEnabled) {
        this.name = name;
        this.image = image;
        this.isEnabled = isEnabled;
    }


    public String getName() {
        return name;
    }

    public Integer getImage() {
        return image;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }
}
