package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private ConnectivityManager connecManger;

    private TextView currLocationP;
    private TextView officeNameP;
    private TextView officialNameP;

    private ImageView photoImageP;
    private ImageView logoImageP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        currLocationP = findViewById(R.id.currentLocationP);
        officeNameP = findViewById(R.id.officeNamePText);
        officialNameP = findViewById(R.id.officialNamePText);
        photoImageP = findViewById(R.id.photoPImage);
        logoImageP = findViewById(R.id.logoPImage);

        Intent intent = this.getIntent();
        String loca = intent.getStringExtra("loca");
        String officeName = intent.getStringExtra("officeName");
        String officialName = intent.getStringExtra("officialName");
        final String photoURL = intent.getStringExtra("photoURL");
        String backgroundColor = intent.getStringExtra("backgroundColor");

        currLocationP.setText(loca);
        officeNameP.setText(officeName);
        officialNameP.setText(officialName);

        if(doNetCheck()) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    String urlToUse = photoURL.replace("http:","https:");
                    picasso.load(urlToUse)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photoImageP);

                }
            }).build();

            picasso.load(photoURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photoImageP);
        } else {
            photoImageP.setImageResource(R.drawable.placeholder);
        }

        if(backgroundColor.equals("BLACK")){
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            logoImageP.setVisibility(View.GONE);
        }
        if(backgroundColor.equals("BLUE")){
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            logoImageP.setImageResource(R.drawable.dem_logo);
        }
        if(backgroundColor.equals("RED")){
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            logoImageP.setImageResource(R.drawable.rep_logo);
        }


    }

    @SuppressLint("ServiceCast")
    private boolean doNetCheck() {

        connecManger = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connecManger == null) {
//                Toast.makeText(this, "Cannot access ", Toast.LENGTH_LONG).show();
            return false;
        }

        NetworkInfo netInfo = connecManger.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            Log.d(TAG, "doNetCheck: YOU ARE CONNECTED TO THE INTERNET!");
            return true;
        } else {
            Log.d(TAG, "doNetCheck: NOT CONNECTED TO THE INTERNET!");
            return false;
        }
    }


}
