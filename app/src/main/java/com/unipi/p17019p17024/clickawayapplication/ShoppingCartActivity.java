package com.unipi.p17019p17024.clickawayapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ShoppingCartActivity extends AppCompatActivity implements LocationListener {
    Button button1;

    LocationManager locationManager;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    //database Firebase
    FirebaseDatabase database;
    DatabaseReference myRef,myRef2;

    boolean updateOrders = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        button1 = findViewById(R.id.buttonSubmitOrder);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        getIntent().getStringExtra("username");
        getIntent().getStringExtra("userID");
        getIntent().getStringExtra("email");

        //database Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Orders");
        myRef2 = database.getReference("Orders/quantity");


    }

    public void submitOrder(View view){

        //check for gps permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        updateOrders = true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double x = location.getLatitude();
        double y = location.getLongitude();
        Date dateAdded = Calendar.getInstance().getTime();


        if(updateOrders){
            HaversineDistance haversineDistance = new HaversineDistance();

            double distanceS = haversineDistance.haversine(37.976351, 23.733375 ,37.998471217152435, 23.74581833876745);
            double distanceIl = haversineDistance.haversine(38.034954, 23.703296 , 37.998471217152435, 23.74581833876745);
            double distanceP = haversineDistance.haversine(37.942260, 23.650997 ,37.998471217152435, 23.74581833876745);
            double distanceG = haversineDistance.haversine(37.877499, 23.755749 , 37.998471217152435, 23.74581833876745);
            double distanceAP = haversineDistance.haversine(38.013034, 23.817960, 37.998471217152435, 23.74581833876745);

            double[] distancesArray = new double[] {distanceS, distanceIl, distanceP, distanceG, distanceAP};
            String[] storesArray = new String[] {"Syntagma", "Ilion", "Piraeus", "Glyfada", "Agia Paraskevi"};

            //
            //Bubble Sort Algorithm
            //
            int n = distancesArray.length;
            double temp = 0.0;
            String temp2 = "";
            for(int i=0; i < n; i++){
                for(int j=1; j < (n-i); j++){
                    if(distancesArray[j-1] > distancesArray[j]){
                        //swap elements
                        //sorting distancesArray
                        temp = distancesArray[j-1];
                        distancesArray[j-1] = distancesArray[j];
                        distancesArray[j] = temp;

                        //sorting storesArray
                        temp2 = storesArray[j-1];
                        storesArray[j-1] = storesArray[j];
                        storesArray[j] = temp2;
                    }
                }
            }

            Toast.makeText(this,"Smallest distance is:" +distancesArray[0] +"\nStore is: "+ storesArray[0], Toast.LENGTH_LONG).show();


            //
            //creating nodes for each order in Firebase database
            //
            myRef.push().setValue( "userID: "+ "ELznDdlK6wSZ3ArkDttTpONurRS2" + "  Latitude: " + x + "  Longitude: " + y + " DateAdded: "+ dateAdded + " ProductID: "+ "c1");
            myRef2.push().setValue("3");
            updateOrders= false;
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}