package com.clarifai.android.starter.api.v2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public Recipe(String title, int cooktime, int id, String source, JSONArray ingredients, JSONArray steps) {
        this.title = title;
        this.id = id;
        this. cooktime = cooktime;
        this.sourceURL = source;
        this.ingredients = ingredients;
        this.instructions = steps;
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
            outputString += ingredient.getString("unitLong") + " ";
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
