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
    private int position;


    public RecipeQuery(int id,  int position, Context context){
        Log.d("Constructor", "in here!");
        this.recipeID = id;
        this.url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/";
        this.context = context;
        this.position = position;
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


        Intent intent = new Intent(context, RecipeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        try {

            Recipe recipe = state.makeRecipe();
            state.setRecipe(recipe,this.position);

            String uri = recipe.getSourceURL();
            int time = recipe.getCooktime();

            String stuff = "asdas " + time;

            String[] ingredients = recipe.getIngredients();


            for (int i = 0; i < ingredients.length; i++)
            {
                Ingredient ingredient = matchString(ingredients[i]);
                Log.i("RECIPE ", ingredients[i]);
                if(ingredient != null)
                {
                    recipe.addIngredientToList(ingredient);
                }
            }


            String ingredientString = String.valueOf(results);
            Log.i("Full RECIPE very list ", ingredientString);

            //state.addIngredientList(state.countRecipe, String.valueOf(results));

            recipe.setIngredientString(ingredientString);

            //final Uri uriParsed = Uri.parse(uri);
            //Intent intent = new Intent(Intent.ACTION_VIEW, uriParsed);
            //context.startActivity(intent);

            state.countRecipe++;

            if (state.countRecipe == 5)
            {
                context.startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private Ingredient matchString (String searchMe){


        IngredientDataSingleton singleton = IngredientDataSingleton.getInstance();

        List<Ingredient> listOfIngredients = singleton.getIngredientList();


        for (int j = 0 ; j < listOfIngredients.size(); j++)
        {
            Ingredient ingredient = listOfIngredients.get(j);
            String findMe = ingredient.getName();
            float waterM = ingredient.getWater();
            float carbonP = ingredient.getCarbon();

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
                            return null;

                        }

                    }

                    return ingredient;


                }
            }
            if (!foundIt)
            {
                //Log.i("MATCH", "NOT FOUND");

            }
        }
        return null;
    }



}
