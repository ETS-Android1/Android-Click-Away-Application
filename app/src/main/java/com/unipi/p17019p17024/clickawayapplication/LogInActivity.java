package com.unipi.p17019p17024.clickawayapplication;

import androidx.appcompat.app.AppCompatActivity;

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
    EditText editTextEmail, editTextPassword, editTextTextUsername;
    Button button1,button2,button3,button4;
    TextView textView1,textView2,textView3;
    CheckBox checkBox, checkBox2;

    //Shared Preferences
    SharedPreferences preferences;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;


    boolean isSignInPushed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextTextUsername = findViewById(R.id.editTextUsername);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        textView1 = findViewById(R.id.loginTitle1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.loginTitle2);
        checkBox = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);

        //
        //Shared Preferences
        //
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Email
        editTextEmail.setText("");
        //Password
        editTextPassword.setText("");

        checkBox.setChecked(false);



        //User Authentication
        mAuth = FirebaseAuth.getInstance();


        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);

    }

    public void signUp(View view) {
        if (editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() || editTextTextUsername.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.errorToast2), Toast.LENGTH_SHORT).show();
        }
        else {
                mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                currentUser = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.signUpToast), Toast.LENGTH_LONG).show();
                                createUsername(editTextTextUsername.getText().toString(), currentUser);

                                isSignInPushed = false;
                                writeSP(isSignInPushed); //writeSP(view, isSignInPushed);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("userID", currentUser.getUid());
                                intent.putExtra("email", currentUser.getEmail());
                                intent.putExtra("username",currentUser.getDisplayName());
                                //Toast.makeText(this,currentUser.getDisplayName(),Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

        }
    }

    public void transitionClick(View view){
        editTextTextUsername.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView1.setVisibility(View.INVISIBLE);
        checkBox.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.VISIBLE);
        checkBox2.setVisibility(View.VISIBLE);
        String str_email = preferences.getString("myKeyEmail", "");
        editTextEmail.setText(str_email);
        String str_password = preferences.getString("myKeyPassword", "");
        editTextPassword.setText(str_password);
        if(str_email == "" && str_password == ""){
            checkBox2.setChecked(false);
        }
        else {
            checkBox2.setChecked(true);
        }

    }

    public void goBack(View view){
        editTextTextUsername.setVisibility(View.VISIBLE);
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        checkBox2.setVisibility(View.INVISIBLE);
        editTextEmail.setText("");
        editTextPassword.setText("");
        checkBox.setChecked(false);
    }


    public void signIn(View view) {
            mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginToast), Toast.LENGTH_LONG).show();

                            isSignInPushed = true;
                            writeSP(isSignInPushed);

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
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(),getResources().getString(R.string.userCratedToast),Toast.LENGTH_LONG).show());
    }


    public void writeSP(Boolean isSignInPushed) {
        SharedPreferences.Editor editor = preferences.edit();
        if (isSignInPushed){
            if(checkBox2.isChecked()) {
                //Email
                editor.putString("myKeyEmail", editTextEmail.getText().toString());
                editor.apply();
                //Password
                editor.putString("myKeyPassword", editTextPassword.getText().toString());
                editor.apply();
            }
            else {
                //Email
                editor.putString("myKeyEmail", "");
                editor.apply();
                //Password
                editor.putString("myKeyPassword", "");
                editor.apply();
            }
        }
        else
        {
            if(checkBox.isChecked()) {
                //Email
                editor.putString("myKeyEmail", editTextEmail.getText().toString());
                editor.apply();
                //Password
                editor.putString("myKeyPassword", editTextPassword.getText().toString());
                editor.apply();
            }
            else {
                //Email
                editor.putString("myKeyEmail", "");
                editor.apply();
                //Password
                editor.putString("myKeyPassword", "");
                editor.apply();
            }
        }
    }
}