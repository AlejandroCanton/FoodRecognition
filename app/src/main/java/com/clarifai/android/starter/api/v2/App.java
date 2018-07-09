package com.clarifai.android.starter.api.v2;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App extends Application {

  // In a real app, rather than attaching singletons (such as the API client instance) to your Application instance,
  // it's recommended that you use something like Dagger 2, and inject your client instance.
  // Since that would be a distraction here, we will just use a regular singleton.
  private static App INSTANCE;

  private List<String> ingredientString;
  private List<Dish> dishes;
  private List<Recipe> recipes;
  private int position;
  private JSONArray currentRecipe;
  public int countRecipe;

  private ArrayList<String> ingredientList;

  @NonNull
  public static App get() {
    final App instance = INSTANCE;
    if (instance == null) {
      throw new IllegalStateException("App has not been created yet!");
    }
    return instance;
  }

  @Nullable
  private ClarifaiClient client;

  public void setDishes(List<Dish> list) {
    dishes = list;
    recipes = new ArrayList<Recipe>();
    ingredientList = new ArrayList<String>();
    countRecipe = 0;
  }

  public void addIngredientList(int i, String ingredients) {
    this.ingredientList.add(i, ingredients);
  }

    public String getIngredientList(int i) {
        return ingredientList.get(i);
    }

    public List<Dish> getDishes() {
    return dishes;
  }


    public void setRecipe(Recipe recipe, int position) {
        recipes.add(position, recipe);
    }

    public Recipe getRecipe(int position) {
        return recipes.get(position);
    }

    public void setCurrentRecipe(JSONArray arr) {
    currentRecipe = arr;
  }

  public Recipe makeRecipe() throws JSONException {
    JSONObject object = currentRecipe.getJSONObject(0);
    String title = object.getString("title");
    int minutes = object.getInt("readyInMinutes");
    int id = object.getInt("id");
    JSONArray ingredients = object.getJSONArray("extendedIngredients");
    String url = object.getString("sourceUrl");
    JSONArray steps = object.getJSONArray("analyzedInstructions");
    Recipe recipe = new Recipe(title,id,minutes,url,ingredients, steps);
    return recipe;
  }

  @Override
  public void onCreate() {
    INSTANCE = this;
    client = new ClarifaiBuilder(getString(R.string.clarifai_api_key))
        // Optionally customize HTTP client via a custom OkHttp instance
        .client(new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS) // Increase timeout for poor mobile networks

            // Log all incoming and outgoing data
            // NOTE: You will not want to use the BODY log-level in production, as it will leak your API request details
            // to the (publicly-viewable) Android log
            .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
              @Override public void log(String logString) {
                Timber.e(logString);
              }
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        )
        .buildSync(); // use build() instead to get a Future<ClarifaiClient>, if you don't want to block this thread
    super.onCreate();

    // Initialize our logging
    Timber.plant(new Timber.DebugTree());
  }

  @NonNull
  public ClarifaiClient clarifaiClient() {
    final ClarifaiClient client = this.client;
    if (client == null) {
      throw new IllegalStateException("Cannot use Clarifai client before initialized");
    }
    return client;
  }
}
