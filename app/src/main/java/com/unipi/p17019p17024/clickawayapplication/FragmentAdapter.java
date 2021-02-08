package com.unipi.p17019p17024.clickawayapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    String[] tabsArray = new String[] {"CoffeeFragment", "BeveragesFragment", "AccessoriesFragment"};
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
                CoffeeFragment coffeeFragment = new CoffeeFragment();
                return coffeeFragment;
            case 1:
                BeveragesFragment beveragesFragment = new BeveragesFragment();
                return beveragesFragment;
            case 2:
                AccessoriesFragment accessoriesFragment = new AccessoriesFragment();
                return accessoriesFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
