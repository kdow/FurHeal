package com.myapp.android.furheal.model;

import java.util.Date;

public class WeightLog {

    private double weight;
    private String unit;
    private Date date;

    public WeightLog(double weight, String unit, Date date) {
        this.weight = weight;
        this.unit = unit;
        this.date = date;
    }
}
