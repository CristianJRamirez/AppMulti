package a45858000w.appmulti;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent i = new Intent(this, ImageActivity.class);
        ImageView iv_image = (ImageView) this.findViewById(R.id.iv_image);

        Bundle dat = getIntent().getExtras();
        String url = dat.getString("seleccion");
        Glide.with(this).load(url).into(iv_image);

        Log.d("Sss", url);




    }
}