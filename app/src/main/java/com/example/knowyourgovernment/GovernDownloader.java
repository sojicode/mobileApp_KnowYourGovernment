package com.example.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GovernDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "GovernDownloader";

    private MainActivity mainActivity;
    private static final String apiBase = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String myAPIkey = "APIkey"; //your APIkey  

    ArrayList<Official> officialArrList = new ArrayList<>();
    private String city;
    private String state;
    private String zip;

    public static final String OMITTED = "";
    public static final String UNKNOWN = "Nonpartisan";


    GovernDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... dataIn) {

        String apiEnd = dataIn[0];
        Log.d(TAG, "doInBackground: dataIn[0] : " + apiEnd);

        String addr = apiBase + myAPIkey + "&address=" + apiEnd;
        Uri.Builder buildURL = Uri.parse(addr).buildUpon();
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            Log.d(TAG, "doInBackground: url >>> "+url);

            HttpURLConnection connec = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: urlToUSE " + urlToUse);

            connec.setRequestMethod("GET");

            InputStream is = connec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

//            Log.d(TAG, "doInBackground: >>>>> " + sb.toString());
            Log.d(TAG, "doInBackground: sb length >>>> " + sb.toString().length());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private ArrayList<Official> parseJSON(String st) {

        officialArrList = new ArrayList<>();

        try {
            JSONObject total = new JSONObject(st);
            JSONObject normaLInput = total.getJSONObject("normalizedInput");
            JSONArray offices = total.getJSONArray("offices");
            JSONArray officials = total.getJSONArray("officials");

            //normalizedInput - city, state, zip
            city = normaLInput.getString("city");
            state = normaLInput.getString("state");
            zip = normaLInput.getString("zip");

            //offices - name, officialIndices
            for (int i = 0; i < offices.length(); i++) {
                JSONObject jsonObject = offices.getJSONObject(i);
                String officeName = jsonObject.getString("name");
                String officialIndices = jsonObject.getString("officialIndices");

//                Log.d(TAG, "parseJSON: office NAME >>>> " + officeName);
//                Log.d(TAG, "parseJSON: OFFICE_INDICES >>>> " + officialIndices);

                String[] index = officialIndices.substring(1, officialIndices.length() - 1).split(",");
                int[] nums = new int[index.length];
                for (int j = 0; j < index.length; j++) {
                    nums[j] = Integer.parseInt(index[j]);
                }

                for (int k = 0; k < nums.length; k++) {
                    JSONObject jsonObj = officials.getJSONObject(nums[k]);
                    String officialName = jsonObj.getString("name");

                    Log.d(TAG, "parseJSON: OfficialNAME >>>>" + officialName);

                    String address = handleAddress(jsonObj);
                    Log.d(TAG, "parseJSON: ADDRESS >>>> " + address);

                    String party1 = jsonObj.has("party") ? jsonObj.getString("party") : UNKNOWN;
                    String party = party1.equals("Nonpartisan") ? "Unknown" : party1;



                    String phone = jsonObj.has("phones") ? jsonObj.getJSONArray("phones").getString(0) : OMITTED;
                    String url = jsonObj.has("urls") ? jsonObj.getJSONArray("urls").getString(0) : OMITTED;
                    String email = jsonObj.has("emails") ? jsonObj.getJSONArray("emails").getString(0) : OMITTED;
                    String photoUrl = jsonObj.has("photoUrl") ? jsonObj.getString("photoUrl") : OMITTED;

                    JSONArray channel = jsonObj.has("channels") ? jsonObj.getJSONArray("channels") : null;

                    String facebook = (channel != null) ? handleChannel(channel, "Facebook") : OMITTED;
                    Log.d(TAG, "parseJSON: facebook >> " +  facebook);

                    String twitter = (channel != null) ? handleChannel(channel, "Twitter") : OMITTED;
                    Log.d(TAG, "parseJSON: twitter >> " + twitter);
                    String youTube = (channel != null) ? handleChannel(channel, "YouTube") : OMITTED;
                    Log.d(TAG, "parseJSON: youTube >> " + youTube);
                    String googlePlus = (channel != null) ? handleChannel(channel, "GooglePlus") : OMITTED;
                    Log.d(TAG, "parseJSON: googlePlus >> "+ googlePlus);

                    Official official = new Official(officeName,officialName,party,address,phone,
                            url,email,photoUrl,googlePlus,facebook,twitter,youTube);

                    officialArrList.add(official);

                }
            }
            Log.d(TAG, "parseJSON: officialArrList" + officialArrList);

            return officialArrList;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String handleAddress(JSONObject jsonObj) {

        String address = "";
        if (!jsonObj.has("address")) {
            address = OMITTED;
        } else {

            try {
                JSONArray addr = jsonObj.getJSONArray("address");
                JSONObject addrObj = addr.getJSONObject(0);
                if (addrObj.has("line1")) {
                    String ln1 = addrObj.getString("line1");
                    address += ln1 + "\n";
                }
                if (addrObj.has("line2")) {
                    String ln2 = addrObj.getString("line2");
                    address += ln2 + "\n";
                }
                if (addrObj.has("city")) {
                    String ct = addrObj.getString("city");
                    address += ct + " ";
                }
                if (addrObj.has("state")) {
                    String st = addrObj.getString("state");
                    address += st + ", ";
                }
                if (addrObj.has("zip")) {
                    String z = addrObj.getString("zip");
                    address += z;
                }

                Log.d(TAG, "handleAddress: >>>> " + address);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return address;

    }

    private String handleChannel(JSONArray channel, String key) {
        String output = "";

        try {
            for (int i = 0; i < channel.length(); i++) {
                if(channel.getJSONObject(i).getString("type").equals(key))
                output = channel.getJSONObject(i).getString("id");
            }
            return output;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String s) {

        officialArrList = parseJSON(s);
        Object[] output = new Object[2];
        output[0] = city + ", " + state + " " + zip;
        output[1] = officialArrList;

        mainActivity.updateResult(output);

    }

}
