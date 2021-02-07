package com.unipi.p17019p17024.clickawayapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    String[] tabsArray = new String[] {"Coffee", "Beverages", "Accessories"};
    int tabNumber = 3;

    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabsArray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Coffee coffee = new Coffee();
                return coffee;
            case 1:
                Beverages beverages = new Beverages();
                return beverages;
            case 2:
                Accessories accessories = new Accessories();
                return accessories;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
