package com.example.stuffhub.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stuffhub.Model.Cart;
import com.example.stuffhub.R;
import com.example.stuffhub.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity
{
    private RecyclerView producstList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;

    private  String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        userID = getIntent().getStringExtra("uid");

        producstList = findViewById(R.id.products_list);
        producstList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        producstList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
        .child("Admin View").child(userID).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions <Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart model)
                    {
                        holder.TxtProductQuantity.setText("Quantity = $"+model.getQuantity());
                        holder.TxtProductPrice.setText("Price = $"+model.getPrice()+ " Pesos");
                        holder.TxtProductName.setText("Name = $"+model.getPname());

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);

                        return holder;
                    }
                };
        producstList.setAdapter(adapter);
        adapter.startListening();
    }
}
