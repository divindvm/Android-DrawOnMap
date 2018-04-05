package com.example.divindivakaran.map;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    int mapClick = 0;
    boolean finished = false;
    boolean start= false;
    int POLYGON_POINTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Long Press To start adding Locations to your trip. Once finished click Get Direction", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {

        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Play Services !", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
//        gotToLocationZoom(39.008224,-76.8984527,15);
        if (mGoogleMap != null){
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
//                    if(mapClick ==0) {
//                        RemoveEverything();
                        start=true;
                        mapClick+= 1;
                    if (finished == false){

                        Geocoder gc = new Geocoder(getApplicationContext());
                        List<android.location.Address> list = null;
                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        android.location.Address address = list.get(0);
                        String locality = address.getLocality();

//                        Toast.makeText(getApplicationContext(), " Locality is "+locality, Toast.LENGTH_LONG).show();

                        if (locality != null){
                            MainActivity.this.setMarker(locality, latLng.latitude, latLng.longitude);
                        }
                        else {
                            MainActivity.this.setMarker("Unknown", latLng.latitude, latLng.longitude);
                        }

                        POLYGON_POINTS+=1;
                    }
//                    }
                    if (mapClick ==2){
                        finished=true;
                    }
                    if (finished == true){
//                        RemoveEverything();
                    }


                }
            });

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (start == true){
//                        MainActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);

                        Geocoder gc = new Geocoder(getApplicationContext());
                        List<android.location.Address> list = null;
                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        android.location.Address address = list.get(0);
                        String locality = address.getLocality();

//                        Toast.makeText(getApplicationContext(), " Locality is "+locality, Toast.LENGTH_LONG).show();

                        if (locality != null){
                            MainActivity.this.setMarker(locality, latLng.latitude, latLng.longitude);
                        }
                        else {
                            MainActivity.this.setMarker("Unknown", latLng.latitude, latLng.longitude);
                        }


                        POLYGON_POINTS+=1;
                    }
                }
            });

        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mGoogleMap.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    private void gotToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private void gotToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);


    }

//    Marker marker;
//    Marker marker1;
//    Marker marker2;
    Polyline line;

    ArrayList<Marker> markers = new ArrayList<Marker>();

    Polygon shape;

    public void geoLocate(View view) throws IOException {

        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(location, 2);
        android.location.Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();
        gotToLocationZoom(lat, lng, 15);
        setMarker(locality, lat, lng);

    }

    Circle circle;

    private void setMarker(String locality, double lat, double lng) {
//        if(marker != null){
//          removeEverything();
//        }

//        if (finished == true){
//            RemoveEverything();
//        }

        MarkerOptions options = new MarkerOptions()
                                .title(locality)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map))
                                .position(new LatLng(lat, lng))
                                .snippet("Point "+ POLYGON_POINTS );

        markers.add(mGoogleMap.addMarker(options));


//        if (markers.size() == POLYGON_POINTS){
//            drawPolygon();
//        }

            drawPolygon();

//        if(marker1 == null ){
//            marker1= mGoogleMap.addMarker(options);
//        }
//        else if(marker2 ==null){
//            marker2= mGoogleMap.addMarker(options);
//            drawLine();
//        }
//        else{
//            removeEverything();
//            marker1= mGoogleMap.addMarker(options);
//        }




//        circle = drawCircle(new LatLng(lat, lng));
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {



//        Toast.makeText(this, "Get Direction Clicked ! latttude of origin is : "+ origin.latitude + "lat of dest is : "+ dest.latitude, Toast.LENGTH_SHORT).show();

        String str_origin ="origin=" + origin.latitude + "," + origin.longitude;
        String str_dest ="destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_origin + "&" +str_dest + "&" + sensor + "&" +mode;
        String output ="json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private  String requestDirection( String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream =httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){

                stringBuffer.append(line);

            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();



        } catch (Exception e) {
            e.printStackTrace();

        }finally {

            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return  responseString;
    }

    private void drawPolygon() {
//        PolygonOptions options = new PolygonOptions()
//                                .fillColor(0x330000FF)
//                                .strokeWidth(3)
//                                .strokeColor(Color.RED);
//        for (int i=0; i< POLYGON_POINTS; i++){
//            options.add(markers.get(i).getPosition());
//        }
//        shape = mGoogleMap.addPolygon(options);



        PolylineOptions options = new PolylineOptions()
                                    .color(Color.rgb(95, 170, 76))
                                    .width(4);

        for (int i=0; i<= POLYGON_POINTS; i++){
            options.add(markers.get(i).getPosition());
        }

        line = mGoogleMap.addPolyline(options);



    }

    private void RemoveEverything() {
            for( Marker marker : markers){
                marker.remove();
            }
            markers.clear();
//            shape.remove();
//            shape= null;
            line.remove();
            line=null;


    }

