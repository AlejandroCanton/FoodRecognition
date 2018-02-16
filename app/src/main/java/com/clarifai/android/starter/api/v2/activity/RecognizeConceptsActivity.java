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
import android.view.View;
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


    //big layout for food
  @BindView(R.id.layout_foods) LinearLayout layoutFood;








    //@BindView(R.id.item1) TextView item1;
    private List<Concept> concepts;
    private List<String> listOfItems;
    private List<String> listOfFood;
    private String m_Text = "";
    private Resources resources;
    private String output = "";




  @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


      resources = getResources();
      listOfFood = new ArrayList<>();

      try
        {
          //Load the file from the raw folder - don't forget to OMIT the extension
          output = LoadFile("food", true);




          //output to LogCat
          Log.i("test", output);

            //Toast toast = Toast.makeText(RecognizeConceptsActivity.this, output, Toast.LENGTH_LONG);
            //Toast toast = Toast.makeText(RecognizeConceptsActivity.this, output, Toast.LENGTH_LONG);
            //toast.show();
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

    //resultsList.setLayoutManager(new LinearLayoutManager(this));
    //resultsList.setAdapter(adapter);
  }

  @OnClick(R.id.fabUpload)
  void pickImage() {
      startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);

  }

  @OnClick(R.id.fabPhoto)
  void takeImage() {
      startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE);

      /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
          //takePictureIntent.setType("/image/*");
          startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }*/
  }

  @OnClick(R.id.fabAdd)
  void enterItem() {


      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Add an ingredient");

// Set up the input
      final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
      input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
      builder.setView(input);

// Set up the buttons
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              m_Text = input.getText().toString();


              if (!listOfItems.contains(m_Text))
              {
                  addEntry(m_Text, (listOfItems.size()));

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


  @OnClick(R.id.fabNext)
  void nextActivity()
  {
      String message = "";


      for (int i = 0; i < listOfItems.size(); i++)
      {
          message = message + listOfItems.get(i) + ",";
      }

      Toast toast = Toast.makeText(RecognizeConceptsActivity.this, message, Toast.LENGTH_LONG);
      toast.show();

          JSONQuery query = new JSONQuery(listOfItems, getApplicationContext());
          query.setUrl();
          query.execute();


  }


  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }

    concepts = new ArrayList<>();
    layoutFood.removeAllViews();
    listOfItems = new ArrayList<>();

    if (requestCode == PICK_IMAGE) //|| requestCode == REQUEST_IMAGE_CAPTURE)
    {

        Log.d("antes", "TODAVIA NO PASA NADA \n \n \n \n \n ");



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
        fabUpload.setEnabled(!busy);
      }
    });
  }

  public void addEntry(String nameOfItem, int count)
  {


      if (count == 8)
      {
          return;
      }

      LinearLayout layoutNewItem = (LinearLayout) getLayoutInflater().inflate(R.layout.item_new, null);
      layoutNewItem.setId(count);

      TextView labelText  =  (TextView) layoutNewItem.getChildAt(0);

      labelText.setText(nameOfItem);

      layoutFood.addView(layoutNewItem);


      View v = new View(this);
      v.setLayoutParams(new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,
              5
      ));
      v.setBackgroundColor(Color.parseColor("#B3B3B3"));

      layoutFood.addView(v);


      listOfItems.add(nameOfItem);

      //if (nameOfItem.equals(listOfFood.get(2)))
      //{
          //Toast toast = Toast.makeText(RecognizeConceptsActivity.this, nameOfItem, Toast.LENGTH_LONG);
          //toast.show();
      //}

  }

    public void deleteCurrent(View view) {

      ImageView deleteImage = (ImageView) view;

      LinearLayout layoutParent = (LinearLayout) deleteImage.getParent();
      int deleteCurrent = layoutParent.getId();

      this.listOfItems.remove(deleteCurrent);

      LinearLayout theBigLayout = (LinearLayout) layoutParent.getParent();
      theBigLayout.removeViewAt(deleteCurrent);

      for (int i = deleteCurrent; i < this.listOfItems.size(); i++)
      {
          layoutFood.getChildAt(i).setId(i);

      }


    }


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
