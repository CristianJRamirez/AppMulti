package a45858000w.appmulti;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexvasilkov.events.Events;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View view;
    private Button btCamara;
    private Button btVideo;
    private Button btMapa;
    private GridView gridview;
    FirebaseDatabase database;
    private DatabaseReference todosRef;

    private String pathFotoTemporal;
    private String pathVideoTemporal;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    private ArrayList<String> items;
    FirebaseListAdapter<String> adapterFBLA;



    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        database = FirebaseDatabase.getInstance();
        todosRef = database.getReference("todos");


        btCamara = (Button) view.findViewById(R.id.btCamara);

        btVideo = (Button) view.findViewById(R.id.btVideo);

        btMapa = (Button) view.findViewById(R.id.btMap);


        btCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto();
            }
        });


        btVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeVideo();
            }
        });


        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MapActivity.class);
                startActivity(intent);
            }
        });

        gridview = (GridView) view.findViewById(R.id.grdGaleria);
        // crear el gridview a partir del elemento del xml gridview




        items = new ArrayList<>();

        adapterFBLA = new FirebaseListAdapter<String>(
                getActivity(), String.class, R.layout.items_multimedia, todosRef)
        {
            @Override
            protected void populateView(View v, String model, int position) {

                ImageView imagen = (ImageView) v.findViewById(R.id.itemMulti);

                Glide.with(getContext()).load(model).into(imagen);

                //Log.d("URL------------->",model);

            }
        };

        gridview.setAdapter(adapterFBLA);



        return view;
    }





    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        pathFotoTemporal = "file:" + image.getAbsolutePath();
        return image;
    }


    private void TakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                DatabaseReference newReference = todosRef.push();
                newReference.setValue(photoFile.getAbsolutePath());
            }
        }
    }


    private void takeVideo ()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(videoFile));// set the image file name
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

                startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                DatabaseReference newReference = todosRef.push();
                newReference.setValue(videoFile.getAbsolutePath());
            }
        }
    }


    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String videoFileName = "Video_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        pathVideoTemporal = "file:" + image.getAbsolutePath();
        return image;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent)  {
        super.onActivityResult(requestCode, resultCode, intent);

        try {

            if (requestCode == REQUEST_TAKE_PHOTO) {
                if (resultCode == RESULT_OK) {

                    //Uri seleccio = intent.getData();

                    Uri.Builder b = Uri.parse(pathFotoTemporal).buildUpon();

                    String[] columna = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(b.build(), columna, null, null, null);
                    cursor.moveToFirst();

                    int indexColumna = cursor.getColumnIndex(columna[0]);
                    String rutaFitxer = cursor.getString(indexColumna);
                    if(!rutaFitxer.equals("")){
                        DatabaseReference newReference = todosRef.push();
                        newReference.setValue(rutaFitxer);
                    }
                    cursor.close();
                }
            }


            if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    //Toast.makeText(this, "Video saved to:\n" +data.getData(), Toast.LENGTH_LONG).show();

                    Uri.Builder b = Uri.parse(pathVideoTemporal).buildUpon();

                    String[] columna = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(b.build(), columna, null, null, null);
                    cursor.moveToFirst();

                    int indexColumna = cursor.getColumnIndex(columna[0]);
                    String rutaFitxer = cursor.getString(indexColumna);
                    if(!rutaFitxer.equals("")){
                        DatabaseReference newReference = todosRef.push();
                        newReference.setValue(rutaFitxer);
                    }
                    cursor.close();

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    // Video capture failed, advise user
                }
            }



        }catch (Exception e) {
            e.printStackTrace();

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Events.register(this);
    }


    @Events.Subscribe("click-boton")
    private void onClickButton() {
        //+Events.create("clicar").param(0).post();
        Log.d("-------------------------->","volver del Mapa");
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("pasar", 0);
        startActivity(intent);
    }
}