//    private void drawLine() {
//
//        PolylineOptions options = new PolylineOptions()
//                                    .add(marker1.getPosition())
//                                    .add(marker2.getPosition())
//                                    .color(Color.BLUE)
//                                    .width(4);
//
//        line = mGoogleMap.addPolyline(options);
//
//    }

    private Circle drawCircle(LatLng latLng) {

        CircleOptions options = new CircleOptions()
                                .center(latLng)
                                .radius(1000)
                                .fillColor(0x33FF0000)
                                .strokeColor(Color.BLACK)
                                .strokeWidth(2);

        return mGoogleMap.addCircle(options);
    }

//    private  void removeEverything(){
//        marker1.remove();
//        marker1=null;
//        marker2.remove();
//        marker2=null;
//        line.remove();
////        circle.remove();
////        circle=null;
//    }





    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location == null){
            Toast.makeText(this, "Can't Fetch Current Location!", Toast.LENGTH_LONG).show();
        }
        else{
            LatLng ll= new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);
        }
    }

    public void clearRoute(View view) {
        if (finished == true) {
            RemoveEverything();
            mapClick = 0;
            finished = false;
            start = false;
            POLYGON_POINTS = 0;
        }
    }

    public void getDirections(View view) {

//        LatLng l1 = new LatLng(39.008224,-76.8984527);
//        LatLng l2 = new LatLng(19.008224,-96.8984527);
        int point=0;
        for (point=0; point<POLYGON_POINTS-1 ; point++){



            LatLng l1 = new LatLng(markers.get(point).getPosition().latitude,markers.get(point).getPosition().longitude);
            LatLng l2 = new LatLng(markers.get(point+1).getPosition().latitude,markers.get(point+1).getPosition().longitude);

//            Toast.makeText(getApplicationContext(),"Lat 1 is : " +markers.get(0).getPosition().latitude+"long is "+markers.get(0).getPosition().longitude,Toast.LENGTH_SHORT).show();

            String url = getRequestUrl(l1,l2);

            TaskRequestDirections taskRequestDirection = new TaskRequestDirections();
            AsyncTask<String, Void, String> urlOut = taskRequestDirection.execute(url);

//            Toast.makeText(this, "URl from response is !" +urlOut, Toast.LENGTH_SHORT).show();

        }
        circle = drawCircle(new LatLng(markers.get(point).getPosition().latitude,markers.get(point).getPosition().longitude));
        
    }

    public class  TaskRequestDirections extends AsyncTask<String, Void ,String >{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);


            }catch (IOException e){
                e.printStackTrace();
            }

            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            AsyncTask<String, Void, List<List<HashMap<String, String>>>> routes = taskParser.execute(s);
//            Toast.makeText(getApplicationContext(), "Routes returned !" +routes, Toast.LENGTH_SHORT).show();

        }
    }

    public class TaskParser extends AsyncTask<String, Void , List<List<HashMap<String, String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes =null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser =new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
//                Toast.makeText(getApplicationContext(), "Parsed Direction is : " +routes, Toast.LENGTH_SHORT).show();


            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Toast.makeText(getApplicationContext(), "Get Direction Clicked !" + routes, Toast.LENGTH_SHORT).show();
            return routes;


        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            if (lists != null){
//
//                Toast.makeText(getApplicationContext(),"List value :" +lists, Toast.LENGTH_SHORT);
                for (List<HashMap<String, String>> path : lists){
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    if (path !=null){

                        String lat="";
                        String lon="";

                        for (HashMap<String, String> point : path ){

                             lat = point.get("lat");
                             lon = point.get("lng");

                            if(lat != null && lon != null){
                                points.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));
                            }
                            else{
                                continue;
                            }
//
//                                LatLng ll= new LatLng()
//                            double def=Double.valueOf(lat);
//                            double lon = Double.parseDouble(point.get("lng"));
//                             points.add(new LatLng(Double.parseDouble(point.get("lat")),Double.parseDouble(point.get("lng"))));
//                            Toast.makeText(getApplicationContext(),"path from class" + lat, Toast.LENGTH_SHORT).show();
                        }
//                        double abc=Double.parseDouble(lat);

//                        Toast.makeText(getApplicationContext(),"path from class" + lat + "and lob :" +lon, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"path Empty", Toast.LENGTH_SHORT).show();
                    }

//                      points.add(new LatLng(10.0159,76.3419));
//                        points.add(new LatLng(9.7138,76.6829));
//                    points.add(new LatLng(55.008224,-56.8984527));
//                    points.add(new LatLng(65.008224,-46.8984527));
//                    points.add(new LatLng(75.008224,-36.8984527));
//                    points.add(new LatLng(85.008224,-26.8984527));
//                    points.add(new LatLng(95.008224,-16.8984527));
                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.rgb(95, 170, (20*POLYGON_POINTS)));
                    polylineOptions.geodesic(true);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "List Variable is null", Toast.LENGTH_SHORT).show();
            }
            if (polylineOptions != null){
                mGoogleMap.addPolyline(polylineOptions);
            }
            else{
                Toast.makeText(getApplicationContext(),"Directions Not Found !", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
