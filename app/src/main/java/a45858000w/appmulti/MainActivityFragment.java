package a45858000w.appmulti;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


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
    private DatabaseReference refLocal;

    private String pathTemporal;
    private String pathFotoTemporal;
    private String pathVideoTemporal;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    private ArrayList<String> items;
    FirebaseListAdapter<String> adapterFBLA;
    private FirebaseAuth auth;
    private int RC_Sign_in =123;

    private TrackGPS gps;
    double longitude;
    double latitude;
    ArrayList<Localizacion> localizaciones =null;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        database = FirebaseDatabase.getInstance();
        todosRef = database.getReference("todos");
        refLocal = database.getReference("localizaciones");

        localizaciones = new ArrayList<Localizacion>();

        btCamara = (Button) view.findViewById(R.id.btCamara);

        btVideo = (Button) view.findViewById(R.id.btVideo);

        btMapa = (Button) view.findViewById(R.id.btMap);

       // setupAuth();

        btCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto();
                capturarLocalizacion();

            }
        });


        btVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeVideo();
                capturarLocalizacion();
            }
        });


        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MapActivity.class);
                for (Localizacion l:localizaciones) {
                    Log.d(" ---------------------------------------------",l.toString());
                }
                intent.putExtra("localizaciones", localizaciones);
                startActivity(intent);
            }
        });

        gridview = (GridView) view.findViewById(R.id.grdGaleria);
        // crear el gridview a partir del elemento del xml gridview




        items = new ArrayList<>();

        adapterFBLA = new FirebaseListAdapter<String>(getActivity(), String.class, R.layout.items_multimedia, todosRef)        {
            @Override
            protected void populateView(View v, String model, int position) {
                ImageView imagen = (ImageView) v.findViewById(R.id.itemMulti);
                Glide.with(getContext()).load(model).into(imagen);
            }
        };

        gridview.setAdapter(adapterFBLA);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String model = adapterFBLA.getItem(position);
                Intent intent;
                if (model.contains("jpg"))
                {
                    intent = new Intent(getContext(), ImageActivity.class);
                }
                else
                {
                    intent = new Intent(getContext(), VideoActivity.class);
                }
                Bundle datos= new Bundle();
                datos.putString("sel", model);
                intent.putExtras(datos);
                startActivity(intent);
                Log.d("URL------------->",model);
            }
            });

        return view;
    }

    private void capturarLocalizacion()
    {
        gps = new TrackGPS(getContext());


        if(gps.canGetLocation()){


            longitude = gps.getLongitude();
            latitude = gps .getLatitude();

            Localizacion l = new Localizacion(longitude,latitude,pathTemporal);
            Log.d("-------->>>>>>>>>>>>>>>>>>",l.toString());



            DatabaseReference newReference = refLocal.push();
            newReference.setValue(l.toString());



            localizaciones.add(l);
        }
        else
        {

            gps.showSettingsAlert();
        }
    }
    private void setupAuth() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Log.d("Current user", String.valueOf(auth.getCurrentUser()));
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                            )
                            .build(),
                    RC_Sign_in);}
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                Log.d("------__-------------------->", photoFile.getAbsolutePath());
                pathTemporal=photoFile.getAbsolutePath();

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

                pathTemporal=videoFile.getAbsolutePath();

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


  /*  public void onActivityResult(int requestCode, int resultCode, Intent intent)  {
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
*/

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
