package com.example.emyeraky.nearlymap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.search.material.library.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.lat;
import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.lng;
import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.placeName;
import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.placelat;
import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.placelng;
import static com.example.emyeraky.nearlymap.GetNearbyPlacesData.searchModelList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    static GoogleMap mMap;
    static double latitude;
    static double longitude;
    static int PROXIMITY_RADIUS = 10000;
    public static String status = "";
    public static String email = "";
    public static String name = "";
    GoogleApiClient mGoogleApiClient;
    static Location mLastLocation;
    static Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    static String url;
    static Context context;
    static String placename;
    PlaceData placeData;
    View view;
    ArrayList<String> names,namefav;
    //////////////////
    private MaterialSearchView searchView;
    static StringBuilder googlePlacesUrl;
    String searchArray;
    SearchModel searchModel;
    static ArrayList<SearchModel> searchname = new ArrayList<>();
    ArrayAdapter adapter1;

    static String type = "";
    ArrayList<LatLng> data_lat_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = getBaseContext();
        searchModel = new SearchModel();
        searchname = searchModelList;

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.header, null);
        placeData = new PlaceData();
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarTop);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        getInfoDatabase();
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, R.string.GPS_Enable, Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        names = new ArrayList<String>();
        namefav = new ArrayList<String>();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                search(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (searchname == null) {
                    Toast.makeText(MapsActivity.this, R.string.choose_Catogory, Toast.LENGTH_SHORT).show();
                    return false;
                }
                for (int i = 0; i < searchname.size(); i++) {
                    names.add(searchname.get(i).getName());
                }
                adapter1 = new ArrayAdapter(MapsActivity.this, android.R.layout.simple_expandable_list_item_1, names);
                Log.d("ddd", query);

                if (names.size() > 0) {
                    searchView.mSearchSrcTextView.setAdapter(adapter1);
//                    searchView.setAdapter(adapter1);
                }
                else if(placeName.size()>0){
                adapter1 = new ArrayAdapter(MapsActivity.this, android.R.layout.simple_expandable_list_item_1, placeName);
                searchView.mSearchSrcTextView.setAdapter(adapter1);
                searchView.setAdapter(adapter1);
                }
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
        MapsActivity.SearchAdapter adapter = new MapsActivity.SearchAdapter();
        searchView.setAdapter(adapter);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarTop, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (isNetworkConnected() && mLastLocation != null) {
                    if (menuItem.isChecked()) menuItem.setChecked(false);
                    else menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    switch (menuItem.getItemId()) {
                        case R.id.inbox:
                            mMap.clear();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 11.0f));
                            onLocationChanged(mLastLocation);
                            Toast.makeText(getApplicationContext(), R.string.currentLocation, Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.starred:
                            String Restaurant = "food";
                            type = "Restaurant";
                            map(Restaurant);
                            return true;
                        case R.id.sent_mail:
                            String Hotels = "lodging";
                            type = "Hotels";
                            map(Hotels);
                            return true;
                        case R.id.drafts:
                            String Hospital = "hospital";
                            type = "Hospital";
                            map(Hospital);
                            return true;
                        case R.id.spam:
                            String Company = "travel_agency";
                            type = "Company";
                            map(Company);
                            return true;

                        case R.id.fav:
                            Toast.makeText(getApplicationContext(), R.string.Favorite, Toast.LENGTH_SHORT).show();
                            getFavorite();
                            return true;
                        case R.id.logout:
                            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                            editor.putInt("user_id", -1);
                            editor.commit();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getBaseContext(), Login.class));
                            finish();
                            return true;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.Wrong_choose, Toast.LENGTH_SHORT).show();
                            return true;
                    }
                } else
                    Toast.makeText(context, R.string.OpenNetwork_NotcurrentLocation, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    List<Favorite> favoriteList;

    private void getFavorite() {
        mMap.clear();
        if (names.size() > 0&& searchname.size()>0) {
            names.clear();
            searchname.clear();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("favorite");

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        posts.child(myAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Favorite> results = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Favorite>>() {
                });
                favoriteList = new ArrayList<>(results.values());
                favoriteListData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void favoriteListData() {
        searchModelList = new ArrayList<>();
        placeName = new ArrayList<>();
        placelat = new ArrayList<>();
        placelng = new ArrayList<>();
        data_lat_long = new ArrayList<>();
        data_lat_long = GetNearbyPlacesData.data_lat_long;
        final ArrayList<String> data_placeid = new ArrayList<>();
        mMap.clear();
        for (int i = 0; i < favoriteList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            GetNearbyPlacesData.searchModels = new SearchModel();
            MarkerOptions markerOptions = new MarkerOptions();

            lat = favoriteList.get(i).getLat();
            lng = favoriteList.get(i).getLng();
            placelat.add(lat);
            placelng.add(lng);
            placeName.add(favoriteList.get(i).getName());
            LatLng latLng = new LatLng(lat, lng);
            GetNearbyPlacesData.searchModels.setLat(lat);
            GetNearbyPlacesData.searchModels.setLang(lng);
            GetNearbyPlacesData.searchModels.setName(favoriteList.get(i).getName());
            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(placelat.get(i));
            temp.setLongitude(placelng.get(i));
            String dis = mLastLocation.distanceTo(temp) / 1000 + " KM";
            searchModelList.add(GetNearbyPlacesData.searchModels);

            GetNearbyPlacesData.placeid = favoriteList.get(i).getId();
            data_placeid.add(favoriteList.get(i).getId());
            markerOptions.position(latLng);
            markerOptions.title(placeName + "");
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d("markerclickedlonlat", marker.getPosition() + "    id   " + marker.getId());
                    for (int i = 0; i < favoriteList.size(); i++) {
                        Log.d("longlat", "" + favoriteList.get(i) + "  allplaceid  " + data_placeid.get(i));
                        LatLng position = marker.getPosition();
                        double lat = position.latitude;
                        double lng = position.longitude;

                        if ((lat == favoriteList.get(i).getLat()) && (lng == favoriteList.get(i).getLng())) {
                            if (isNetworkConnected()) {
                                MainActivity.placeLat = placelat.get(i);
                                MainActivity.placeLng = placelng.get(i);
                                MainActivity.placeId = data_placeid.get(i);
                                MainActivity.placen = placeName.get(i);
                                MapsActivity.placename = placeName.get(i);
                            }
                        }
                    }

                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    return true;
                }
            });
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(placeName + "").icon(BitmapDescriptorFactory.fromResource(R.drawable.favorite)).draggable(true));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
    }

    private void getInfoDatabase() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        final TextView nametxt = (TextView) header.findViewById(R.id.usernameheader);
        final TextView emailtxt = (TextView) header.findViewById(R.id.emailheader);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("users");

        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, User> results = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, User>>() {
                });

                List<User> users = new ArrayList<>(results.values());

                for (User user : users) {
                    if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        nametxt.setText(user.getName());
                        emailtxt.setText(user.getEmail());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void map(String type) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 11.0f));
        Log.d("onClick", "Button is Clicked");
        mMap.clear();
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        url = getUrl(latitude, longitude, type);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData.mContext = getBaseContext();
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(MapsActivity.this, R.string.Nearby + "" + type, Toast.LENGTH_LONG).show();

    }

    private void search(String query) {
        searchView.setAdapter(adapter1);
        if (GetNearbyPlacesData.size != 0) {
            if (query != null) {
                String text = query;
                for (int i = 0; i < 20; i++) {
                    searchArray = searchModelList.get(i).getName();
                    if (searchArray.equals(text)) {
                        mMap.clear();
                        LatLng mLatLng = new LatLng(searchModelList.get(i).getLat(), searchModelList.get(i).getLang());
                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(searchModelList.get(i).getLat());
                        temp.setLongitude(searchModelList.get(i).getLang());
                        String dis = mLastLocation.distanceTo(temp) / 1000 + " KM";
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(searchModelList.get(i).getLat(), searchModelList.get(i).getLang()))
                                .radius(100)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE));

                        drawPath(mLatLng);
                        Toast.makeText(MapsActivity.this, R.string.search_place + dis, Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        System.out.println("does not contain.");
                    }
                }
            }
        } else {
            Snackbar.make(findViewById(R.id.search_view), R.string.Snackbar_Message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            searchView.clearFocus();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.Message_To_OpenGPS)
                .setCancelable(false)
                .setPositiveButton(R.string.Goto_Settings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.cancel_Gps,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.color.show);
        alert.show();
    }

    static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    static public String getUrl(double latitude, double longitude, String nearbyPlace) {

        googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(R.string.Current_Position+"");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MapsActivity.this, R.string.currentLocation, Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format(R.string.format+"", latitude, longitude));
        if (!status.equals(""))
            map(status);
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, R.string.RequestPermissions, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private class SearchAdapter extends BaseAdapter implements Filterable {

        private ArrayList<String> data;

        LayoutInflater inflater;

        public SearchAdapter() {
            inflater = LayoutInflater.from(MapsActivity.this);
            data = new ArrayList<String>();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (!TextUtils.isEmpty(constraint)) {
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();

                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null) {
                        data = (ArrayList<String>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MapsActivity.SearchAdapter.MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                mViewHolder = new MapsActivity.SearchAdapter.MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MapsActivity.SearchAdapter.MyViewHolder) convertView.getTag();
            }

            String currentListData = (String) getItem(position);

            mViewHolder.textView.setText(currentListData);

            return convertView;
        }

        private class MyViewHolder {
            TextView textView;

            public MyViewHolder(View convertView) {
                textView = (TextView) convertView.findViewById(android.R.id.text1);
            }
        }
    }

    public void drawPath(final LatLng placeLocation) {
        final LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(latLng)
                .to(placeLocation)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 3, Color.RED);
                            mMap.addPolyline(polylineOptions);
                            mMap.addMarker(new MarkerOptions().position(placeLocation)
                                    .title(R.string.title_Marker + ""));
                            Log.e("Direction :", "Direction Seccuss");
                        } else {
                            Log.e("Direction :", direction.getErrorMessage());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("Direction :", "Failure :" + t.getLocalizedMessage());
                        Toast.makeText(MapsActivity.this,R.string.Check_Network, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}