package com.clarifai.android.starter.api.v2;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canton on 29/03/2018.
 */

public class IngredientDataSingleton {

    private static IngredientDataSingleton instance;

    private List<Ingredient> ingredientList;
    private List<String> ingredientNames;


    protected IngredientDataSingleton() {}

    public static IngredientDataSingleton getInstance(){
        if(instance==null)
                instance= new IngredientDataSingleton();
        return instance;
    }


    public void setIngredientNames(List<String> data){
        this.ingredientNames = data;
    }

    public List getIngredientNames(){
        return this.ingredientNames;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList =ingredientList;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public float getWaterMark(int i){
        return ingredientList.get(i).getWater();
    }

    public float getCarbonPrint(int i){
        return ingredientList.get(i).getCarbon();
    }

}
