package com.example.emyeraky.nearlymap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    static SearchModel searchModels;
    static List<String> placeName;
    static ArrayList<SearchModel> searchModelList;
    static Context mContext;
    static String placeid;
    static double lat, lng;
    static int size;
    static ArrayList<LatLng> data_lat_long;
    static ArrayList<Double> placelat, placelng;
    List<HashMap<String, String>> nearbyPlacesList;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(result);
        size = nearbyPlacesList.size();
        ShowNearbyPlaces(nearbyPlacesList);
        MapsActivity.searchname = searchModelList;
        Log.d("ddd", MapsActivity.searchname.size() + " ");
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        searchModelList = new ArrayList<>();
        placeName = new ArrayList<>();
        data_lat_long = new ArrayList<>();
        final ArrayList<String> data_placeid = new ArrayList<>();
        placelat = new ArrayList<>();
        placelng = new ArrayList<>();

        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            searchModels = new SearchModel();
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            googlePlace.get("lat");
            lat = Double.parseDouble(googlePlace.get("lat"));
            lng = Double.parseDouble(googlePlace.get("lng"));
            placelat.add(lat);
            placelng.add(lng);
            placeName.add(googlePlace.get("place_name"));
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            data_lat_long.add(latLng);
            searchModels.setLat(lat);
            searchModels.setLang(lng);
            searchModels.setName(placeName.get(i));
            searchModelList.add(searchModels);

            placeid = googlePlace.get("place_id");
            data_placeid.add(placeid);
            markerOptions.position(latLng);
            Log.d("places", placeid);
            markerOptions.title(placeName + " : " + vicinity);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d("markerclickedlonlat", marker.getPosition() + "    id   " + marker.getId());
                    for (int i = 0; i < data_lat_long.size(); i++) {
                        Log.d("longlat", "" + data_lat_long.get(i) + "  allplaceid  " + data_placeid.get(i));

                        if (marker.getPosition().equals(data_lat_long.get(i))) {
                            MainActivity.placeLat = placelat.get(i);
                            MainActivity.placeLng = placelng.get(i);
                            MainActivity.placeId = data_placeid.get(i);
                            MainActivity.placen = placeName.get(i);
                            MapsActivity.placename = placeName.get(i);
                        }
                    }
                    Log.d("finalplaceid", "" + placeid + "");

                    Intent i = new Intent(mContext, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    return true;
                }
            });
            if (MapsActivity.googlePlacesUrl.toString().contains("&type=food")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.mmwine)).draggable(true));
            } else if (MapsActivity.googlePlacesUrl.toString().contains("&type=lodging")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.mmbed)).draggable(true));
            } else if (MapsActivity.googlePlacesUrl.toString().contains("&type=hospital")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.mmkit)).draggable(true));

            } else if (MapsActivity.googlePlacesUrl.toString().contains("&type=travel_agency")) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.mmairplane)).draggable(true));

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
    }
}
