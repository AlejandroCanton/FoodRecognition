package com.clarifai.android.starter.api.v2;
/**
 * Created by Macbook on 3/7/17.
 */

public class Dish{
    private int id;
    private String title;
    private String image;


    public Dish(int id, String title, String image){
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public int getID(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getImage(){
        return this.image;
    }
}
