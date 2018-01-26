package com.example.android.bakingtime.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.SpacesItemDecoration;
import com.example.android.bakingtime.activities.BaseActivity;
import com.example.android.bakingtime.activities.MainActivity;
import com.example.android.bakingtime.adapters.RecipeAdapter;
import com.example.android.bakingtime.data.Contract.RecipeEntry;
import com.example.android.bakingtime.databinding.FragmentRecipeBinding;
import com.example.android.bakingtime.models.Recipe;

public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = RecipeFragment.class.getSimpleName();
    private static final int RECIPE_LOADER = 0;
    private RecyclerView mRecyclerView;
    private Parcelable mRecyclerViewState;
    private RecipeAdapter recipeAdapter;
    private int mScrollPosition = RecyclerView.NO_POSITION;
    private OnRecipeFragmentInteractionListener mListener;
    private Toolbar activityToolbar;
    private int spanCount = 2;

    private static final String[] RECIPE_COLUMN = {
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_RECIPE_ID,
            RecipeEntry.COLUMN_RECIPE_NAME,
            RecipeEntry.COLUMN_RECIPE_SERVING,
            RecipeEntry.COLUMN_RECIPE_IMAGE
    };
    // These indices are tied to RECIPE_COLUMN.  If RECIPE_COLUMN changes, these must change
    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_SERVINGS = 2;
    public static final int COL_IMAGE = 3;

    public  RecipeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
//    public static RecipeFragment newInstance() {
//        RecipeFragment fragment = new RecipeFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String toolbarTitle = getResources().getString(R.string.app_name);
        activityToolbar = ((MainActivity) getActivity()).getActivityToolbar();
        actionBarTitle(toolbarTitle);
    }

    private void actionBarTitle(String toolbarTitle) {
        ((BaseActivity)getActivity()).setSupportActionBar(activityToolbar);
        ActionBar actionBar = ((BaseActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(toolbarTitle);
            Log.w(LOG_TAG, "actionBarTitle: " + toolbarTitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentRecipeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe, container, false);
        View rootView = binding.getRoot();
        mRecyclerView = binding.recyclerViewRecipe;

        if (getResources().getBoolean(R.bool.isTablet)){
            Log.w(LOG_TAG, "isTablet: "+ true );
            tabOrientation(mRecyclerView);
        }else {
            Log.w(LOG_TAG, "isTablet: "+ false );
            mobileOrientation(mRecyclerView);
        }
        mRecyclerView.setHasFixedSize(true);
        recipeAdapter = new RecipeAdapter(getContext(), new RecipeAdapter.RecipeAdapterOnClickHandler() {
            @Override
            public void onClick(Recipe recipe, RecipeAdapter.RecipeViewHolder vh) {
                Log.w(LOG_TAG, "onClick: " + recipe.getName());
                onItemPressed(recipe);

            }
        });

        mRecyclerView.setAdapter(recipeAdapter);

//        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)) {
//            mRecyclerViewState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE_KEY);
//            mScrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
//
//            mRecyclerView.scrollToPosition(mScrollPosition);
//            Log.w(LOG_TAG, "state onRestoreInstanceState: " + mScrollPosition);
//        }

        return rootView;
    }

    private void mobileOrientation(RecyclerView recyclerView){
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else {
            gridSpacing(recyclerView, spanCount);
        }
    }

    private void tabOrientation(RecyclerView recyclerView){

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridSpacing(recyclerView, spanCount);
        }else {
            spanCount = 3;
            gridSpacing(recyclerView,spanCount);
        }
    }

    private void gridSpacing(RecyclerView recyclerView, int spanCount){
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    // TODO: Rename method, update argument and hook method into UI event
    private void onItemPressed(Recipe recipe) {
        if (mListener != null) {
            mListener.onRecipeFragmentInteraction(recipe);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeFragmentInteractionListener) {
            mListener = (OnRecipeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeFragmentInteractionListener");
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        LinearLayoutManager linearLayoutManagerSteps = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        setScrollPosition(outState, linearLayoutManagerSteps);
//    }

//    private void setScrollPosition(Bundle outState, LinearLayoutManager linearLayoutManager) {
//        if (linearLayoutManager != null) {
//            mRecyclerViewState = linearLayoutManager.onSaveInstanceState();
//            mScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
//            outState.putInt(SCROLL_POSITION_KEY, mScrollPosition);
//            outState.putParcelable(RECYCLERVIEW_STATE_KEY, mRecyclerViewState);
//            Log.w(LOG_TAG, "state onSaveInstanceState: mScrollPosition " + mScrollPosition);
//        }
//    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(RECIPE_LOADER, null, this);
        Log.w(LOG_TAG, "onActivityCreated");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                RecipeEntry.CONTENT_URI,
                RECIPE_COLUMN,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recipeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recipeAdapter.swapCursor(null);
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
    public interface OnRecipeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRecipeFragmentInteraction(Recipe recipe);
    }
}
