package com.clarifai.android.starter.api.v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.adapter.RecipeListAdapter;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private final String TAG = "Recipe Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Log.i(TAG, "Create list from input");

        // Get the list (String or JSON?)
        App state = App.get();
        List<Dish> dishes = state.getDishes();
        String[] titles = new String[dishes.size()];
        String[] images = new String[dishes.size()];

        for (int i = 0; i < dishes.size(); ++i) {
            titles[i] = dishes.get(i).getTitle();
            images[i] = dishes.get(i).getImage();
        }

        // Set the listview to display all questions
        ListView listView = (ListView)findViewById(R.id.recipeListView);

        RecipeListAdapter adapter= new RecipeListAdapter(this,
                titles,
                images);

        listView.setAdapter(adapter);


        //startQueryRecipe();



        // Set the onclick function of each item to go to the quiz activity while passing in
        // selection data
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i(TAG, "List View on Click Pressed. Loading description for " + position);

                int recipeId = App.get().getDishes().get(position).getID();

                RecipeQuery recipe = new RecipeQuery(recipeId, getApplicationContext());
                recipe.setUrl();
                recipe.execute();


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
}
