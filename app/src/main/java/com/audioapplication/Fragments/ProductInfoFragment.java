package com.audioapplication.Fragments;

import android.app.Activity;
import android.net.Network;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.audioapplication.Adapter.ProductAdapter;
import com.audioapplication.AudioApplication;
import com.audioapplication.MainActivity;
import com.audioapplication.Models.Product;
import com.audioapplication.Networker.Networker;
import com.audioapplication.R;
import com.audioapplication.Utils.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductInfoFragment extends Fragment implements View.OnClickListener{
    Activity activity;
    String TAG = "ProductFragment";
    View rootView;
    RecyclerView product_container;
    ProductAdapter productAdapter;
    Button grid_view_btn;
    boolean isViewWithListCatalog = false;
    public static boolean isFragmentVisible;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_product_info, container, false);
        grid_view_btn = (Button) rootView.findViewById(R.id.grid_view_btn);
        grid_view_btn.setOnClickListener(this);
        MainActivity.opened_fragment=1;
        product_container = (RecyclerView) rootView.findViewById(R.id.product_container);
        product_container.setHasFixedSize(true);
        setRecyclerView();
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            isFragmentVisible = true;
        }else {
            isFragmentVisible = false;
        }
    }

    private void setRecyclerView() {
        if (!isViewWithListCatalog){
            grid_view_btn.setBackground(ContextCompat.getDrawable(activity,R.drawable.ic_list));
            product_container.setLayoutManager(new GridLayoutManager(getContext(), 2));
            productAdapter=new ProductAdapter(AudioApplication.data.product_list,activity);
            productAdapter.setProductView(false);
            product_container.setAdapter(productAdapter);
        }else{
            grid_view_btn.setBackground(ContextCompat.getDrawable(activity,R.drawable.ic_grid));
            product_container.setLayoutManager(new LinearLayoutManager(activity));
            productAdapter=new ProductAdapter(AudioApplication.data.product_list,activity);
            productAdapter.setProductView(true);
            product_container.setAdapter(productAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.grid_view_btn:
                if (isViewWithListCatalog){
                    isViewWithListCatalog = false;
                }else {
                    isViewWithListCatalog = true;
                }
                setRecyclerView();
                break;
        }
    }
}
