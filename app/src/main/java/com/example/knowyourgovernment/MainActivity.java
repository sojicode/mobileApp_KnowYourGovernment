package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivityTag";

    private TextView currLocation;

    private ConnectivityManager connecManger;

    private RecyclerView recyclerView;
    private  OfficialAdapter officialAdapter;

    private ArrayList<Official> officialArrList = new ArrayList<>();

    private static int LOCATION_REQUEST_CODE = 123;
    private LocationManager locaManager;
    private Criteria criteria;

    public static String searchedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialArrList, this);

        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currLocation = findViewById(R.id.currentLocationP);

        if(!doNetCheck()){
            goNoNetworkDialog();
            currLocation.setText("No Data For Location");

        } else {
            if(searchedLocation != null) {
                currLocation.setText(searchedLocation);
                new GovernDownloader(MainActivity.this).execute(searchedLocation);


            }
            else if(permissionCheck()) {
                setLocation();
            }
        }


    }

    public boolean permissionCheck() {

        locaManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
            int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void setLocation(){

        String bestProvider = locaManager.getBestProvider(criteria, true);

        Location currentLocation = locaManager.getLastKnownLocation(bestProvider);

        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();
            Log.d(TAG, "setLocation: latitude, longitude >>> " + lat + lon);

            doLatLon(lat,lon);

        } else {
            Log.d(TAG, "setLocation: Location unavailable!");
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

    public void goNoNetworkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }

    public void doLatLon(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation(lat, lon, 1);
            displayAddress(addresses);
            getStateCode(addresses.get(0));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayAddress(List<Address> addresses) {
        String stateCode = getStateCode(addresses.get(0));
        String postalCode = "";

                StringBuilder sb = new StringBuilder();
        if(addresses.size() == 0) {
            Toast.makeText(this, "Nothing Found", Toast.LENGTH_LONG).show();
        }
        for(Address ad : addresses) {
            String a = String.format("%s, %s %s ",

                    (ad.getLocality() == null ? "" : ad.getLocality()), //Mountain View
                    stateCode,
                    (ad.getPostalCode() == null ? "" : ad.getPostalCode())); //94043


            Log.d(TAG, "displayAddress: ad.getLocality() >> " + ad.getLocality());

            Log.d(TAG, "displayAddress: ad.getPostalCode() >> " + ad.getPostalCode());

            if (!a.trim().isEmpty())
                sb.append(a.trim());

            sb.append("\n");
            Log.d(TAG, "displayAddress: SB >>>> " + sb.toString());
            //1600 Amphitheatre Parkway Mountain View California 94043 United States
        }
        postalCode = addresses.get(0).getPostalCode();
        Log.d(TAG, "displayAddress: postalCode >> "+postalCode);


        ((TextView) findViewById(R.id.currentLocationP)).setText(sb.toString());

        new GovernDownloader(MainActivity.this).execute(postalCode);


    }

    private String getStateCode(Address addr){
        String fullAddress = "";
        for(int j = 0; j <= addr.getMaxAddressLineIndex(); j++)
            if (addr.getAddressLine(j) != null)
                fullAddress = fullAddress + " " + addr.getAddressLine(j);

        String stateCode = null;
        Pattern pattern = Pattern.compile(" [A-Z]{2} ");
        String helper = fullAddress.toUpperCase().substring(0, fullAddress.toUpperCase().indexOf("USA"));
        Matcher matcher = pattern.matcher(helper);
        while (matcher.find())
            stateCode = matcher.group().trim();

        Log.d(TAG, "getUSStateCode: stateCode :  " + stateCode);

        return stateCode;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

        int pos = recyclerView.getChildLayoutPosition(v);
        Official offi = officialArrList.get(pos);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("loca",currLocation.getText().toString());

        Bundle bd = new Bundle();
        bd.putSerializable("officialDetail", offi);
        intent.putExtras(bd);
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "Long Clicked", Toast.LENGTH_SHORT).show();
        onClick(v);
        return false;
    }

    public void openAboutActivity(View v){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(main_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        View v = findViewById(R.id.aboutItem);

        switch(item.getItemId()){
            case R.id.aboutItem:
                openAboutActivity(v);
                break;
            case R.id.locationItem:
                openFindLocationDialog();
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: default choose");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFindLocationDialog() {

        if(doNetCheck()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String input = et.getText().toString();
                    Log.d(TAG, "onClick: INPUT >>>> " + input);

                    new GovernDownloader(MainActivity.this).execute(input);

                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(MainActivity.this, "CANCEL choose!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setTitle("Enter a City, State or a Zip Code:");

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            officialArrList.clear();
            officialAdapter.notifyDataSetChanged();
            goNoNetworkDialog();
        }

    }

    public void updateResult(Object[] output){

        if(output == null) {
            currLocation.setText("No Data For Location");

        } else {
            officialArrList.clear();
            currLocation.setText(output[0].toString());
            searchedLocation = output[0].toString();
            ArrayList<Official> officials = (ArrayList<Official>)output[1];
            for(int i = 0; i < officials.size(); i++){
                officialArrList.add(officials.get(i));
            }
        }
        officialAdapter.notifyDataSetChanged();

    }



}
