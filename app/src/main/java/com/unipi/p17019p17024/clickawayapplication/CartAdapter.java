package com.unipi.p17019p17024.clickawayapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends FirebaseRecyclerAdapter<
        Cart, CartAdapter.CartViewholder> {

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private DatabaseReference userIDRef;

    public CartAdapter(
            @NonNull FirebaseRecyclerOptions<Cart> options)
    {
        super(options);
    }


    @Override
    protected void
    onBindViewHolder(@NonNull CartViewholder holder,
                     int position, @NonNull Cart model)
    {
        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userID = currentUser.getUid();



        userIDRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userID);

        String productIDs = getRef(position).getKey();

        userIDRef.child(productIDs).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    String pImage = snapshot.child("image").getValue().toString();

                    Picasso.get().load(pImage).placeholder(R.drawable.ic_product_image).into(holder.productImage);

                    String pName = snapshot.child("name").getValue().toString();
                    String pType = snapshot.child("type").getValue().toString();
                    String pQuantity = snapshot.child("quantity").getValue().toString();
                    String pTotalPrice = snapshot.child("total price").getValue().toString();

                    holder.productName.setText(pName);
                    holder.productType.setText(pType);
                    holder.productQuantity.setText(pQuantity);
                    holder.productPrice.setText(pTotalPrice);


                    //Delete from cart Button
                    holder.imageButtonDeleteFromCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userIDRef.child(productIDs).removeValue();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    @NonNull
    @Override
    public CartViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart, parent, false);
        return new CartAdapter.CartViewholder(view);
    }


    class CartViewholder extends RecyclerView.ViewHolder {
        TextView productName, productType, productQuantity, productPrice;/*, textViewEmptyCartTitle*/
        CircleImageView productImage;
        ImageButton imageButtonDeleteFromCart;
        //Button buttonSubmitOrder;
        //ImageView imageViewEmptyCart;

        public CartViewholder(@NonNull View itemView)
        {
            super(itemView);

            productName = itemView.findViewById(R.id.cart_name);
            productType = itemView.findViewById(R.id.cart_type);
            productQuantity = itemView.findViewById(R.id.cart_quantity);
            productPrice = itemView.findViewById(R.id.cart_price);
            productImage = itemView.findViewById(R.id.cart_profile_image);
            imageButtonDeleteFromCart = itemView.findViewById(R.id.imageButtonDeleteFromCart);

            //textViewEmptyCartTitle = itemView.findViewById(R.id.textViewEmptyCartTitle);
            //imageViewEmptyCart = itemView.findViewById(R.id.imageViewEmptyCart);
            //buttonSubmitOrder = itemView.findViewById(R.id.buttonSubmitOrder);
        }
    }

}
