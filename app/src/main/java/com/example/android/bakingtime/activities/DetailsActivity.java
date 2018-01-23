package com.example.android.bakingtime.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.fragments.BaseStepFragment;
import com.example.android.bakingtime.fragments.DetailFragment;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;

public class DetailsActivity extends BaseActivity implements
        DetailFragment.OnDetailFragmentInteractionListener {
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    public static final String STEPS_KEY = "steps";
    private boolean mTwoPane;
    private Recipe recipe;
    private Steps steps;
    private Toolbar mToolbar;
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mToolbar = (Toolbar) findViewById(R.id.details_activity_toolbar);

        Log.w(LOG_TAG, "savedInstanceState: " + savedInstanceState);

        if (savedInstanceState == null){
            getDataFromMainActivity();
            fragmentToDisplay();
        }else {
            recipe = savedInstanceState.getParcelable(RECIPE_KEY);
            assert recipe != null;
            Log.w(LOG_TAG, "getDataFromInstanceState: " + recipe.getName());
        }
        setToolbarTitle(recipe);
    }

    private void setToolbarTitle(Recipe recipe) {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(recipe.getName());
    }

    private void getDataFromMainActivity() {
        //get vehicle value from uri path
        if (getIntent().getExtras() != null) {
            recipe = getIntent().getParcelableExtra(RECIPE_KEY);
//            Log.w(LOG_TAG, "getDataFromMainActivity " + recipe.getName());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_KEY, recipe);
    }


    private void fragmentToDisplay(){
        Log.w(LOG_TAG, "fragmentToDisplay: " + recipe);
        mTwoPane = checkPane();
        if (mTwoPane){
            addDetailFragment();
            showBaseStepFragment(steps);
        }else {
            addDetailFragment();
        }
    }

    private void showBaseStepFragment(Steps steps) {
        Log.w(LOG_TAG, "showBaseStepFragment: " + mTwoPane);
        this.steps = steps;
        BaseStepFragment baseStepFragment = new BaseStepFragment();

        if (!mTwoPane){
            baseStepFragment.setArguments(baseStepBundle(this.steps));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment_container, baseStepFragment)
                    .addToBackStack(null)
                    .commit();
        }else {

            baseStepFragment.setArguments(baseStepBundle(this.steps));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.steps_fragment_container, baseStepFragment)
                    .commit();
        }
    }


    private boolean checkPane(){
        mTwoPane = findViewById(R.id.steps_fragment_container) != null;
        return mTwoPane;
    }

//    public Toolbar getActivityToolbar(){
//        mToolbar = (Toolbar) findViewById(R.id.details_activity_toolbar);
//        return mToolbar;
//    }

    private void addDetailFragment() {
//        Log.w(LOG_TAG, "addDetailFragment: " + recipe.getName());
        args = new Bundle();
        args.putParcelable(RECIPE_KEY, recipe);

        final android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.details_fragment_container);
        DetailFragment detailsFragment = new DetailFragment();
        detailsFragment.setArguments(args);

        if (fragment != null){
            Log.w(LOG_TAG, "fragment != null: " + recipe.getName());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment_container, detailsFragment)
                    .commit();
        }else {
//            Log.w(LOG_TAG, "addDetailFragment: " + recipe.getName());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_fragment_container, detailsFragment)
                    .commit();
        }
    }


    private Bundle baseStepBundle(Steps steps){
        args = new Bundle();
        args.putParcelable(RECIPE_KEY, recipe);
        if (steps != null){
            Log.w(LOG_TAG, "steps != null: "+ steps.getShortDescription());
            args.putParcelable(STEPS_KEY, steps);

        }else {
            Log.w(LOG_TAG, "steps == null: ");
            LocalStore localStore = new LocalStore(this);
            steps = localStore.getStep(0,recipe);
            steps.setSize(localStore.getStepsCursor(recipe.getId()).getCount());
            args.putParcelable(STEPS_KEY, steps);
        }
        Log.w(LOG_TAG, "baseStepBundle: "+ steps.getShortDescription());
        return args;
    }


    @Override
    public void onDetailFragmentInteraction(Steps steps) {
        Log.w(LOG_TAG, "onDetailFragmentInteraction: " + steps.getShortDescription());

        showBaseStepFragment(steps);
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                addDetailFragment();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
