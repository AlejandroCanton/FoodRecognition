package com.clarifai.android.starter.api.v2;

import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d95wang on 3/8/17.
 */

public class Recipe {

    private String title;
    private int cooktime;
    private int id;
    private String sourceURL;
    private JSONArray ingredients;
    private JSONArray instructions;
    private String ingredientString;
    private List<Ingredient> ingredientList;

    public Recipe(String title, int cooktime, int id, String source, JSONArray ingredients, JSONArray steps) {
        this.title = title;
        this.id = id;
        this. cooktime = cooktime;
        this.sourceURL = source;
        this.ingredients = ingredients;
        this.instructions = steps;
        ingredientList = new ArrayList<>();
    }

    public float calculateWaterMark(){

        List<Ingredient> ingredientList = this.ingredientList;
        Ingredient ingredient;
        float waterMark;
        float waterMarkCount = 0;
        int zerosCount = 0;

        for (int i = 0; i < ingredientList.size(); i++)
        {
            ingredient = ingredientList.get(i);
            waterMark = ingredient.getWater();

            if (waterMark == 0){
                zerosCount++;
            }else {
                waterMarkCount = waterMarkCount + waterMark;
            }
        }

        if (zerosCount > (ingredientList.size() / 2)){
            return -1;
        }else {
            return waterMarkCount;
        }
    }

    public float calculateCarbonPrint() {

        List<Ingredient> ingredientList = this.ingredientList;
        Ingredient ingredient;
        float carbonPrintCount = 0;
        int zerosCount = 0;

        for (int i = 0; i < ingredientList.size(); i++)
        {
            ingredient = ingredientList.get(i);
            float carbonPrint = ingredient.getCarbon();

            if (carbonPrint == 0){
                zerosCount++;
            }else {
                carbonPrintCount += carbonPrint;
            }
        }

        if (zerosCount > (ingredientList.size() / 2)){
            return -1;
        }else {
            return carbonPrintCount;
        }
    }

    public void setIngredientString(String ingredientString) {
        this.ingredientString = ingredientString;
    }

    public String getIngredientString() {
        return ingredientString;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void addIngredientToList(Ingredient ingredient){
        ingredientList.add(ingredient);
    }

    public String getTitle() {
        return title;
    }

    public int getCooktime() {
        return cooktime;
    }

    public int getID() {
        return id;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public String[] getIngredients() throws JSONException {
        String[] output = new String[ingredients.length()];
        for (int i = 0; i < ingredients.length(); ++i) {
            String outputString = "";
            JSONObject ingredient = ingredients.getJSONObject(i);
            outputString += ingredient.getInt("amount") + " ";
            //outputString += ingredient.getString("unitLong") + " ";
            outputString += ingredient.getString("name");
            output[i] = outputString;
        }
        return output;
    }

    public String[] getSteps() throws JSONException {
        JSONObject instructionSet = instructions.getJSONObject(0);
        JSONArray steps = instructionSet.getJSONArray("steps");
        String[] output = new String[steps.length()];
        for (int i = 0; i < steps.length(); ++i) {
            JSONObject step = steps.getJSONObject(i);
            String outputString = step.getString("step");
            output[i] = outputString;
        }
        return output;
    }


}
