package com.audioapplication.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audioapplication.Models.Product;
import com.audioapplication.R;
import com.bumptech.glide.Glide;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    List<Product> productList;
    Context context;
    Activity activity;
    LayoutInflater mInflater;
    boolean isProductViewAsList=true;
    public ProductAdapter(List<Product> productList, Activity activity) {
        this.productList = productList;
        this.activity = activity;
        mInflater = ((LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    public ProductAdapter(){}

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView;
        if (isProductViewAsList) {
            itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.single_product_view, parent, false);
        }else {
            itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.single_product_grid_view, parent, false);
        }
         return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ProductAdapter.ViewHolder holder, int position) {
        final Product product=productList.get(position);
        holder.product_name.setText(product.product_name);
        holder.coupon_desc.setText(product.discount_info);
        holder.coupon.setText(product.coupon_code);
        holder.number_of_people_used.setText(product.used_today);
        holder.added_time.setText(product.added_time);

        String imgLink = product.product_image;
        if (imgLink.length() > 0) {
            Glide.with(activity).load(imgLink).into(holder.product_image);
        }

        holder.coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("coupon_text", product.coupon_code);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(activity.getApplicationContext(), "Coupon Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProductView(boolean product_view_as_list){
        isProductViewAsList  = product_view_as_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


     class ViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image;
        TextView product_name;
        TextView coupon_desc;
        TextView number_of_people_used;
        TextView coupon;
        TextView added_time;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            product_image = (ImageView) itemLayoutView.findViewById(R.id.product_image);
            product_name = (TextView) itemLayoutView.findViewById(R.id.product_name);
            coupon_desc = (TextView) itemLayoutView.findViewById(R.id.coupon_desc);
            number_of_people_used=(TextView) itemLayoutView.findViewById(R.id.used_by);
            coupon = (TextView) itemLayoutView.findViewById(R.id.coupon);
            added_time =(TextView) itemLayoutView.findViewById(R.id.added_time);
         }
    }
}