package a45858000w.appmulti;

import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 45858000w on 03/02/17.
 */

public class Api {



    public static ArrayList<DatosEstacion> getDatosEstaciones(){
        Uri builtUri = Uri.parse("http://wservice.viabicing.cat/v2/stations")
                .buildUpon()
                //.appendPath("box_office.json")
                // .appendQueryParameter("country", pais)//para buscar dentro de la api con algun paramentro en concreto
                .build();
        String url = builtUri.toString();
        //Log.d("URL", url);

        try {
            String JsonResponse = HttpUtils.get(url);

            ArrayList<DatosEstacion> estaciones =new ArrayList<>();

            JSONObject data= new JSONObject(JsonResponse);
            JSONArray jsonEstaciones = data.getJSONArray("stations");

            for (int i = 0; i<jsonEstaciones.length() ; i++) {
                //DatosEstacion estacion= new DatosEstacion();
                JSONObject object = jsonEstaciones.getJSONObject(i);

                //Log.d("ESTACION ---->>>", object.toString());
                Gson gson = new Gson();
                DatosEstacion estacion= gson.fromJson(object.toString(),DatosEstacion.class);
                //Log.d("ESTACION ---->>>", estacion.toString());

                estaciones.add(estacion);
            }


            return estaciones;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
