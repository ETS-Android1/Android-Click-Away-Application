package com.unipi.p17019p17024.clickawayapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FavoritesActivity extends AppCompatActivity {

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        userID = getIntent().getExtras().get("userID").toString();
    }
}