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
 * Use the {@link AccessoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccessoriesFragment extends Fragment {

    private View accessoriesView;
    private RecyclerView myAccessoriesRecyclerViewList;
    private DatabaseReference accessoriesRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccessoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccessoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccessoriesFragment newInstance(String param1, String param2) {
        AccessoriesFragment fragment = new AccessoriesFragment();
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
        accessoriesView = inflater.inflate(R.layout.fragment_accessories, container, false);

        myAccessoriesRecyclerViewList = (RecyclerView) accessoriesView.findViewById(R.id.accessoriesRecyclerList);
        myAccessoriesRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));

        accessoriesRef = FirebaseDatabase.getInstance().getReference().child("Products").child("Accessories");

        return accessoriesView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(accessoriesRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, AccessoriesFragment.ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Product, AccessoriesFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AccessoriesFragment.ProductViewHolder holder, int position, @NonNull Product model)
            {
                String accessoriesIDs = getRef(position).getKey();

                accessoriesRef.child(accessoriesIDs).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")) {
                            String aImage = snapshot.child("image").getValue().toString();

                            String aName = snapshot.child("name").getValue().toString();
                            String aType = snapshot.child("type").getValue().toString();
                            String aPrice = snapshot.child("price").getValue().toString();

                            //Picasso.get().load(aImage).placeholder(R.drawable.ic_product_image).into(holder.accessoriesImage);
                            Picasso.with(getActivity()).load(aImage).placeholder(R.drawable.ic_product_image).into(holder.accessoriesImage);

                            holder.accessoriesName.setText(aName);
                            holder.accessoriesType.setText(aType);
                            holder.accessoriesPrice.setText(aPrice);
                        }
                        else {
                            String aName = snapshot.child("name").getValue().toString();
                            String aType = snapshot.child("type").getValue().toString();
                            String aPrice = snapshot.child("price").getValue().toString();

                            holder.accessoriesName.setText(aName);
                            holder.accessoriesType.setText(aType);
                            holder.accessoriesPrice.setText(aPrice);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public AccessoriesFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessories_display_layout, parent, false);
                AccessoriesFragment.ProductViewHolder viewHolder = new AccessoriesFragment.ProductViewHolder(view);
                return  viewHolder;
            }
        };

        myAccessoriesRecyclerViewList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView accessoriesName, accessoriesType, accessoriesPrice;
        CircleImageView accessoriesImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            accessoriesName = itemView.findViewById(R.id.accessories_name);
            accessoriesType = itemView.findViewById(R.id.accessories_type);
            accessoriesPrice = itemView.findViewById(R.id.accessories_price);
            accessoriesImage = itemView.findViewById(R.id.accessories_profile_image);
        }

    }

}