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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    DatabaseReference myRef;

    boolean updateOrders = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        button1 = findViewById(R.id.buttonSubmitOrder);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Orders:");
        //textView1.setText(getIntent().getStringExtra("userID"));
    }

    public void submitOrder(View view){

        //check for gps permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);
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

        //
        //creating a node for each user in Firebase containing his/hers ID,location and timestamp
        //
        if(updateOrders){
            //myRef.push().setValue( "userID: "+ getIntent().getStringExtra("userID") + "  Latitude: " + String.valueOf(x) + "  Longitude: " + String.valueOf(y) +
            //        "  DateAdded: "+ String.valueOf(dateAdded) + "ProductID: "+  + "Quantity: "+);
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