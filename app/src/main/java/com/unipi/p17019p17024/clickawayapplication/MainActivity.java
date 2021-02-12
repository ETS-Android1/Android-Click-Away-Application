package com.unipi.p17019p17024.clickawayapplication;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements LocationListener {
    Button button1;

    //
    //Fixed userID
    //
    String userID = "ELznDdlK6wSZ3ArkDttTpONurRS2";


    LocationManager locationManager;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    //database Firebase
    FirebaseDatabase database;
    DatabaseReference myRef;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button1 = findViewById(R.id.shopping_cart);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        getIntent().getStringExtra("userID");
        getIntent().getStringExtra("email");
        getIntent().getStringExtra("username");


        //database Firebase
        database = FirebaseDatabase.getInstance();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //
        //show username and email on navigationView
        //
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.usernameTextView);
        TextView navEmail = headerView.findViewById(R.id.emailTextView);
        navUsername.setText(getIntent().getStringExtra("username"));
        navEmail.setText(getIntent().getStringExtra("email"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void shoppingCartOnClick(MenuItem menuItem){
        Intent intent = new Intent(getApplicationContext(), ShoppingCartActivity.class);
        //intent.putExtra("userID", currentUser.getUid());
        //intent.putExtra("email", currentUser.getEmail());
        //intent.putExtra("username",currentUser.getDisplayName());


        //
        //Fixed userID
        //
        intent.putExtra("userID", userID);

        startActivity(intent);
    }
    public void favoritesOnClick(MenuItem menuItem){
        Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}