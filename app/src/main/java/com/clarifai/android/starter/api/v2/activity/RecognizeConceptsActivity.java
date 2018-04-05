package com.clarifai.android.starter.api.v2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import com.clarifai.android.starter.api.v2.ExifUtils;
import com.clarifai.android.starter.api.v2.IngredientDataSingleton;
import com.clarifai.android.starter.api.v2.JSONQuery;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private final String TAG = "Ingredients Activity";

    public IngredientDataSingleton singleton;



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

  @BindView(R.id.fabDelete) View fabDelete;

  // big layout for food
  @BindView(R.id.layout_foods) LinearLayout layoutFood;

  @BindView(R.id.layout_help) LinearLayout layoutHelp;



    //@BindView(R.id.item1) TextView item1;
    private List<String> listOfFood;
    private List<Concept> concepts;
    private List<String> listOfItems;
    private Resources resources;
    private String m_Text = "";


  @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      resources = getResources();
      listOfItems = new ArrayList<>();
      listOfFood = new ArrayList<>();
      fabNext.setVisibility(View.INVISIBLE);
      createSingleton();


  }

    private void createSingleton() {

        List<String> ingredientList = populateIngredienteList();

        if (ingredientList != null){

            singleton = IngredientDataSingleton.getInstance();
            singleton.setIngredientData(ingredientList);

            Log.i("SINGLETON      ", "SETTING SUCCEED");

            listOfFood = singleton.getIngredientData();

            Log.i("SINGLETON      ", "GETTING SUCCEED");

        }else{
            createSingleton();
        }
    }

    public List<String> populateIngredienteList (){

      try
      {
          //Load the file from the raw folder - don't forget to OMIT the extension
          String output = LoadFile("food", true);
          List<String> listOfFood = new ArrayList<>();

          Scanner scanner = new Scanner(output);

          while (scanner.hasNext())
          {
              listOfFood.add(scanner.nextLine());
          }

          return listOfFood;
      }
      catch (IOException e)
      {
          //display an error toast message
          Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "File: not found!", Toast.LENGTH_LONG);
          toast.show();
          return null;
      }



  }

  @Override protected void onStart() {
    super.onStart();
    Log.i(TAG, "The Detection activity has started");
  }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "The Detection activity has paused");

        fabNext.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "The Detection activity has resumed");

        //setBusy(false);

        fabNext.setVisibility(View.INVISIBLE);
        if (listOfItems.size() > 0)
        {
            layoutFood.setVisibility(VISIBLE);
            fabNext.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            setBusy(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "The Detection activity has stopped");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "The Detection activity has restarted");
    }

    @OnClick(R.id.fabDelete)
    void deleteAll(View view){

        layoutHelp.setVisibility(VISIBLE);
        Log.i(TAG, "DELETE has been clicked");


        concepts = new ArrayList<>();
        layoutFood.removeAllViews();
        listOfItems = new ArrayList<>();
        imageView.setVisibility(View.INVISIBLE);


    }

  //Tries to use the gallery to pick an image
  @OnClick(R.id.fabUpload)
  void pickImage(View view) {
      startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
  }

  //Tries to use the camera to take a picture
  @OnClick(R.id.fabPhoto)
  void takeImage(View view) {
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
                      layoutHelp.setVisibility(View.INVISIBLE);
                      addEntry(m_Text);
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

  void enterItem(View view)
  {
      enterItem();
  }

  //It starts the next activity with the contents of the list
  @OnClick(R.id.fabNext)
  void nextActivity(View view)
  {
      Toast toast = Toast.makeText(RecognizeConceptsActivity.this, "Preparing Recipes", Toast.LENGTH_LONG);
      toast.show();

      JSONQuery query = new JSONQuery(listOfItems, getApplicationContext());
      query.setUrl();
      query.execute();
      setBusy(true);
      onPause();

  }




    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    //Depending on the result from the select picture, take picture; it uses (or not)
    // the image and sends it to be converted, so the ClarifAI API is able to recognize it
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }

    concepts = new ArrayList<>();
    layoutHelp.setVisibility(View.INVISIBLE);
    layoutFood.removeAllViews();
    listOfItems = new ArrayList<>();

    if (requestCode == PICK_IMAGE) //|| requestCode == REQUEST_IMAGE_CAPTURE)
    {

        Uri selectedImageURI = data.getData();
        File imageFile = new File(getRealPathFromURI(selectedImageURI));
        Bitmap bitmap= decodeFile(imageFile.getPath());


        /*if (requestCode == PICK_IMAGE)
      {
        inStream = context.getContentResolver().openInputStream(data.getData());
        bitmap = BitmapFactory.decodeStream(inStream);

      } else if (requestCode == REQUEST_IMAGE_CAPTURE)
      {
        Bundle extras = data.getExtras();
        bitmap = (Bitmap) extras.get("data");

      }*/

      final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, bitmap, requestCode);
      if (imageBytes != null) {

        onImagePicked(imageBytes);
      }
    }

      if (requestCode == REQUEST_IMAGE_CAPTURE)
      {

          Bundle extras = data.getExtras();
          Bitmap bitmap = (Bitmap) extras.get("data");
        final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, bitmap, requestCode);
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
      List<String> ingredientData = singleton.getIngredientData();


      for (Concept c : concepts)
      {
          if (ingredientData.contains(c.name()))
          {
                  filteredNames.add(c.name());
          }
      }

      for (int i = 0; i < filteredNames.size(); i++)
      {
          String nameOfConcept = filteredNames.get(i);
          addEntry(nameOfConcept);
      }

  }

  @Override protected int layoutRes() { return R.layout.activity_recognize; }

  private void setBusy(final boolean busy) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        switcher.setDisplayedChild(busy ? 1 : 0);
        layoutFood.setVisibility(busy ? GONE : VISIBLE);
        imageView.setVisibility(busy ? GONE : VISIBLE);
        fabUpload.setEnabled(!busy);
        fabAdd.setEnabled(!busy);
        fabNext.setEnabled(!busy);
        fabPhoto.setEnabled(!busy);

      }
    });
  }


  //Adds the item to the list, both on the view and on the model
  public void addEntry(String nameOfItem)
  {

      if (listOfItems.size() >= 8)
      {
          return;
      }

      LinearLayout layoutNewItem = (LinearLayout) getLayoutInflater().inflate(R.layout.item_new, null);
      layoutNewItem.setId(listOfItems.size());


      LinearLayout layoutInside = (LinearLayout) layoutNewItem.getChildAt(0);

      TextView labelText  =  (TextView)  layoutInside.getChildAt(0);

      labelText.setText(nameOfItem);

      layoutFood.addView(layoutNewItem);
      listOfItems.add(nameOfItem.replace(' ', '-'));
      fabNext.setVisibility(VISIBLE);


  }

    //Deletes the item from the list, both on the view and on the model
    public void deleteCurrent(View view) {

      ImageView deleteImage = (ImageView) view;

      LinearLayout layoutParent = (LinearLayout) deleteImage.getParent().getParent();
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

    public Bitmap decodeFile(String filePath) {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap b1 = BitmapFactory.decodeFile(filePath, o2);
        Bitmap b= ExifUtils.rotateBitmap(filePath, b1);

        return b;

        // image.setImageBitmap(bitmap);
    }




}
