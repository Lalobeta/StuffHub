package com.example.stuffhub.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stuffhub.Interface.itemClickListener;
import com.example.stuffhub.R;

public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice,txtProductStatus;
    public ImageView imageView;
    public itemClickListener listener;

    public itemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView)itemView.findViewById(R.id.product_seller_image);
        txtProductName = (TextView)itemView.findViewById(R.id.seller_product_name);
        txtProductDescription= (TextView)itemView.findViewById(R.id.product_seller_description);
        txtProductPrice = (TextView)itemView.findViewById(R.id.product_seller_price);
        txtProductStatus = (TextView)itemView.findViewById(R.id.seller_product_state);

    }

    public  void setItemClickListener(itemClickListener listener)
    {
        this.listener = listener;
    }
    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(),false);
    }
}

