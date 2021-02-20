package com.unipi.p17019p17024.clickawayapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements LocationListener {
    String userID, email, username;

    TextView textViewEmptyCartTitle, textViewCartTitle;
    ImageButton imageButtonDeleteFromCart;
    Button buttonSubmitOrder, buttonEmptyCart;
    ImageView imageViewEmptyCart;

    LocationManager locationManager;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    //database Firebase
    private DatabaseReference database, cartRef, ordersRef, storesRef, userIDRef;

    boolean updateOrders = false;

    //RecyclerView
    private RecyclerView recyclerView;
    CartAdapter adapter; // Create Object of the Adapter class

    String productID, productID2;
    String quantity, quantity2;

    String[] storesArray;

    public List<String> productsList = new ArrayList<>();
    public List<Integer> quantitiesList = new ArrayList<>();

    boolean isOrderSubmittable = true;
    boolean dataIsNotChangedStores = true;

    private static final int REC_RESULT = 653;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        imageButtonDeleteFromCart = findViewById(R.id.imageButtonDeleteFromCart);

        textViewCartTitle = findViewById(R.id.textViewCartTitle);
        textViewEmptyCartTitle = findViewById(R.id.textViewEmptyCartTitle);
        imageViewEmptyCart = findViewById(R.id.imageViewEmptyCart);
        buttonSubmitOrder = findViewById(R.id.buttonSubmitOrder);
        buttonEmptyCart = findViewById(R.id.buttonEmptyCart);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userID = getIntent().getStringExtra("userID");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        //database Firebase
        database = FirebaseDatabase.getInstance().getReference();

        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userID);

        recyclerView = findViewById(R.id.cartRecyclerList);


        // To display the Recycler view linearly
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Cart> options
                = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef, Cart.class)
                .build();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        adapter = new CartAdapter(options);
        // Connecting Adapter class with the Recycler view
        recyclerView.setAdapter(adapter);


        //
        //If the user has items in cart: Visibility of buttons, textViews etc.
        //
        userIDRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userID);

        userIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mySnapshot) {
                if (mySnapshot.exists()){
                    textViewCartTitle.setVisibility(View.VISIBLE);
                    textViewEmptyCartTitle.setVisibility(View.INVISIBLE);
                    imageViewEmptyCart.setVisibility(View.INVISIBLE);
                    buttonEmptyCart.setVisibility(View.INVISIBLE);
                    buttonSubmitOrder.setVisibility(View.VISIBLE);
                }
                else {
                    textViewCartTitle.setVisibility(View.INVISIBLE);
                    textViewEmptyCartTitle.setVisibility(View.VISIBLE);
                    imageViewEmptyCart.setVisibility(View.VISIBLE);
                    buttonEmptyCart.setVisibility(View.VISIBLE);
                    buttonSubmitOrder.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void submitOrder(View view){
        //
        //check for gps permission
        //
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        beforeOrderMessage("Order confirmation", "Are you sure you want to submit your order?");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double x = location.getLatitude();
        double y = location.getLongitude();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateAdded = simpleDateFormat.format(new Date());


        if(updateOrders){
            //
            //calculating distances using HaversineDistance class
            //
            double distanceS = HaversineDistance.haversine(37.976351, 23.733375 ,x, y);
            double distanceIl = HaversineDistance.haversine(38.034954, 23.703296 , x, y);
            double distanceP = HaversineDistance.haversine(37.942260, 23.650997 ,x, y);
            double distanceG = HaversineDistance.haversine(37.877499, 23.755749 , x, y);
            double distanceAP = HaversineDistance.haversine(38.013034, 23.817960, x, y);

            double[] distancesArray = new double[] {distanceS, distanceIl, distanceP, distanceG, distanceAP};
            storesArray = new String[] {"Syntagma", "Ilion", "Piraeus", "Glyfada", "Agia Paraskevi"};

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

            //Toast.makeText(this,"Smallest distance is:" +distancesArray[0] +"\nStore is: "+ storesArray[0], Toast.LENGTH_LONG).show();



            //
            //creating nodes for each order in Firebase database
            //
            DatabaseReference newOrder = database.child("Orders").push();
            String keyChild = newOrder.getKey();

            database.child("Orders").child(keyChild).child("userID").setValue(userID);
            database.child("Orders").child(keyChild).child("Latitude").setValue(x);
            database.child("Orders").child(keyChild).child("Longitude").setValue(y);
            database.child("Orders").child(keyChild).child("Date added").setValue(dateAdded);
            database.child("Orders").child(keyChild).child("Store").setValue(storesArray[0]);


            //
            // accessing data from 'Cart' node in Firebase
            //
            cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userID);
            cartRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                            productID = dsp.child("id").getValue().toString();
                            quantity = dsp.child("quantity").getValue().toString();

                            database.child("Orders").child(keyChild).child("products").child(productID).child("quantity").setValue(quantity);

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // we are showing that error message in toast
                    Toast.makeText(ShoppingCartActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            //
            // updating products' availability
            //
            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
            ordersRef.child(keyChild).child("products").addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot1)
                {
                    if (dataSnapshot1.exists()) {
                        for (DataSnapshot dsp10 : dataSnapshot1.getChildren()) {

                            productID2 = dsp10.getKey();
                            quantity2 = dsp10.child("quantity").getValue().toString();

                            productsList.add(productID2);
                            quantitiesList.add(Integer.parseInt(quantity2));
                        }


                        storesRef = FirebaseDatabase.getInstance().getReference().child("Stores");
                        storesRef.child(storesArray[0]).child("products").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(dataIsNotChangedStores) {
                                    // checking if one or more ordered products are unavailable
                                    for (DataSnapshot dsp2 : snapshot.getChildren()) {
                                        if (productsList.contains(dsp2.getKey())) {
                                            int position = productsList.indexOf(dsp2.getKey());
                                            if (quantitiesList.get(position) > Integer.parseInt(dsp2.child("quantity").getValue().toString())) {
                                                isOrderSubmittable = false;
                                            }
                                        }
                                    }

                                    // all products (and their quantity) are available in the corresponding store
                                    if (isOrderSubmittable) {
                                        //displaying message to user
                                        afterOrderMessage("Order submitted successfully!", "Based on your current location, you can pick up your order from " + storesArray[0] + "'s store tomorrow (between 9.00 AM to 5:00 PM).");

                                        //updating database
                                        for (DataSnapshot dsp3 : snapshot.getChildren()) {
                                            if (productsList.contains(dsp3.getKey())) {
                                                //updating availability of each ordered product for the corresponding store in database
                                                int position = productsList.indexOf(dsp3.getKey());
                                                Integer bigNumber = Integer.parseInt(dsp3.child("quantity").getValue().toString());
                                                Integer smallNumber = quantitiesList.get(position);
                                                Integer remainder = bigNumber - smallNumber;
                                                storesRef.child(storesArray[0]).child("products").child(String.valueOf(dsp3.getKey())).child("quantity").setValue(remainder);

                                            }
                                        }
                                    } else {
                                        //displaying message to user
                                        afterOrderMessage("Order submitted successfully!", "Based on your current location, you will pick up your order from " + storesArray[0] + "'s store.\n" +
                                                "Some of the products you ordered are currently available. You will be notified via email, to pick them up, once they are available.");

                                        //updating database
                                        for (DataSnapshot dsp4 : snapshot.getChildren()) {
                                            if (productsList.contains(dsp4.getKey())) {
                                                int position = productsList.indexOf(dsp4.getKey());
                                                //updating availability of each ordered product for the corresponding store in database
                                                int sum = Integer.parseInt(dsp4.child("pending").getValue().toString()) + quantitiesList.get(position);
                                                storesRef.child(storesArray[0]).child("products").child(String.valueOf(dsp4.getKey())).child("pending").setValue(sum);
                                            }
                                        }
                                    }
                                    dataIsNotChangedStores = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    dataIsNotChangedStores = true;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // we are showing that error message in toast
                    Toast.makeText(ShoppingCartActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });


            //deleting all items from user's cart
            database.child("Cart").child(userID).removeValue();

            updateOrders = false;
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void beforeOrderMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_my_launcher)
                .setNegativeButton("No", (dialog, which) -> {
                    updateOrders = false;
                })
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateOrders = true;
                })
                .setNeutralButton("Tap to speak", (dialog, which) -> {
                    //speech recognition
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say 'Yes' or 'No'");
                    startActivityForResult(intent,REC_RESULT);
                })
                .show();
    }

    //order submitted successfully
    public void afterOrderMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_my_launcher)
                .setPositiveButton("Ok", (dialog, which) -> {
                    //do nothing
                })
                .show();
    }


    //speech recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.contains("yes") || matches.contains("Yes")) {
                updateOrders = true;
            }
            else if(matches.contains("no") || matches.contains("No")){
                updateOrders = false;
            }
            else{
                afterOrderMessage("Didn't catch that", "Please, try again!");
            }
        }
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }


    public void GoToProducts(View view){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("userID", currentUser.getUid());
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("username",currentUser.getDisplayName());
        startActivity(intent);
    }


}