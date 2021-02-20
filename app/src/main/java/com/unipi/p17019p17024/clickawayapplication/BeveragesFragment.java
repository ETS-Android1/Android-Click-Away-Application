package com.unipi.p17019p17024.clickawayapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BeveragesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BeveragesFragment extends Fragment {

    private View beveragesView;
    private RecyclerView myBeveragesRecyclerViewList;
    private DatabaseReference beveragesRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BeveragesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BeveragesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BeveragesFragment newInstance(String param1, String param2) {
        BeveragesFragment fragment = new BeveragesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        beveragesView = inflater.inflate(R.layout.fragment_beverages, container, false);

        myBeveragesRecyclerViewList = (RecyclerView) beveragesView.findViewById(R.id.beveragesRecyclerList);
        myBeveragesRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));

        beveragesRef = FirebaseDatabase.getInstance().getReference().child("Products").child("Beverages");

        return beveragesView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(beveragesRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, BeveragesFragment.ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Product, BeveragesFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BeveragesFragment.ProductViewHolder holder, int position, @NonNull Product model)
            {
                String beveragesIDs = getRef(position).getKey();

                beveragesRef.child(beveragesIDs).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //if (snapshot.hasChild("image")) {
                                String bImage = snapshot.child("image").getValue().toString();

                                Picasso.get().load(bImage).placeholder(R.drawable.ic_product_image).into(holder.beveragesImage);
                                //Picasso.with(getActivity()).load(bImage).placeholder(R.drawable.ic_product_image).into(holder.beveragesImage);
                            //}

                            String bName = snapshot.child("name").getValue().toString();
                            String bType = snapshot.child("type").getValue().toString();
                            String bPrice = snapshot.child("price").getValue().toString();

                            holder.beveragesName.setText(bName);
                            holder.beveragesType.setText(bType);
                            holder.beveragesPrice.setText(bPrice);

                            //
                            //On Click
                            //
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent productIntent = new Intent(getContext(), ProductActivity.class);

                                    productIntent.putExtra("product_id", beveragesIDs);
                                    productIntent.putExtra("product_name", bName);
                                    productIntent.putExtra("product_type", bType);
                                    productIntent.putExtra("product_price", bPrice);
                                    productIntent.putExtra("product_image", bImage);

                                    startActivity(productIntent);
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
            public BeveragesFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beverages_display_layout, parent, false);
                BeveragesFragment.ProductViewHolder viewHolder = new BeveragesFragment.ProductViewHolder(view);
                return  viewHolder;
            }
        };

        myBeveragesRecyclerViewList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView beveragesName, beveragesType, beveragesPrice;
        CircleImageView beveragesImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            beveragesName = itemView.findViewById(R.id.beverages_name);
            beveragesType = itemView.findViewById(R.id.beverages_type);
            beveragesPrice = itemView.findViewById(R.id.beverages_price);
            beveragesImage = itemView.findViewById(R.id.beverages_profile_image);
        }

    }

}