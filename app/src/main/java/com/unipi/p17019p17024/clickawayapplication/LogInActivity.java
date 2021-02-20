package com.unipi.p17019p17024.clickawayapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LogInActivity extends AppCompatActivity {
    EditText editText1,editText2,editText3;
    Button button1,button2,button3,button4;
    TextView textView1,textView2;
    CheckBox checkBox;

    SharedPreferences sharedPreferences;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;


    boolean isSignedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editText1 = findViewById(R.id.editTextTextEmail);
        editText2 = findViewById(R.id.editTextTextPassword);
        editText3 = findViewById(R.id.editTextTextUsername);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        checkBox = findViewById(R.id.checkBox);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Shared Preferences


        //User Authentication
        mAuth = FirebaseAuth.getInstance();


        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);

    }

    public void signUp(View view) {
        if (editText1.getText().toString().isEmpty() || editText2.getText().toString().isEmpty() || editText3.getText().toString().isEmpty()) {
            Toast.makeText(this, "One or more fields are empty!", Toast.LENGTH_SHORT).show();
        }
        else {
                mAuth.createUserWithEmailAndPassword(editText1.getText().toString(), editText2.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                currentUser = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Sign-up successful!", Toast.LENGTH_LONG).show();
                                createUsername(editText3.getText().toString(), currentUser);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("userID", currentUser.getUid());
                                intent.putExtra("email", currentUser.getEmail());
                                intent.putExtra("username",currentUser.getDisplayName());


                                if(checkBox.isChecked()) {
                                    //to-do stuff (shared preferences)
                                }
                                else{

                                }


                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

        }
    }

    public void transitionClick(View view){
        editText1.setHint("Email");
        editText2.setHint("Password");
        editText3.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView1.setText("Enter your credentials");
        checkBox.setVisibility(View.INVISIBLE);

    }

    public void goBack(View view){
        editText1.setHint("Enter email");
        editText2.setHint("Enter password");
        editText3.setVisibility(View.VISIBLE);
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView1.setText("   Create an account");
        checkBox.setVisibility(View.VISIBLE);
    }


    public void signIn(View view) {
            mAuth.signInWithEmailAndPassword(editText1.getText().toString(), editText2.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Log-in successful!", Toast.LENGTH_LONG).show();
                            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                            intent2.putExtra("userID", currentUser.getUid());
                            intent2.putExtra("email", currentUser.getEmail());
                            intent2.putExtra("username",currentUser.getDisplayName());
                            startActivity(intent2);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
    }


    private void createUsername(String username, FirebaseUser user){
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(),"User created",Toast.LENGTH_LONG).show());
    }
}