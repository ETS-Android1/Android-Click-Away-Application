package com.unipi.p17019p17024.clickawayapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    Button button1;

    String userID, email, username;


    LocationManager locationManager;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    //database Firebase
    DatabaseReference database;

    // Firebase Cloud Storage
    StorageReference userProfileImageRef;

    private AppBarConfiguration mAppBarConfiguration;

    //Location
    //private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private static final int GalleryPick = 1;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button1 = findViewById(R.id.shopping_cart);
        loadingBar = new ProgressDialog(this);

        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userID = getIntent().getStringExtra("userID");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");


        //database Firebase
        database = FirebaseDatabase.getInstance().getReference();

        // Firebase Cloud Storage
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        getUserProfileImage();


        //
        //Background Location
        /*
        findViewById(R.id.buttonStopLocationUpdates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });
        */


        //
        // Navigation Drawer
        //
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

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.usernameTextView);
        TextView navEmail = headerView.findViewById(R.id.emailTextView);
        ImageButton imageButton = (ImageButton)headerView.findViewById(R.id.imageButton);
        navUsername.setText(getIntent().getStringExtra("username"));
        navEmail.setText(getIntent().getStringExtra("email"));

        imageButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GalleryPick);
        });
    }

    //
    //
    // User Profile Image
    //
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            Uri ImageUri = data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = userProfileImageRef.child(userID + ".jpg");
                filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                    showMessage("Hurray!","Profile image uploaded successfully");
                    final String downloadUrl = uri.toString();

                    database.child("Users").child(userID).child("image")
                            .setValue(downloadUrl)
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Image saved", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    String message = Objects.requireNonNull(task1.getException()).toString();
                                    showMessage("Profile image didn't save!","Error: "+ message);
                                }
                                loadingBar.dismiss();
                            });
                }).addOnFailureListener(e -> showMessage("Profile image didn't update!","Error"));
            }
        }
    }

    public void getUserProfileImage(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageButton imageButton = (ImageButton)headerView.findViewById(R.id.imageButton);

        database.child("Users").child(userID)
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                        {
                            String link = dataSnapshot.child("image").getValue().toString();

                            // loading that data into imageButton
                            Picasso.get().load(link).into(imageButton);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // we are showing that error message in toast
                        Toast.makeText(MainActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //
    //Background Location
    //
    /*
    public boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }
            else{
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //
    //
    //
    */

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
        intent.putExtra("userID", userID);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void favoritesOnClick(MenuItem menuItem){
        Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_my_launcher)
                .show();
    }

    public void signOut(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);
    }

}