package com.audioapplication.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.audioapplication.Fragments.AudioFragment;
import com.audioapplication.Fragments.ProductInfoFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    int tabcount;
    public HomePagerAdapter(FragmentManager fm, int count){
        super(fm);
        this.tabcount=count;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AudioFragment();
            case 1:
                return new ProductInfoFragment();
            default:
                return new AudioFragment();
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
