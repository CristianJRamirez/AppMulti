package a45858000w.appmulti;

/**
 * Created by 45858000w on 07/03/17.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;




import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;

import org.osmdroid.views.MapView;

import java.io.File;






public class InfoWindow extends BasicInfoWindow {
    private Context mContext;
    private String absolutePath;
    private String name;

    public InfoWindow(Context context, MapView mapView, String absolute, String name) {
        //super(R.layout.bonuspack_bubble, mapView);
        super(R.layout.imagen_mapa, mapView);
        mContext = context;
        absolutePath = absolute;
        this.name = name;
        Log.d("@@@@@@@@@@@@@@@@@ ->"+absolutePath,"---·"+name);
    }

    @Override
    public void onOpen(Object item) {
        //super.onOpen(item);
        ImageView imageView = (ImageView) mView.findViewById(R.id.bubble_image);
        File f = new File(absolutePath);
        if (f.exists()) {
            Glide.with(mContext)
                    //.load(Uri.fromFile(f))
                    .load(absolutePath)
                    .centerCrop()
                    .into(imageView);
        }/* else {
            StorageReference storageReference = ((MainActivityFragment) mContext.getApplicationContext()).getStorage();
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference.child(name))
                    .centerCrop()
                    .into(imageView);
        }*/


    }
}