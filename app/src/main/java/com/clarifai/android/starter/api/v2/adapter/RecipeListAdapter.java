package com.clarifai.android.starter.api.v2.adapter;

/**
 * Created by Canton on 03/04/2018.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clarifai.android.starter.api.v2.R;
import com.squareup.picasso.Picasso;


public class RecipeListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] imgUrl;

    public RecipeListAdapter(Activity context, String[] itemname, String[] imgUrl) {
        super(context, R.layout.item_recipe, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgUrl=imgUrl;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_recipe, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.recipeName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.recipeImage);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname[position]);


        // Set the image of the recipe
        Picasso.with(getContext()).load(imgUrl[position]).into(imageView);


        //imageView.setImageResource(imgid[0]);
        extratxt.setText("Ingredients, watermark, health info and stuff");
        return rowView;

    };
}