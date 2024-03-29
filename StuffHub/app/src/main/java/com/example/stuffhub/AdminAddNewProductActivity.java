package com.example.stuffhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

import static android.os.Build.VERSION_CODES.N;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private  String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick=1 ;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private StorageReference ProductImagesref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesref = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText)findViewById(R.id.product_name);
        InputProductDescription = (EditText)findViewById(R.id.product_description);
        InputProductPrice = (EditText)findViewById(R.id.product_price);
        loadingBar= new ProgressDialog(this);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });


    }



    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent ,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            ImageUri= data.getData();
            InputProductImage.setImageURI(ImageUri);
    }

    }

    private void ValidateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty((Description)))
        {
            Toast.makeText(this,"Please write product description...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty((Price)))
        {
            Toast.makeText(this,"Please write product price...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty((Pname)))
        {
            Toast.makeText(this,"Please write product name...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }

    }




    private void StoreProductInformation() {

        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate= null;
        if (android.os.Build.VERSION.SDK_INT >= N) {
            currentDate = new SimpleDateFormat("MM DD YYYY");

        saveCurrentDate= currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentTime.format(calendar.getTime());

        productRandomKey= saveCurrentDate + saveCurrentTime;
    }
        final StorageReference filePath = ProductImagesref.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProductActivity.this, "Product image uploaded successful", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();

                       }
                       downloadImageUrl = filePath.getDownloadUrl().toString();
                       return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())

                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"Getting product image Url Sccessfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();


                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap <String, Object> productMap= new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error " + message , Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
