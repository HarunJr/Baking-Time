package com.example.android.bakingtime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.bakingtime.BakingIngredientService;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.fragments.RecipeFragment;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.utilities.NetworkUtils;

public class MainActivity extends BaseActivity implements RecipeFragment.OnRecipeFragmentInteractionListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String RECIPE_KEY = "recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            NetworkUtils.initNetworkConnection(application);
            addRecipeFragment();
        }
    }

    public Toolbar getActivityToolbar(){
        return (Toolbar) findViewById(R.id.main_activity_toolbar);
    }

    private void addRecipeFragment() {
//        Log.w(LOG_TAG, "addRecipeFragment: " + toolbarTitle);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final android.support.v4.app.Fragment fragment = fragmentManager.findFragmentById(R.id.recipe_fragment_container);
        RecipeFragment recipeFragment = new RecipeFragment();

        if (fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_fragment_container, recipeFragment)
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_fragment_container, recipeFragment)
                    .commit();
        }
    }

    @Override
    public void onRecipeFragmentInteraction(Recipe recipe) {

        //store recipe to access from anywhere e.g widget
        LocalStore localStore = new LocalStore(this);
        localStore.storeRecipePreference(recipe);

        //update widget with recipe
        BakingIngredientService.startActionUpdateStepsWidget(this);
        startActivity(new Intent(getApplicationContext(), DetailsActivity.class)
                .putExtra(RECIPE_KEY, recipe));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                addRecipeFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
