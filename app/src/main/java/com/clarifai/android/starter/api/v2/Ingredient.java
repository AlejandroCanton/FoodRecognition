package com.clarifai.android.starter.api.v2;

/**
 * Created by Canton on 21/05/2018.
 */

public class Ingredient {


    String name;
    float water;
    float carbon;

    public Ingredient(String name, String carbon, String water){
        this.name = name;
        this.water = Float.parseFloat(water);
        this.carbon = Float.parseFloat(carbon);
    }

    public String getName() {
        return name;
    }

    public float getCarbon() {
        return carbon;
    }

    public float getWater() {
        return water;
    }
}
