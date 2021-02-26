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

public class FavoritesAdapter extends FirebaseRecyclerAdapter<
        Favorites, FavoritesAdapter.FavoritesViewholder> {

    //User Authentication
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private DatabaseReference userIDRef;

    public FavoritesAdapter(
            @NonNull FirebaseRecyclerOptions<Favorites> options)
    {
        super(options);
    }


    @Override
    protected void
    onBindViewHolder(@NonNull FavoritesAdapter.FavoritesViewholder holder,
                     int position, @NonNull Favorites model)
    {
        //User Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userID = currentUser.getUid();



        userIDRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userID);

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

                    holder.productName.setText(pName);
                    holder.productType.setText(pType);


                    //Delete from favorites Button
                    holder.imageButtonDeleteFromFavorites.setOnClickListener(new View.OnClickListener() {
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
    public FavoritesViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorites, parent, false);
        return new FavoritesAdapter.FavoritesViewholder(view);
    }


    class FavoritesViewholder extends RecyclerView.ViewHolder {
        TextView productName, productType;
        CircleImageView productImage;
        ImageButton imageButtonDeleteFromFavorites;

        public FavoritesViewholder(@NonNull View itemView)
        {
            super(itemView);

            productName = itemView.findViewById(R.id.favorites_name);
            productType = itemView.findViewById(R.id.favorites_type);
            productImage = itemView.findViewById(R.id.favorites_profile_image);
            imageButtonDeleteFromFavorites = itemView.findViewById(R.id.imageButtonDeleteFromFavorites);

        }
    }

}
