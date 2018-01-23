package com.example.android.bakingtime.live;

import com.example.android.bakingtime.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

interface BakingWebServices {
    String RECIPE_ENDPOINT = "topher/2017/May/59121517_baking/baking.json";

    @GET(RECIPE_ENDPOINT)
    Call<List<Recipe>> getRecipes();
}
