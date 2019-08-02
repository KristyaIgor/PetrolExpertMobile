package com.petrolexpert.mobile.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position== 0){
            TabConection one= new TabConection();
            return one;
        }else{
            TabOther two= new TabOther();
            return  two;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}