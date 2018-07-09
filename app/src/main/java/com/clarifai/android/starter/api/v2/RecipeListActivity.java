package com.clarifai.android.starter.api.v2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.adapter.RecipeListAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private final String TAG = "Recipe Activity";

    String[] titles;
    String[] images;
    String[] ingredients;
    float[] waterMarks;
    float[] carbonPrints;
    int[] colors;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Log.i(TAG, "Create list from input");

        // Get the list (String or JSON?)
        App state = App.get();
        List<Dish> dishes = state.getDishes();
        titles = new String[dishes.size()];
        images = new String[dishes.size()];
        ingredients = new String[dishes.size()];
        waterMarks = new float[dishes.size()];
        carbonPrints = new float[dishes.size()];
        colors = new int[dishes.size()];



        for (int i = 0; i < dishes.size(); ++i) {
            //titles[i] = dishes.get(i).getTitle();

            titles[i] = state.getRecipe(i).getTitle();
            images[i] = dishes.get(i).getImage();
            //ingredients[i] = state.getRecipe(i).getSourceURL();

            Recipe recipe = state.getRecipe(i);
            ingredients[i] = recipe.getIngredientString();
            waterMarks[i] = recipe.calculateWaterMark();
            carbonPrints[i] = recipe.calculateCarbonPrint();
        }


        createList(ingredients);

    }


    private void createList(String[] content) {


        // Set the listview to display all questions
        ListView listView = (ListView)findViewById(R.id.recipeListView);

        RecipeListAdapter adapter= new RecipeListAdapter(this,
                titles,
                images,
                content,
                colors);

        listView.setAdapter(adapter);

        //startQueryRecipe();

        // Set the onclick function of each item to go to the quiz activity while passing in
        // selection data
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i(TAG, "List View on Click Pressed. Loading description for " + position);

                String uri = App.get().getRecipe(position).getSourceURL();
                final Uri uriParsed = Uri.parse(uri);

                Intent intent = new Intent(Intent.ACTION_VIEW, uriParsed);
                getApplicationContext().startActivity(intent);

                /*RecipeQuery recipe = new RecipeQuery(recipeId, getApplicationContext());
                recipe.setUrl();
                recipe.execute();*/

            }
        });

    }




    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "The Recipes activity has restarted");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "The Recipes activity has paused");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "The Recipes activity has started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "The Recipes activity has resumed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "The Recipes activity has stopped");
    }



    private String[] changeColors(float[] array, String units) {

        String[] contents = new String[array.length];

        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }

        // Finds and returns max
        int max = 0;
        int min = 0;

        for (int j = 0; j < array.length; j++) {

            if (Float.isNaN(array[j])) {
                return null;
            }
            if (array[j] == -1){
                colors[j] = ResourcesCompat.getColor(getResources(), R.color.white, null);
                contents[j] = "There is not enough data to provide an accurate number";
                if(min == j){
                    min++;
                }
            }
            else {

                if (array[j] > array[max]) {
                    max = j;
                }
                else if (array[j] < array[min]) {
                    min = j;
                }
                colors[j] = ResourcesCompat.getColor(getResources(), R.color.yellow, null);
                contents[j] = Float.toString(  (array[j]*1000 ) ) + units;
            }


        }
        colors[max] = ResourcesCompat.getColor(getResources(), R.color.red, null);

        if(min<array.length){
            colors[min] = ResourcesCompat.getColor(getResources(), R.color.green, null);
        }


        return contents;

    }

    private void resetColors (){
        for (int i = 0; i < colors.length; i++){
            colors[i] = ResourcesCompat.getColor(getResources(), R.color.white, null);
        }
    }

    public void onClickWaterPrint(View view) {

        Switch waterSwitch = (Switch) findViewById(R.id.waterMark);
        Switch carbonSwitch = (Switch) findViewById(R.id.carbonPrint);

        if (waterSwitch.isChecked()){
            carbonSwitch.setChecked(false);
            createList(changeColors(waterMarks, " ml of water used"));
        }
        else {
            resetColors();
            createList(ingredients);
        }

    }

    public void onClickCarbonPrint(View view) {

        Switch waterSwitch = (Switch) findViewById(R.id.waterMark);
        Switch carbonSwitch = (Switch) findViewById(R.id.carbonPrint);

        if (carbonSwitch.isChecked()){
            waterSwitch.setChecked(false);
            createList(changeColors(carbonPrints, " grams of CO2 produced"));
        }
        else {
            resetColors();
            createList(ingredients);
        }


    }

}
