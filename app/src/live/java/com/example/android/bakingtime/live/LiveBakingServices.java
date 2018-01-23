package com.example.android.bakingtime.live;

import android.util.Log;

import com.example.android.bakingtime.BakingService.BakingServerRequest;
import com.example.android.bakingtime.models.Ingredients;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;
import com.example.android.bakingtime.data.Contract;
import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.infrastructure.BakingTimeApplication;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class LiveBakingServices extends BaseLiveService {
    private static final String LOG_TAG = LiveBakingServices.class.getSimpleName();
    private final LocalStore localStore = new LocalStore(application);

    LiveBakingServices(BakingTimeApplication application, BakingWebServices api) {
        super(application, api);
    }

    @Subscribe
    public void getBakingMessage(final BakingServerRequest request) {
        Log.w(LOG_TAG, "getBakingMessage: ");

        Call<List<Recipe>> call = api.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                getBakingData(response);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.w(LOG_TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getBakingData(Response<List<Recipe>> response){
        if (response.isSuccessful()) {
            application.getContentResolver().delete(Contract.IngredientEntry.CONTENT_URI, null, null);
            application.getContentResolver().delete(Contract.StepsEntry.CONTENT_URI, null, null);

            for (Recipe recipe : response.body()) {
                Log.w(LOG_TAG, "onResponse: " + recipe.getName());
                localStore.storeRecipeData(recipe);

                for (Ingredients ingredient : recipe.getIngredientsList()) {
                    Log.w(LOG_TAG, "onResponse: " + ingredient.getIngredient());
                    localStore.storeIngredientsData(ingredient, recipe.getId());
                }

                for (Steps steps : recipe.getSteps()) {
                    Log.w(LOG_TAG, "onResponse: steps" + steps.getShortDescription());
                    localStore.storeStepsData(steps, recipe.getId());
                }
            }
        }
    }
}
