package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";

    private ConnectivityManager connecManger;

    private TextView currLocation;
    private TextView officeName;
    private TextView officialName;
    private TextView partyName;

    private ImageView photoImage;
    private ImageView logoImage;

    private TextView addressTitle;
    private TextView address;

    private TextView phoneTitle;
    private TextView phone;

    private TextView urlTitle;
    private TextView url;

    private TextView emailTitle;
    private TextView email;

    private ImageView googlePlusBtn;
    private ImageView facebookBtn;
    private ImageView twitterBtn;
    private ImageView youTubeBtn;

    private Official official;

    public static final String OMITTED = "";
    public static final String UNKNOWN = "Unknown";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        currLocation = findViewById(R.id.currentLocationP);
        officeName = findViewById(R.id.officeNamePText);
        officialName = findViewById(R.id.officialNameText);
        partyName = findViewById(R.id.partyNameText);
        photoImage = findViewById(R.id.photoPImage);
        logoImage = findViewById(R.id.logoPImage);

        addressTitle = findViewById(R.id.addrTitleText);
        address = findViewById(R.id.addressText);

        phoneTitle = findViewById(R.id.phoneTitleText);
        phone = findViewById(R.id.phoneText);

        emailTitle = findViewById(R.id.emailTitleText);
        email = findViewById(R.id.emailText);

        urlTitle = findViewById(R.id.urlTitleText);
        url = findViewById(R.id.urlText);

        facebookBtn = findViewById(R.id.facebookImage);
        twitterBtn = findViewById(R.id.twitterImage);
        youTubeBtn = findViewById(R.id.youTubeImage);
        googlePlusBtn = findViewById(R.id.ggplusImage);

        Intent intent = this.getIntent();
        Bundle bd = intent.getExtras();
        currLocation.setText(intent.getStringExtra("loca"));
        official = (Official)bd.getSerializable("officialDetail");

        Log.d(TAG, "onCreate: official detail : " + official);

        String officeN = official.getOfficeName();
        officeName.setText(officeN);
        Log.d(TAG, "onCreate: officeName >>> " + officeN);

        String name = official.getOfficialName();
        officialName.setText(name);

        String partyN = official.getPartyName();
        Log.d(TAG, "onCreate: partyName >>>> " + partyN);

        if(partyN.equals(UNKNOWN)){
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            partyName.setText("("+ "Unknown" +")");
            logoImage.setVisibility(View.GONE);
        }
        if(partyN.equals("Democratic Party") || partyN.equals("Democrat") ||
                partyN.equals("Democratic")) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            partyName.setText("("+ partyN +")");
            logoImage.setImageResource(R.drawable.dem_logo);

        }
        if(partyN.equals("Republican Party") || partyN.equals("Republican")) {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            partyName.setText("("+ partyN +")");
            logoImage.setImageResource(R.drawable.rep_logo);
        }


        if(official.getAddress().equals(OMITTED)) {
            addressTitle.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        } else {
            addressTitle.setText("Address:");
            address.setText(official.getAddress());
        }

        if(official.getPhone().equals(OMITTED)){
            phoneTitle.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
        } else {
            phoneTitle.setText("Phone:");
            phone.setText(official.getPhone());
        }

        if(official.getEmail().equals(OMITTED)){
            emailTitle.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        } else {
            emailTitle.setText("Email:");
            email.setText(official.getEmail());
        }

        if(official.getUrl().equals(OMITTED)){
            urlTitle.setVisibility(View.GONE);
            url.setVisibility(View.GONE);
        } else {
            urlTitle.setText("Website:");
            url.setText(official.getUrl());
        }


//        Linkify.addLinks(address, Linkify.ALL);
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Log.d(TAG, "onCreate: ADDRESS >>> " + address.toString());
        address.setLinkTextColor(Color.parseColor("WHITE"));

        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        phone.setLinkTextColor(Color.parseColor("WHITE"));

        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        email.setLinkTextColor(Color.parseColor("WHITE"));

        Linkify.addLinks(url, Linkify.WEB_URLS);
        url.setLinkTextColor(Color.parseColor("WHITE"));


        if(doNetCheck()) {

            photoImage.setImageResource(R.drawable.placeholder);

            if(official.getPhotoUrl().equals(OMITTED)) {
                photoImage.setImageResource(R.drawable.missing);
            } else {
                final String photoURL = official.getPhotoUrl();
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        String pathURL = photoURL.replace("http:", "https:");
                        picasso.load(pathURL)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(photoImage);


                        }

                }).build();
                picasso.load(photoURL)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photoImage);

            }

        } else {
            photoImage.setImageResource(R.drawable.placeholder);
        }

        if(official.getFacebook().equals(OMITTED)) {
            facebookBtn.setVisibility(View.GONE);
        }
        if(official.getTwitter().equals(OMITTED)) {
            twitterBtn.setVisibility(View.GONE);
        }
        if(official.getYouTube().equals(OMITTED)) {
            youTubeBtn.setVisibility(View.GONE);
        }
        if(official.getGooglePlus().equals(OMITTED)) {
            googlePlusBtn.setVisibility(View.GONE);
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


    public void doLinkMap(String addr) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address.toString()));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);

    }

    public void facebookClicked(View v) {

        String name = official.getFacebook();
        String FACEBOOK_URL = "https://www.facebook.com/" + name;
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if(versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                urlToUse = "fb://page/" + name;
            }
        } catch(PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {

        Intent intent = null;
        String name = official.getTwitter();
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);

    }

    public void youTubeClicked(View v) {

        String name = official.getYouTube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com" + name));
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }

    }

    public void googlePlusClicked(View v) {

        String name = official.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com"+ name)));
        }

    }

    public void openPhotoActivity(View v) {

        if(official.getPhotoUrl().equals(OMITTED)) {
            return;
        }

        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("loca",currLocation.getText().toString());
        intent.putExtra("officeName", official.getOfficeName());
        intent.putExtra("officialName", official.getOfficialName());
        intent.putExtra("photoURL",official.getPhotoUrl());

        String partyN = official.getPartyName();

        if(partyN.equals(UNKNOWN)){
            intent.putExtra("backgroundColor", "BLACK");
        }
        if(partyN.equals("Democratic Party") || partyN.equals("Democrat") ||
                partyN.equals("Democratic")) {
            intent.putExtra("backgroundColor", "BLUE");
        }
        if(partyN.equals("Republican Party") || partyN.equals("Republican")) {
            intent.putExtra("backgroundColor", "RED");
        }

        startActivity(intent);

    }







}
