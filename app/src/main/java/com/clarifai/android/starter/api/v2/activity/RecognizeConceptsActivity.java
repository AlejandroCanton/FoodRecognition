package com.clarifai.android.starter.api.v2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import com.clarifai.android.starter.api.v2.App;
import com.clarifai.android.starter.api.v2.ClarifaiUtil;
import com.clarifai.android.starter.api.v2.JSONQuery;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.zip.Inflater;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class RecognizeConceptsActivity extends BaseActivity {

  public static final int PICK_IMAGE = 100;
  public static final int REQUEST_IMAGE_CAPTURE = 99;


    // the list of results that were returned from the API
  //@BindView(R.id.resultsList) RecyclerView resultsList;

  // the view where the image the user selected is displayed
  @BindView(R.id.image) ImageView imageView;

  // switches between the text prompting the user to hit the FAB, and the loading spinner
  @BindView(R.id.switcher) ViewSwitcher switcher;

  // the FAB that the user clicks to select an image
  @BindView(R.id.fabUpload) View fabUpload;

  // the FAB that the user clicks to capture an image
  @BindView(R.id.fabPhoto) View fabPhoto;

  // the FAB that the user clicks to add to the list
  @BindView(R.id.fabAdd) View fabAdd;

  // the FAB that the user clicks to go to next activity
  @BindView(R.id.fabNext) View fabNext;

  // big layout for food
  @BindView(R.id.layout_foods) LinearLayout layoutFood;



    //@BindView(R.id.item1) TextView item1;
    private List<String> listOfFood;
    private List<Concept> concepts;
    private List<String> listOfItems;
    private Resources resources;
    private String m_Text = "";
    private String output = "";


  @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      resources = getResources();
      listOfItems = new ArrayList<>();
      listOfFood = new ArrayList<>();
      fabNext.setVisibility(View.INVISIBLE);

      try
        {
          //Load the file from the raw folder - don't forget to OMIT the extension
          output = LoadFile("food", true);

            Scanner scanner = new Scanner(output);

            while (scanner.hasNext())
            {
                listOfFood.add(scanner.nextLine());
            }
        }
        catch (IOException e)
        {
          //display an error toast message
          Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "File: not found!", Toast.LENGTH_LONG);
          toast.show();
        }


  }

  @Override protected void onStart() {
    super.onStart();
  }

  //Tries to use the gallery to pick an image
  @OnClick(R.id.fabUpload)
  void pickImage() {
      startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);

  }

  //Tries to use the camera to take a picture
  @OnClick(R.id.fabPhoto)
  void takeImage() {
      startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE);
  }

  //Prompts the user to write new items to the list
  @OnClick(R.id.fabAdd)
  void enterItem() {

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Add an ingredient");

      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_dropdown_item_1line, listOfFood);

        // Set up the input
      final AutoCompleteTextView input = new AutoCompleteTextView(this);
      input.setAdapter(adapter);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
      input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
      builder.setView(input);

        // Set up the buttons
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              m_Text = input.getText().toString();

              if (listOfFood.contains(m_Text))
              {
                  if (!listOfItems.contains(m_Text))
                  {
                      addEntry(m_Text, (listOfItems.size()));
                  }
                  else
                  {
                      Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "The ingredient is already on the list. Please insert another one!", Toast.LENGTH_LONG);
                      toast.show();
                      enterItem();
                  }
              }
              else
              {
                  Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "The ingredient is not a valid option. Please choose from the recommendations!", Toast.LENGTH_LONG);
                  toast.show();
                  enterItem();
              }
          }
      });
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
          }
      });
      builder.show();

  }



  //It starts the next activity with the contents of the list
  @OnClick(R.id.fabNext)
  void nextActivity()
  {
      Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "Preparing Recipes", Toast.LENGTH_LONG);
      toast.show();

      JSONQuery query = new JSONQuery(listOfItems, getApplicationContext());
      query.setUrl();
      query.execute();
      setBusy(true);
      onPause();

  }

    @Override
    protected void onPause() {
        super.onPause();
        fabNext.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        fabNext.setVisibility(View.INVISIBLE);
        if (listOfItems.size() > 0)
        {
            fabNext.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }

    }


    //Depending on the result from the select picture, take picture; it uses (or not)
    // the image and sends it to be converted, so the ClarifAI API is able to recognize it
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }

    concepts = new ArrayList<>();
    layoutFood.removeAllViews();
    listOfItems = new ArrayList<>();

    if (requestCode == PICK_IMAGE) //|| requestCode == REQUEST_IMAGE_CAPTURE)
    {

      final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data, requestCode);
      if (imageBytes != null) {
        onImagePicked(imageBytes);
      }
    }

      if (requestCode == REQUEST_IMAGE_CAPTURE)
      {

        final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data, requestCode);
        if (imageBytes != null) {
          onImagePicked(imageBytes);
        }
      }



  }


  //Sends the image picked to the ClarifAI API and uses the methods to filter and add with the results it gets
  private void onImagePicked(@NonNull final byte[] imageBytes) {
    // Now we will upload our image to the Clarifai API
    setBusy(true);

    // Make sure we don't show a list of old concepts while the image is being uploaded
    adapter.setData(Collections.<Concept>emptyList());

    new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
      @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
        // The default Clarifai model that identifies concepts in images
        final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

        // Use this model to predict, with the image that the user just selected as the input
        return generalModel.predict()
            .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
            .executeSync();
      }

      @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
        setBusy(false);
        if (!response.isSuccessful()) {
          showErrorSnackbar(R.string.error_while_contacting_api);
          return;
        }
        final List<ClarifaiOutput<Concept>> predictions = response.get();
        if (predictions.isEmpty()) {
          showErrorSnackbar(R.string.no_results_from_api);
          return;
        }
        //adapter.setData(predictions.get(0).data());
          concepts = predictions.get(0).data();
          filterBatch(concepts);
          imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
      }

      private void showErrorSnackbar(@StringRes int errorString) {
        Snackbar.make(
            root,
            errorString,
            Snackbar.LENGTH_INDEFINITE
        ).show();
      }
    }.execute();
  }


  //Makes the filter of the ClarifAI API results with the previously loaded food-only file.
  private void filterBatch(@NonNull List<Concept> concepts)
  {

      List<String> filteredNames = new ArrayList<>();

      for (Concept c : concepts)
      {
          Scanner scanner = new Scanner(output);

          while (scanner.hasNext())
          {
              if (scanner.nextLine().equals(c.name()))
              {
                  filteredNames.add(c.name());
              }
          }
      }

      for (int i = 0; i < filteredNames.size(); i++)
      {
          String nameOfConcept = filteredNames.get(i);
          addEntry(nameOfConcept, i);
      }

  }

  @Override protected int layoutRes() { return R.layout.activity_recognize; }

  private void setBusy(final boolean busy) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        switcher.setDisplayedChild(busy ? 1 : 0);
        imageView.setVisibility(busy ? GONE : VISIBLE);
        //fabUpload.setEnabled(!busy);
      }
    });
  }


  //Adds the item to the list, both on the view and on the model
  public void addEntry(String nameOfItem, int count)
  {

      if (count >= 8)
      {
          return;
      }

      LinearLayout layoutNewItem = (LinearLayout) getLayoutInflater().inflate(R.layout.item_new, null);
      layoutNewItem.setId(count);

      TextView labelText  =  (TextView) layoutNewItem.getChildAt(0);

      labelText.setText(nameOfItem);

      layoutFood.addView(layoutNewItem);
      listOfItems.add(nameOfItem.replace(' ', '-'));
      fabNext.setVisibility(VISIBLE);


  }

    //Deletes the item from the list, both on the view and on the model
    public void deleteCurrent(View view) {

      ImageView deleteImage = (ImageView) view;

      LinearLayout layoutParent = (LinearLayout) deleteImage.getParent();
      int deleteCurrent = layoutParent.getId();

      this.listOfItems.remove(deleteCurrent);

      LinearLayout theBigLayout = (LinearLayout) layoutParent.getParent();
      theBigLayout.removeViewAt(deleteCurrent);

      if(listOfItems.size() == 0)
      {
          fabNext.setVisibility(View.INVISIBLE);
          imageView.setImageBitmap(null);
      }else {

          for (int i = deleteCurrent; i < this.listOfItems.size(); i++)
          {
              layoutFood.getChildAt(i).setId(i);

          }
      }




    }


    //Loads the file with words related to food, used to filter the results from ClarifAI API
    public String LoadFile(String fileName, boolean loadFromRawFolder) throws IOException
{
          //Create a InputStream to read the file into
          InputStream iS;

          if (loadFromRawFolder)
          {
            //get the resource id from the file name
            //int rID = resources.getIdentifier("raw/"+fileName, null, null);
            //get the file as a stream
            iS = resources.openRawResource(R.raw.food);
          }
          else
          {
            //get the file as a stream
            iS = resources.getAssets().open(fileName);
          }

          //create a buffer that has the same size as the InputStream
          byte[] buffer = new byte[iS.available()];
          //read the text file as a stream, into the buffer
          iS.read(buffer);
          //create a output stream to write the buffer into
          ByteArrayOutputStream oS = new ByteArrayOutputStream();
          //write this buffer to the output stream
          oS.write(buffer);
          //Close the Input and Output streams
          oS.close();
          iS.close();

          //return the output stream as a String
          return oS.toString();
        }


}
