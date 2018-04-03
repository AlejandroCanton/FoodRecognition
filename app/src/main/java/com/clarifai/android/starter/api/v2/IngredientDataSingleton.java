package com.clarifai.android.starter.api.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canton on 29/03/2018.
 */

public class IngredientDataSingleton {

    private static IngredientDataSingleton instance;

    private List<String> ingredientList;


    protected IngredientDataSingleton() {}

    public static IngredientDataSingleton getInstance(){
        if(instance==null)
                instance= new IngredientDataSingleton();
        return instance;
    }


    public void setIngredientData(List<String> data){
        this.ingredientList = data;
    }

    public List getIngredientData(){
        return this.ingredientList;
    }
}
