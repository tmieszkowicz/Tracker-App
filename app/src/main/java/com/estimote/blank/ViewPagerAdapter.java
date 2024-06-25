package com.estimote.blank;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.estimote.blank.fragments.Location;
import com.estimote.blank.fragments.Statistics;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Location();
            case 1:
                return new Statistics();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Location";
            case 1:
                return "Statistics";
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }
}