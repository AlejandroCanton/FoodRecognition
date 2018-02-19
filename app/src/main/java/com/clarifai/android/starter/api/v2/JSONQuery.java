package com.clarifai.android.starter.api.v2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Macbook on 3/7/17.
 */

public class JSONQuery extends AsyncTask{

    private List<String> ingredients;
    private String url;
    private List<Dish> dishes;
    private JSONArray jsonArray;
    private Context context;

    public JSONQuery(List<String> ingredients, Context context){

        this.ingredients = ingredients;
        this.url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients=";
        this.dishes = new ArrayList<Dish>(5);
        this.context = context;
    }

    public void setUrl(){
        this.url += this.ingredients.get(0);
        if(this.ingredients.size() > 1) {
            for (int i = 1; i < this.ingredients.size(); i++) {
                this.url += "%2C" + this.ingredients.get(i).trim();
            }
        }
        this.url += "&limitLicense=false&number=5&ranking=1";
        Log.d("URL", this.url);
    }

    public List<Dish> getDishes(){
        return this.dishes;
    }

    public void addDishes() throws JSONException {
        for(int i = 0; i < 5; i++) {
            JSONObject dish = this.jsonArray.getJSONObject(i);
            String x = dish.toString();
            int id = dish.getInt("id");
            String title = dish.getString("title");
            String image = dish.getString("image");
            this.dishes.add(i, new Dish(id, title, image));
        }
    }

    @Override
    protected Void doInBackground(Object[] params) {

        try{
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .header("X-Mashape-Key", "YOUR_SPOONACULAR_API_KEY_HERE")
                    .header("Accept", "application/json")
                    .asJson();
            this.jsonArray = response.getBody().getArray();
            this.addDishes();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        App state = App.get();
        state.setDishes(getDishes());
        super.onPostExecute(o);
        Intent i = new Intent(context, RecipeListActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
