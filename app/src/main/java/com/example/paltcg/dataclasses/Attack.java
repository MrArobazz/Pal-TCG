package com.example.paltcg.dataclasses;

import androidx.annotation.NonNull;

public class Attack {

    String name;

    Integer degats;


    public Attack(String name, Integer degats) {
        this.name = name;
        this.degats = degats;
    }

    public String getName() {
        return name;
    }

    public Integer getDegats() {
        return degats;
    }

    @NonNull
    @Override
    public String toString() {
        return "[NAME : " + name + ";DGTS : " + degats + "]";
    }
}
