package com.example.stuffhub.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.stuffhub.Admin.AdminCheckNewProductsActivity;
import com.example.stuffhub.Buyer.MainActivity;
import com.example.stuffhub.Model.Products;
import com.example.stuffhub.R;

import com.example.stuffhub.ViewHolder.ProductViewHolder;
import com.example.stuffhub.ViewHolder.itemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {

                        case R.id.navigation_home:
                            Intent home = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);

                            startActivity(home);

                            return true;

                        case R.id.navigation_add:
                            Intent intentCate = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);

                            startActivity(intentCate);
                            Log.d("dummy", "ADD ===");
                            return true;

                        case R.id.navigation_logout:
                            final FirebaseAuth mAuth;
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();

                            Intent intentMain = new Intent(SellerHomeActivity.this, MainActivity.class);
                            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentMain);
                            finish();
                            Log.d("dummy", "Notification Reselected ===");
                            return true;

                    }
                    return false;
                }


            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
       BottomNavigationView navView = findViewById(R.id.nav_view);

       navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager( this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedProductsRef.orderByChild("sid")
                                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class).build();
        FirebaseRecyclerAdapter<Products, itemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, itemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull itemViewHolder holder, int i, @NonNull Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductStatus.setText("State: "+model.getProductState());
                        holder.txtProductPrice.setText("Price = $" + model.getPrice() + " Pesos");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                final String productID = model.getPid();
                                CharSequence options [] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"

                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                                builder.setTitle("Do you want to delete this Product. Are you sure?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        if(position==0) {
                                            deleteProduct(productID);
                                        }
                                        if (position==1)
                                        {

                                        }
                                    }

                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view, parent, false);
                        itemViewHolder holder = new itemViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productID)
    {
        unverifiedProductsRef.child(productID).child("productState")
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(SellerHomeActivity.this, "That item has been Deleted Successfully. ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
