package com.example.android.bakingtime.fragments;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.IngredientsAdapter;
import com.example.android.bakingtime.adapters.StepsAdapter;
import com.example.android.bakingtime.data.Contract.IngredientEntry;
import com.example.android.bakingtime.data.Contract.StepsEntry;
import com.example.android.bakingtime.databinding.FragmentDetailBinding;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String SCROLL_POSITION_KEY = "scroll_position";
    public static final String RECYCLERVIEW_STATE_KEY = "rv_state";
    private static final int INGREDIENT_LOADER = 0;
    private static final int STEPS_LOADER = 1;
    private static  Recipe recipe;
    private RecyclerView mStepsRV;
    private IngredientsAdapter ingredientsAdapter;
    private StepsAdapter stepsAdapter;
    private Parcelable mRecyclerViewState;
    private int mScrollPosition = RecyclerView.NO_POSITION;

    private OnDetailFragmentInteractionListener mListener;

    public static final String[] INGREDIENT_COLUMN = {
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_QUANTITY,
            IngredientEntry.COLUMN_MEASURE,
            IngredientEntry.COLUMN_INGREDIENT
    };
    // These indices are tied to RECIPE_COLUMN.  If RECIPE_COLUMN changes, these must change
    public static final int COL_QUANTITY = 0;
    public static final int COL_MEASURE = 1;
    public static final int COL_INGREDIENT = 2;

    public static final String[] STEPS_COLUMN = {
            StepsEntry.TABLE_NAME + "." + StepsEntry.COLUMN_ID,
            StepsEntry.COLUMN_RECIPE_KEY,
            StepsEntry.COLUMN_SHORT_DESC,
            StepsEntry.COLUMN_DESCRIPTION,
            StepsEntry.COLUMN_VIDEO_URL,
            StepsEntry.COLUMN_THUMBNAIL_URL
    };
    // These indices are tied to STEPS_COLUMN.  If STEPS_COLUMN changes, these must change
    public static final int COL_STEP_ID = 0;
    public static final int COL_RECIPE_KEY = 1;
    public static final int COL_SHORT_DESC = 2;
    public static final int COL_DESC = 3;
    public static final int COL_VIDEO_URL = 4;
    public static final int COL_THUMBNAIL_URL = 5;

    public DetailFragment() {
        // Required empty public constructor
    }

// --Commented out by Inspection START (1/23/2018 8:22 AM):
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment DetailFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static DetailFragment newInstance(String param1, String param2) {
//        DetailFragment fragment = new DetailFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
// --Commented out by Inspection STOP (1/23/2018 8:22 AM)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(RECIPE_KEY);
//            assert recipe != null;
//            actionBarTitle(recipe.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        View rootView = binding.getRoot();

        TextView ingredientTitleView = binding.tvIngredientTitle;
        TextView stepTitleView = binding.tvStepsTitle;
        RecyclerView mIngredientRV = binding.recyclerViewIngredient;
        mStepsRV = binding.recyclerViewSteps;

        ingredientTitleView.setText(getResources().getString(R.string.ingredient_title));
        mIngredientRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIngredientRV.setNestedScrollingEnabled(false);
        mIngredientRV.setHasFixedSize(true);
        ingredientsAdapter = new IngredientsAdapter(getContext());
        mIngredientRV.setAdapter(ingredientsAdapter);

        stepTitleView.setText(getResources().getString(R.string.steps_title));
        mStepsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStepsRV.setNestedScrollingEnabled(true);
        mStepsRV.setHasFixedSize(true);
        stepsAdapter = new StepsAdapter(getActivity(), new StepsAdapter.StepsAdapterOnClickHandler() {
            @Override
            public void onClick(Steps steps, StepsAdapter.StepsViewHolder vh) {
                onButtonPressed(steps);
            }
        });
        mStepsRV.setAdapter(stepsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mStepsRV.addItemDecoration(itemDecoration);

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)) {
            mRecyclerViewState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE_KEY);
            mScrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
            RecyclerView mRecyclerView;

            mRecyclerView = mIngredientRV;

            mRecyclerView.scrollToPosition(mScrollPosition);
            Log.w(LOG_TAG, "state onRestoreInstanceState: " + mScrollPosition);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int[] loaderArray = {INGREDIENT_LOADER, STEPS_LOADER};
        for (int aLoaderArray : loaderArray) {
            getLoaderManager().initLoader(aLoaderArray, null, this);
        }
        Log.w(LOG_TAG, "onActivityCreated");
    }

//    public int getStepSize(){
//       return stepsAdapter.getItemCount();
//    }

    // TODO: Rename method, update argument and hook method into UI event
    private void onButtonPressed(Steps steps) {
        Log.w(LOG_TAG, "onButtonPressed " + steps.getSize());
        if (mListener != null) {
            mListener.onDetailFragmentInteraction(steps);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LinearLayoutManager linearLayoutManagerSteps = (LinearLayoutManager) mStepsRV.getLayoutManager();
        setScrollPosition(outState, linearLayoutManagerSteps);
    }

    private void setScrollPosition(Bundle outState, LinearLayoutManager linearLayoutManager) {
        if (linearLayoutManager != null) {
            mRecyclerViewState = linearLayoutManager.onSaveInstanceState();
            mScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_KEY, mScrollPosition);
            outState.putParcelable(RECYCLERVIEW_STATE_KEY, mRecyclerViewState);
            Log.w(LOG_TAG, "state onSaveInstanceState: mScrollPosition " + mScrollPosition);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri ingredientWithRecipeIdUri = IngredientEntry.buildKeyUri(recipe.getId());
        Uri stepsWithRecipeIdUri = StepsEntry.buildKeyUri(recipe.getId());

        switch (id) {
            case INGREDIENT_LOADER: {
                Log.w(LOG_TAG, "onCreateLoader INGREDIENT_LOADER" + ingredientWithRecipeIdUri);
                return new CursorLoader(
                        getContext(),
                        ingredientWithRecipeIdUri,
                        INGREDIENT_COLUMN,
                        null,
                        null,
                        null
                );
            }
            case STEPS_LOADER: {
                Log.w(LOG_TAG, "onCreateLoader STEPS_LOADER" + stepsWithRecipeIdUri);
                return new CursorLoader(
                        getContext(),
                        stepsWithRecipeIdUri,
                        STEPS_COLUMN,
                        null,
                        null,
                        null
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == INGREDIENT_LOADER) {
            Log.w(LOG_TAG, "onLoadFinished: INGREDIENT_LOADER " + data.getCount());
            ingredientsAdapter.swapCursor(data);
        } else {
            Log.w(LOG_TAG, "onLoadFinished: STEPS_LOADER " + data.getCount());
            stepsAdapter.swapCursor(data);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == INGREDIENT_LOADER) {
            Log.w(LOG_TAG, "onLoaderReset: INGREDIENT_LOADER");
            ingredientsAdapter.swapCursor(null);
        } else {
            Log.w(LOG_TAG, "onLoaderReset: STEPS_LOADER");
            stepsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailFragmentInteractionListener) {
            mListener = (OnDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDetailFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDetailFragmentInteraction(Steps steps);
    }
}
