package com.example.stuffhub.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stuffhub.Admin.AdminMaintainProductsActivity;
import com.example.stuffhub.Model.Cart;
import com.example.stuffhub.Model.Products;
import com.example.stuffhub.Prevalent.Prevalent;
import com.example.stuffhub.R;
import com.example.stuffhub.ViewHolder.ProductViewHolder;
import com.example.stuffhub.ui.categories.CategoriesFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.example.stuffhub.R.id.drawer_layout;
import static com.example.stuffhub.R.id.nav_host_fragment;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "dummy";
    private AppBarConfiguration mAppBarConfiguration;

    private DatabaseReference ProductsRef, databaseReference;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            Intent intent= getIntent();
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                type= getIntent().getExtras().get("Admin").toString();
            }

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

            Paper.init(this);

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Home");
            setSupportActionBar(toolbar);


            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(!type.equals("Admin"))
                    {
                        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                        startActivity(intent);
                    }

                }

            });

            final DrawerLayout drawer = findViewById(drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            toggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                    R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);


            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            drawer.addDrawerListener(toggle);


            View headerView = navigationView.getHeaderView(0);
            TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
            CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);


            if(!type.equals("Admin"))
            {
                userNameTextView.setText(Prevalent.currentOnlineUser.getName());
                Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
            }


            recyclerView = findViewById(R.id.recycler_menu);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        } catch (Exception e)
        {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);

        return v;
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("productState").equalTo("Approved"), Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }

    @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

     //  if (id == R.id.action_settings)
    //  {
    //      return true;
    //  }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
      ;
      Intent intent;
        switch(item.getItemId())
        {

            case R.id.nav_cart:
                if(!type.equals("Admin"))
                {
                    intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }


                break;
            case R.id.nav_search:
                if(!type.equals("Admin"))
                {
                    intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
                    startActivity(intent);
                }

                break;
//            case R.id.nav_categories:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
//                break;
            case R.id.nav_settings:
                if(!type.equals("Admin"))
                {
                    intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.nav_logout:
                if(!type.equals("Admin")) {
                    Paper.book().destroy();

                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
 // Inflate the menu; this adds items to the action bar if it is present.
 getMenuInflater().inflate(R.menu.home, menu);
 return true;
 }

 @Override
 public boolean onSupportNavigateUp() {
 NavController navController = Navigation.findNavController(this, nav_host_fragment);
 return NavigationUI.navigateUp(navController, mAppBarConfiguration)
 || super.onSupportNavigateUp();
 }
}