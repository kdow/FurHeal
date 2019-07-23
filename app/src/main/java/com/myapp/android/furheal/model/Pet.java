package com.myapp.android.furheal.model;

import java.util.ArrayList;
import java.util.List;

public class Pet {

    private String name;
    private List<WeightEntry> weights = new ArrayList<>();

    public Pet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
