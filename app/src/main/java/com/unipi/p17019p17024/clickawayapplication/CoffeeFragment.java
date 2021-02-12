package com.unipi.p17019p17024.clickawayapplication;

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
 * Use the {@link CoffeeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoffeeFragment extends Fragment {

    private View coffeeView;
    private RecyclerView myCoffeeRecyclerViewList;
    private DatabaseReference coffeeRef;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CoffeeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CoffeeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoffeeFragment newInstance(String param1, String param2) {
        CoffeeFragment fragment = new CoffeeFragment();
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
        coffeeView = inflater.inflate(R.layout.fragment_coffee, container, false);

        myCoffeeRecyclerViewList = (RecyclerView) coffeeView.findViewById(R.id.coffeeRecyclerList);
        myCoffeeRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));

        coffeeRef = FirebaseDatabase.getInstance().getReference().child("Products").child("Coffee");

        return coffeeView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(coffeeRef, Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model)
            {
                String coffeeIDs = getRef(position).getKey();

                coffeeRef.child(coffeeIDs).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")) {
                            String cImage = snapshot.child("image").getValue().toString();

                            String cName = snapshot.child("name").getValue().toString();
                            String cType = snapshot.child("type").getValue().toString();
                            String cPrice = snapshot.child("price").getValue().toString();

                            //Picasso.get().load(cImage).placeholder(R.drawable.ic_product_image).into(holder.coffeeImage);
                            Picasso.with(getActivity()).load(cImage).placeholder(R.drawable.ic_product_image).into(holder.coffeeImage);

                            holder.coffeeName.setText(cName);
                            holder.coffeeType.setText(cType);
                            holder.coffeePrice.setText(cPrice);
                        }
                        else {
                            String cName = snapshot.child("name").getValue().toString();
                            String cType = snapshot.child("type").getValue().toString();
                            String cPrice = snapshot.child("price").getValue().toString();

                            holder.coffeeName.setText(cName);
                            holder.coffeeType.setText(cType);
                            holder.coffeePrice.setText(cPrice);
                        }
                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coffee_display_layout, parent, false);
                ProductViewHolder viewHolder = new ProductViewHolder(view);
                return  viewHolder;
            }
        };

        myCoffeeRecyclerViewList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView coffeeName, coffeeType, coffeePrice;
        CircleImageView coffeeImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            coffeeName = itemView.findViewById(R.id.coffee_name);
            coffeeType = itemView.findViewById(R.id.coffee_type);
            coffeePrice = itemView.findViewById(R.id.coffee_price);
            coffeeImage = itemView.findViewById(R.id.coffee_profile_image);
        }

    }

}