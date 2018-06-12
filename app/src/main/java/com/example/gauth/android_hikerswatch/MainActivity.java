package com.example.gauth.android_hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {//This code is basically taking input from user when he is prompted for permission
        //request code is the number 1
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {//for the code to work it needs to recheck the users location hence we have written the inner if statement
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                //the location Manager code below gets the location and 0,0 are just numbers on how often we want to update location
                //To save battery we have to update location less frequently
                //the below code is the location when he grants permission for 1st time.When later location changes the
                //the last part of this file is used.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView lattitude=(TextView)findViewById(R.id.latTextView);
        final TextView longitude=(TextView)findViewById(R.id.lonTextView);
        final TextView accuracy=(TextView)findViewById(R.id.accuracyTextView);
        final TextView altitude=(TextView)findViewById(R.id.altitudeTextView);
        final TextView addressLine=(TextView)findViewById(R.id.addressTextView);

        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //called when phone moves
                Log.i("Location",location.toString());

                Log.i("Lattitude is",Double.toString(location.getLatitude()));
        lattitude.setText("Lattitude :"+Double.toString(location.getLatitude()));

                Log.i("Longitude is ",Double.toString(location.getLongitude()));
                longitude.setText("Longitude :"+Double.toString(location.getLongitude()));

                Log.i("Accuracy is ",Double.toString(location.getAccuracy()));
                accuracy.setText("Accuracy :"+Double.toString(location.getAccuracy()));

                Log.i("Altitude is",Double.toString(location.getAltitude()));
                altitude.setText("Altitude :"+Double.toString(location.getAltitude()));

                //Reverse geocoding
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try { //try catch needed if it is not a valid address etc
                    List<Address> listAddress=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(listAddress!=null&&listAddress.size()>0)
                    {
                        //Country name is null
                        if(listAddress.get(0).getCountryName()!=null) {
                            Log.i("Country Name", listAddress.get(0).getCountryName());//get(0) means to get the 1st item in the array
                        }
                        else{
                            Log.i("Country Name","IS NULL!!!");
                        }

                        //Country code is null
                        if(listAddress.get(0).getCountryCode()!=null) {
                            Log.i("Country Code", listAddress.get(0).getCountryCode());
                        }
                        else{Log.i("Country Code","IS NULL!!!");}


                        //Check if Address line is null
                        if(listAddress.get(0).getPostalCode()!=null) {
                            Log.i("Address Line", listAddress.get(0).getAddressLine(0));
                         addressLine.setText("Address :"+listAddress.get(0).getAddressLine(0));                        }
                        else{Log.i("Address Line","IS NULL!!!");
                        }

                        //Check if postal code is null
                        if(listAddress.get(0).getPostalCode()!=null)
                        {Log.i("Postal Code",listAddress.get(0).getPostalCode());
                        }
                        else
                        { Log.i("Postal code is","IS NULL!!!");  }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
//called when location service is enabled or disabled
            }

            @Override
            public void onProviderEnabled(String provider) {
//when location service enabled by user
            }

            @Override
            public void onProviderDisabled(String provider) {
//when location service enabled by user
            }
        };
        if (Build.VERSION.SDK_INT<23)//SDK<23 means device running on previous than Marshmallow
        //in that case we donot need to ask for permission from user and can straight away use gps
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {//ask for permission
                //We need string for what we want permission for
                //The integer can be any number you want and is esentially to check the other end that this particular request was mad
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                //we have permission after being granted for 1st time then this code will update location everytime.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                //the above code actually calls the listner automatically The 1st 0 is interval time it calls the listner in millisecon
                //The 2nd 0 is the distance in meter based on which listner is called
            }
        }
    }
}
