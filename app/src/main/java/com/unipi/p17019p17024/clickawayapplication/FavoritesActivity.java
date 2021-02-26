package com.unipi.p17019p17024.clickawayapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class FavoritesActivity extends AppCompatActivity {

    String userID, email, username;

    TextView textViewFavoritesTitle, textView4;
    ImageButton imageButtonDeleteFromFavorites;
    ImageView imageView2;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    //database Firebase
    private DatabaseReference database, favoritesRef, userIDRef;

    //RecyclerView
    private RecyclerView recyclerView;
    FavoritesAdapter adapter; // Create Object of the Adapter class


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        //imageButtonDeleteFromFavorites = findViewById(R.id.imageButtonDeleteFromFavorites);
        textViewFavoritesTitle = findViewById(R.id.textViewFavoritesTitle);
        textView4 = findViewById(R.id.textView4);
        imageView2 = findViewById(R.id.imageView2);


        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userID = getIntent().getStringExtra("userID");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        //database Firebase
        database = FirebaseDatabase.getInstance().getReference();

        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userID);

        recyclerView = findViewById(R.id.favoritesRecyclerList);


        // To display the Recycler view linearly
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Favorites> options
                = new FirebaseRecyclerOptions.Builder<Favorites>()
                .setQuery(favoritesRef, Favorites.class)
                .build();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        adapter = new FavoritesAdapter(options);
        // Connecting Adapter class with the Recycler view
        recyclerView.setAdapter(adapter);


        //
        //If the user has items in favorites: Visibility of buttons, textViews etc.
        //
        userIDRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userID);

        userIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mySnapshot) {
                if (mySnapshot.exists()){
                    textViewFavoritesTitle.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.INVISIBLE);
                    imageView2.setVisibility(View.INVISIBLE);
                }
                else {
                    textViewFavoritesTitle.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    imageView2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    public void GoToHome(View view){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("userID", currentUser.getUid());
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("username",currentUser.getDisplayName());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("userID", currentUser.getUid());
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("username",currentUser.getDisplayName());
        startActivity(intent);
    }


}