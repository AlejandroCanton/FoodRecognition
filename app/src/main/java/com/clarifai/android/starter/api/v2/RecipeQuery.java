package com.clarifai.android.starter.api.v2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by d95wang on 3/8/17.
 */

public class RecipeQuery extends AsyncTask {

    private int recipeID;
    private String url;
    private JSONArray jsonArray;
    private Context context;

    public RecipeQuery(int id, Context context){
        Log.d("Constructor", "in here!");
        this.recipeID = id;
        this.url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/";
        this.context = context;
    }

    public void setUrl(){
        this.url += recipeID;
        this.url += "/information?includeNutrition=false";
        Log.d("URL", this.url);
    }

    @Override
    protected Void doInBackground(Object[] params) {

        try{
            Log.d("TAG", "in here");
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .header("X-Mashape-Key", "cImbt393XOmshEN3Hr6fCmTpuN7dp1On8Oejsnq5uIbToxWY7T")
                    .header("Accept", "application/json")
                    .asJson();
            this.jsonArray = response.getBody().getArray();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        App.get().setCurrentRecipe(jsonArray);

        App state = App.get();

        try {
            String uri = state.makeRecipe();

            final Uri uriParsed = Uri.parse(uri);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uriParsed);
                    context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
