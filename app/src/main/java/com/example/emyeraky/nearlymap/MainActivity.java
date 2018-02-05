package com.example.emyeraky.nearlymap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;

import com.github.clans.fab.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.piotrek.customspinner.CustomSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.example.emyeraky.nearlymap.MapsActivity.context;
import static com.example.emyeraky.nearlymap.MapsActivity.email;


public class MainActivity extends AppCompatActivity {
    public static double placeLat;
    public static double placeLng;
    SwipeRefreshLayout swipe;
    TextView txtspin, name, address, open, allreview;
    ImageView photoview;
    ListView listreview;
    CustomSpinner customspinner;
    PlaceData placedata;
    static String placeId;
    static String placen;
    Review[] reviewdata;
    ArrayList<String> hours;
    Toolbar toolbar;
    Spinner spinner2;
    FloatingActionButton menu1, menu2, menu3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        swipe=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipe.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        MainActivity.FetchVideo fetchVideo = new MainActivity.FetchVideo();
                        fetchVideo.execute(placeId);

                    }
                },3000);
            }
        });
        photoview = (ImageView) findViewById(R.id.toolbarImage);
        name = (TextView) findViewById(R.id.nameloc);
        allreview = (TextView) findViewById(R.id.allreview);
        address = (TextView) findViewById(R.id.addressloc);
        open = (TextView) findViewById(R.id.openhour);
        listreview = (ListView) findViewById(R.id.review);
        collapsingToolbarLayout.setTitle(placen);

        MainActivity.FetchVideo fetchVideo = new MainActivity.FetchVideo();
        fetchVideo.execute(placeId);
        ///////////////////////////////////////////////////////////////
        menu1 = (FloatingActionButton) findViewById(R.id.subFloatingMenu1);
        menu2 = (FloatingActionButton) findViewById(R.id.subFloatingMenu2);
        menu3 = (FloatingActionButton) findViewById(R.id.subFloatingMenu3);

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + GetNearbyPlacesData.lat + "," + GetNearbyPlacesData.lng + " (" + placen + ")";
                shareIt(placen, geoUri);

            }
        });
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = placedata.getFormatted_phone_number();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + phone));
                startActivity(i);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();

            }
        });
    }

    private void addToFavorite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("favorite");
        Favorite favorite = new Favorite(placen, GetNearbyPlacesData.placeid, placeLat,placeLng);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        posts.child(myAuth.getCurrentUser().getUid()).push().setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.done_add_Tofavorite, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.not_added_Tofavorite, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void shareIt(String name, String loc) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, loc);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
    //get data from Json
    private void getplaceDataFromJson(String placeJsonStr) throws JSONException {
        ArrayList<String> photos = new ArrayList<>();
        JSONObject objHours = null;
        JSONObject placeJson = new JSONObject(placeJsonStr);
        JSONObject jsonObj = placeJson.getJSONObject("result");
        if (jsonObj.has("photos")) {
            JSONArray photoArray = jsonObj.getJSONArray("photos");
            for (int i = 0; i < photoArray.length(); i++) {
                JSONObject photo = photoArray.getJSONObject(i);
                photos.add("https://maps.googleapis.com/maps/api/place/photo?"
                        + "maxwidth=400&photoreference=" + photo.getString("photo_reference")
                        + "&key=AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
            }
            Picasso.with(context).load(photos.get(0)).into(photoview);
            JSONArray reviewArray = jsonObj.getJSONArray("reviews");
            reviewdata = new Review[reviewArray.length()];

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                reviewdata[i] = new Review();
                reviewdata[i].setAuther(review.getString("author_name"));
                reviewdata[i].setUrl(review.getString("author_url"));
                reviewdata[i].setProfile(review.getString("profile_photo_url"));
                reviewdata[i].setText(review.getString("text"));
                reviewdata[i].setTime(review.getString("relative_time_description"));

            }
            AdapterReview adapterReview = new AdapterReview(context, reviewdata);
            listreview.setAdapter(adapterReview);
        }

        if (jsonObj.has("opening_hours")) {
            objHours = jsonObj.getJSONObject("opening_hours");
            open.setText(objHours.getString("open_now"));
            JSONArray jsonStrings = objHours.getJSONArray("weekday_text");
            String strings[] = new String[jsonStrings.length()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = jsonStrings.getString(i);
            }
            hours = new ArrayList<>(Arrays.asList(strings));
            txtspin = (TextView) findViewById(R.id.text);
            txtspin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinner2 = (Spinner) findViewById(R.id.spinner2);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, hours);
                    spinner2.setAdapter(adapter1);
                }
            });

        }
        placedata = new PlaceData();
        placedata.setFormatted_address(jsonObj.getString("formatted_address"));
        placedata.setName(placen);
        name.setText(placen);
        address.setText(placedata.getFormatted_address());
        if (jsonObj.has("formatted_phone_number")) {
            placedata.setFormatted_phone_number(jsonObj.getString("formatted_phone_number"));
        }
    }

    public class FetchVideo extends AsyncTask<String, ProgressDialog, String> {
        private String LOG_TAG = MainActivity.FetchVideo.class.getSimpleName();
        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {

                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String videoJsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + params[0] + "&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0";

                URL url = new URL(FORECAST_BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                videoJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return videoJsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                getplaceDataFromJson(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
