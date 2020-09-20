package com.example.findnearbyplace;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
    ,   GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static  final int Request_user_location_code=99;
    private double latitude,longitude;
    private int Radius=10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClick(View v)
    {
        String hospital="hospital",school="school",restaurant="restaurant";
        Object trangsferData[]=new Object[2];
        GetNearbyPlace getNearbyPlace=new GetNearbyPlace();
        switch (v.getId())
        {
            case R.id.imagebtn_search:
                EditText addressfield=(EditText) findViewById(R.id.location_search);
                String address = addressfield.getText().toString();
                List<Address> addressList=null;
                MarkerOptions userMarker=new MarkerOptions();
                if(!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try {
                        addressList=geocoder.getFromLocationName(address,6);
                        if(addressList!=null)
                        {
                            for(int i=0;i<addressList.size();i++)
                            {
                                Address userAddress=addressList.get(i);
                                LatLng latLng=new LatLng(userAddress.getLatitude(),userAddress.getLongitude());

                                userMarker.position(latLng);
                                userMarker.title(address);
                                userMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(userMarker);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                            }
                        }
                        else {
                            Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(this,"Please write location name",Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.imagebtn_hoptital:
                mMap.clear();
                String url=getUrl(latitude,longitude,hospital);
                trangsferData[0]=mMap;
                trangsferData[1]=url;

                getNearbyPlace.execute(trangsferData);
                Toast.makeText(this,"Searching for nearby hospital",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing nearby hospital",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imagebtn_school:
                mMap.clear();
                url = getUrl(latitude, longitude, school);
                trangsferData[0]=mMap;
                trangsferData[1]=url;

                getNearbyPlace.execute(trangsferData);
                Toast.makeText(this,"Searching for nearby school",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing nearby school",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imagebtn_restaurants:
                mMap.clear();
                url= getUrl(latitude,longitude,restaurant);
                trangsferData[0]=mMap;
                trangsferData[1]=url;

                getNearbyPlace.execute(trangsferData);
                Toast.makeText(this,"Searching for nearby restaurant",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing nearby restaurant",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private String getUrl(double latitude,double longitude,String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location= "+latitude+","+longitude);
        googleURL.append("&radius=" + Radius);
        googleURL.append("&type" +nearbyPlace);
        googleURL.append("&key= "+"AIzaSyAlTtlvN_hvgxLKJvv6XChULYGhBMO0MXU");

        Log.d("GoogleMpasActivity","url= "+googleURL.toString());

        return googleURL.toString();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String []{Manifest.permission.ACCESS_FINE_LOCATION},Request_user_location_code );
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String []{Manifest.permission.ACCESS_FINE_LOCATION},Request_user_location_code );
            }
            return false;
        }
        else
            {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_user_location_code:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient==null)
                        {
                            buildGoogleClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied ", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected  synchronized void buildGoogleClient()
    {
       googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();


        lastLocation=location;
        if(currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

       /* LatLng vietnam = new LatLng(16.058036, 108.237237);
        mMap.addMarker(new MarkerOptions().position(vietnam).title("Marker in VietNam"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vietnam,18));*/

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker =mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
