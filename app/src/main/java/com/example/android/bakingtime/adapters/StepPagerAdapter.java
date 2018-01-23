package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.fragments.StepsFragment;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import java.util.Locale;

/**
 * Created by HARUN on 9/14/2017.
 */

public class StepPagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = StepPagerAdapter.class.getSimpleName();
    private final String tabTitle;
    private final Steps steps;
    private final Recipe recipe;

    public StepPagerAdapter(FragmentManager fm, Context context, Steps steps, Recipe recipe) {
        super(fm);
        this.steps = steps;
        this.recipe = recipe;
        tabTitle = context.getResources().getString(R.string.recipe_step_tab_label);
        Log.w(LOG_TAG, "StepPagerAdapter: " + steps.getShortDescription());
    }

    @Override
    public Fragment getItem(int position) {

        Log.w(LOG_TAG, "getItem: " + position +" "+steps.getId()+" "+steps.getDescription());
        return StepsFragment.newInstance(steps, position, recipe);
    }

    @Override
    public int getCount() {
        Log.w(LOG_TAG, "getCount: " + steps.getSize() +" "+steps.getId()+" "+steps.getDescription());

        return steps.getSize();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(Locale.US, tabTitle, position);
    }

}
