package com.audioapplication.Fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.audioapplication.Adapter.HomePagerAdapter;
import com.audioapplication.R;

public class HomeFragment extends Fragment {
    Activity activity;
    View rootView;
    TabLayout tabs_layout;
    public static ViewPager viewpagerLender;
    AppBarLayout appBarLender;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tabs_layout=(TabLayout) rootView.findViewById(R.id.tabs_layout);
        viewpagerLender=(ViewPager) rootView.findViewById(R.id.viewpagerLender);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            tabs_layout.setElevation(24);
        }

        tabs_layout.addTab(tabs_layout.newTab().setText("Audio Record"));
        tabs_layout.addTab(tabs_layout.newTab().setText("Product Listings"));
        tabs_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs_layout.setTabGravity(TabLayout.GRAVITY_FILL);
        appBarLender=(AppBarLayout) rootView.findViewById(R.id.appBarLender);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        viewpagerLender.setAdapter(new HomePagerAdapter(getFragmentManager(), 2));
        viewpagerLender.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs_layout));
        tabs_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpagerLender.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
