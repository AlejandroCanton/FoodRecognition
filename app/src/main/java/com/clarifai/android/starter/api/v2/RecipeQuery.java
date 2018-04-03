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

import java.util.ArrayList;
import java.util.List;


public class RecipeQuery extends AsyncTask {

    private int recipeID;
    private String url;
    private JSONArray jsonArray;
    private Context context;
    private List<String> results = new ArrayList<>();


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
                    .header("X-Mashape-Key", context.getResources().getString(R.string.spoonacular_api_key))
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
        App state = App.get();
        state.setCurrentRecipe(jsonArray);

        try {

            Recipe recipe = state.makeRecipe();

            String uri = recipe.getSourceURL();
            int time = recipe.getCooktime();

            String stuff = "asdas " + time;

            String[] ingredients = recipe.getIngredients();


            for (int i = 0; i < ingredients.length; i++)
            {
                Log.i("RECIPE ", ingredients[i]);
                matchString(ingredients[i]);
            }

            Log.i("RECIPE ", String.valueOf(results));


            final Uri uriParsed = Uri.parse(uri);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uriParsed);
                    context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void matchString (String searchMe){


        IngredientDataSingleton singleton = IngredientDataSingleton.getInstance();

        List<String> listOfFood = singleton.getIngredientData();


        for (String findMe : listOfFood)
        {
            int searchMeLength = searchMe.length();
            int findMeLength = findMe.length();
            boolean foundIt = false;
            for (int i = 0;
                 i <= (searchMeLength - findMeLength);
                 i++) {
                if (searchMe.regionMatches(i, findMe, 0, findMeLength)) {
                    foundIt = true;

                    String print = (searchMe.substring(i, i + findMeLength));


                    if(results.size() == 0)
                    {
                        results.add(print);
                        Log.i("FIRST MATCH", print);
                    }
                    else{

                        if (print.contains(results.get(results.size()-1)))
                        {
                            results.remove(results.size()-1);
                            results.add(print);
                            Log.i("MATCH ", "El nuevo contiene al anterior, se reemplaza con este " + print);
                        }
                        else if (!results.get(results.size()-1).contains(print))
                        {
                            Log.i("MATCH ", "No se conteiene nada, se anade este  " + print);
                            results.add(print);
                        }
                        else
                        {
                            Log.i("MATCH ", "El viejo contiene al nuevo, no se hace nada con este  " + print);

                        }

                    }


                    break;
                }
            }
            if (!foundIt)
            {
                //Log.i("MATCH", "NOT FOUND");

            }
        }

    }



}
