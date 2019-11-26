package com.example.stuffhub.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stuffhub.Interface.ItemClickListener;
import com.example.stuffhub.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView TxtProductName, TxtProductPrice, TxtProductQuantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder (View itemView)
    {
        super(itemView);

        TxtProductName = itemView.findViewById(R.id.cart_product_name);
        TxtProductPrice = itemView.findViewById(R.id.cart_product_price);
        TxtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
    }
    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
