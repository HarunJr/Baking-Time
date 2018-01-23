package com.example.android.bakingtime.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.StepPagerAdapter;
import com.example.android.bakingtime.databinding.FragmentBaseStepBinding;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import static com.example.android.bakingtime.activities.DetailsActivity.STEPS_KEY;
import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;

public class BaseStepFragment extends Fragment {
    private static final String LOG_TAG = BaseStepFragment.class.getSimpleName();

    private ViewPager mPager;
    private TabLayout mTabs;
    private Steps steps;
    private Recipe recipe;

    public BaseStepFragment() {
        // Required empty public constructor
    }

//    // TODO: Rename and change types and number of parameters
//    public static BaseStepFragment newInstance(String param1, String param2) {
//        BaseStepFragment fragment = new BaseStepFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            steps = getArguments().getParcelable(STEPS_KEY);
            recipe = getArguments().getParcelable(RECIPE_KEY);
            if (steps != null) {
                Log.w(LOG_TAG, "onCreate " + steps.getShortDescription() + recipe.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentBaseStepBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_step, container, false);
        View rootView = binding.getRoot();

        mPager = binding.stepViewpager;
        mTabs = (TabLayout) binding.tabLayout;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert steps != null;
        Log.w(LOG_TAG, "onActivityCreated: " + steps.getId() + " " + steps.getSize());

        final StepPagerAdapter pagerAdapter = new StepPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), steps, recipe);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(steps.getId());
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPager.setCurrentItem(position);
                Log.w(LOG_TAG, "onPageSelected " + position);

            }
        });
        mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabs.setupWithViewPager(mPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STEPS_KEY, steps);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnBaseStepFragmentInteractionListener) {
//            mListener = (OnBaseStepFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnBaseStepFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnBaseStepFragmentInteractionListener {
//        void onBaseStepFragmentInteraction(Steps steps);
//    }
}
