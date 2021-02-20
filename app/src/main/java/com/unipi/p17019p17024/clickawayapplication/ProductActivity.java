package com.unipi.p17019p17024.clickawayapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {
    private String productID, productName, productType, productPrice, productImage;
    Integer quantity, count_minus, count_plus;
    Double totalPrice;

    //Πώς το ξέρω ότι είναι false αρχικά;;;
    //Μήπως πρέπει κάθε φορά να ελέγχω αν υπάρχει το productID στο Favourites της Firebase;
    Boolean isAddedToFavourites;

    //
    //textView
    //
    TextView textViewProductName, textViewProductType, textViewQuantity, textViewTotalPrice;
    //
    //imageView
    //
    ImageView imageViewProductImage;
    //
    //imageButton
    //
    ImageButton imageButtonFavourites, imageButtonMinus, imageButtonPlus;
    //
    //button
    //
    Button buttonAddToCart;

    //Database Firebase
    DatabaseReference database;

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userID = currentUser.getUid();

        //Quantity
        quantity = 1;
        count_minus = 0;
        count_plus = 0;

        //
        //textView
        //
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductType = findViewById(R.id.textViewProductType);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        //
        //imageView
        //
        imageViewProductImage = findViewById(R.id.imageViewProductImage);
        //
        //imageButton
        //
        imageButtonFavourites = findViewById(R.id.imageButtonFavourites);
        imageButtonMinus = findViewById(R.id.imageButtonMinus);
        imageButtonPlus = findViewById(R.id.imageButtonPlus);
        //
        //button
        //
        buttonAddToCart = findViewById(R.id.buttonAddToCart);


        productID = getIntent().getExtras().get("product_id").toString();
        productName = getIntent().getExtras().get("product_name").toString();
        productType = getIntent().getExtras().get("product_type").toString();
        productPrice = getIntent().getExtras().get("product_price").toString();
        productImage = getIntent().getExtras().get("product_image").toString();

        Picasso.get().load(productImage).placeholder(R.drawable.ic_product_image).into(imageViewProductImage);

        //Name
        textViewProductName.setText(productName);
        //Type
        textViewProductType.setText(productType);
        //Price
        totalPrice = Double.parseDouble(productPrice);
        totalPrice = round(totalPrice, 2);
        textViewTotalPrice.setText(totalPrice.toString());

        //Database Firebase
        database = FirebaseDatabase.getInstance().getReference();


        //
        //Check if product is on Favourites in Firebase
        //

    }


    public void addToCartClick(View v) {

        database.child("Cart").child(userID).child(productID).child("id").setValue(productID);
        database.child("Cart").child(userID).child(productID).child("name").setValue(productName);
        database.child("Cart").child(userID).child(productID).child("type").setValue(productType);
        database.child("Cart").child(userID).child(productID).child("image").setValue(productImage);
        database.child("Cart").child(userID).child(productID).child("quantity").setValue(quantity);
        database.child("Cart").child(userID).child(productID).child("total price").setValue(totalPrice);

        Toast.makeText(this,"Product added to cart!",Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userID", currentUser.getUid());
        intent.putExtra("email", currentUser.getEmail());
        intent.putExtra("username",currentUser.getDisplayName());
        startActivity(intent);
    }

    public void favouritesClick(View v) {

        database.child("Cart").child(userID).child(productID).child("id").setValue(productID);
        database.child("Cart").child(userID).child(productID).child("name").setValue(productName);
        database.child("Cart").child(userID).child(productID).child("price").setValue(productPrice);
        database.child("Cart").child(userID).child(productID).child("type").setValue(productType);
        database.child("Cart").child(userID).child(productID).child("image").setValue(productImage);
    }

    public void minusClick(View v) {
        if(quantity>1) {
            //Quantity
            quantity--;
            textViewQuantity.setText(quantity.toString());

            //Price
            totalPrice = Double.parseDouble(productPrice) * quantity;
            totalPrice = round(totalPrice, 2);
            textViewTotalPrice.setText(totalPrice.toString());

            count_minus = 0;
        }
        else {
            count_minus++;
            if (count_minus >= 3){
                Toast.makeText(this,"Quantity must be bigger than zero!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void plusClick(View v) {
        if(quantity<100) {
            //Quantity
            quantity++;
            textViewQuantity.setText(quantity.toString());

            //Price
            totalPrice = Double.parseDouble(productPrice) * quantity;
            totalPrice = round(totalPrice, 2);
            textViewTotalPrice.setText(totalPrice.toString());

            count_plus = 0;
        }
        else {
            count_plus++;
            if (count_plus >= 3){
                Toast.makeText(this,"Quantity must be less than a hundred!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}