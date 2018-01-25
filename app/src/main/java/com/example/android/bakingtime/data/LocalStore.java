package com.example.android.bakingtime.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bakingtime.data.Contract.IngredientEntry;
import com.example.android.bakingtime.data.Contract.RecipeEntry;
import com.example.android.bakingtime.data.Contract.StepsEntry;
import com.example.android.bakingtime.fragments.DetailFragment;
import com.example.android.bakingtime.models.Ingredients;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import java.util.ArrayList;

import static com.example.android.bakingtime.data.Contract.StepsEntry.CONTENT_URI;

public class LocalStore {
    private static final String LOG_TAG = LocalStore.class.getSimpleName();
    private SQLiteDatabase db;
    private final DbHelper dbHelper;
    private final Context mContext;
    private final SharedPreferences recipe_SP;

    public LocalStore(Context context) {
        this.mContext = context;
        this.recipe_SP = context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
        dbHelper = new DbHelper(mContext);
    }

    private void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    private void close() {
        db.close();
    }

    public void storeRecipePreference(Recipe recipe) {
        Log.w(LOG_TAG, "storeRecipeData: " + recipe.getName());
        SharedPreferences.Editor spEditor = recipe_SP.edit();
        spEditor.putInt("id", recipe.getId());
        spEditor.putString("name", recipe.getName());
        spEditor.apply();
    }

    public Recipe getStoredRecipe() {
        int id = recipe_SP.getInt("id", 0);
        String name = recipe_SP.getString("name", "");

        Log.w(LOG_TAG, id + ", " + name);

        return new Recipe(id,name);
    }



    public void storeRecipeData(Recipe recipe) {
        open();
        ArrayList<ContentValues> movieList = new ArrayList<>();
        ContentValues movieValues = new ContentValues();

        movieValues.put(RecipeEntry.COLUMN_RECIPE_ID, recipe.getId());
        movieValues.put(RecipeEntry.COLUMN_RECIPE_NAME, recipe.getName());

        movieList.add(movieValues);

        if (movieList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[movieList.size()];
            movieList.toArray(cvArray);

            //TODO: bulkInsert With Handler
            DatabaseHandler handler = new DatabaseHandler(mContext.getContentResolver());
            handler.startBulkInsert(1, null, RecipeEntry.CONTENT_URI, cvArray);
        }
        close();
    }

    public void storeIngredientsData(Ingredients ingredients, int key) {
        open();
        ArrayList<ContentValues> ingredientsList = new ArrayList<>();
        ContentValues ingredientValues = new ContentValues();
        Log.w(LOG_TAG, "storeIngredientsData: " + ingredients.getQuantity());

        ingredientValues.put(IngredientEntry.COLUMN_RECIPE_KEY, key);
        ingredientValues.put(IngredientEntry.COLUMN_QUANTITY, ingredients.getQuantity());
        ingredientValues.put(IngredientEntry.COLUMN_MEASURE, ingredients.getMeasure());
        ingredientValues.put(IngredientEntry.COLUMN_INGREDIENT, ingredients.getIngredient());

        ingredientsList.add(ingredientValues);

        if (ingredientsList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[ingredientsList.size()];
            ingredientsList.toArray(cvArray);

            //TODO: bulkInsert
            long ingredientRowId = mContext.getContentResolver().bulkInsert(IngredientEntry.CONTENT_URI, cvArray);
            if (ingredientRowId > 0) {
                Log.w(LOG_TAG, "storeIngredientsDataSUCCESS: " + ingredients.getIngredient());
            } else {
                Log.w(LOG_TAG, " >>>> ERROR Inserting into SQLiteDb: ");
            }
        }
        close();
    }

    public void storeStepsData(Steps steps, int key) {
        open();
        ArrayList<ContentValues> stepsList = new ArrayList<>();
        ContentValues stepsValues = new ContentValues();
        Log.w(LOG_TAG, "storeStepsData: " + key + " " + steps.getVideoURL());

        stepsValues.put(StepsEntry.COLUMN_ID, steps.getId());
        stepsValues.put(StepsEntry.COLUMN_RECIPE_KEY, key);
        stepsValues.put(StepsEntry.COLUMN_SHORT_DESC, steps.getShortDescription());
        stepsValues.put(StepsEntry.COLUMN_DESCRIPTION, steps.getDescription());
        stepsValues.put(StepsEntry.COLUMN_VIDEO_URL, steps.getVideoURL());
        stepsValues.put(StepsEntry.COLUMN_THUMBNAIL_URL, steps.getThumbnailURL());

        stepsList.add(stepsValues);

        if (stepsList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[stepsList.size()];
            stepsList.toArray(cvArray);

            //TODO: bulkInsert
            long ingredientRowId = mContext.getContentResolver().bulkInsert(CONTENT_URI, cvArray);
            if (ingredientRowId > 0) {
                Log.w(LOG_TAG, "storeStepsDataSUCCESS: " + steps.getShortDescription());
            } else {
                Log.w(LOG_TAG, " >>>> ERROR Inserting into SQLiteDb: ");
            }
        }
        close();
    }

    public Steps getStep(int position, Recipe recipe) {
        Log.w(LOG_TAG, "getStep: " + position);

        Steps steps = null;
        Uri stepsWithIdUri = buildIdWithNameUri(position, recipe);
        Cursor cursor = mContext.getContentResolver().query(
                stepsWithIdUri, DetailFragment.STEPS_COLUMN, null, null, null);

        assert cursor != null;
        if (cursor.getCount() > 0) {
            Log.w(LOG_TAG, "getStepById: " + stepsWithIdUri);
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(DetailFragment.COL_STEP_ID);
                String shortDesc = cursor.getString(DetailFragment.COL_SHORT_DESC);
                String description = cursor.getString(DetailFragment.COL_DESC);
                String videoUrl = cursor.getString(DetailFragment.COL_VIDEO_URL);
                String thumbnailUrl = cursor.getString(DetailFragment.COL_THUMBNAIL_URL);

                steps = new Steps(id, shortDesc, description, videoUrl, thumbnailUrl);

            } while (cursor.moveToNext());
//            steps.setSize(cursor.getCount());
            Log.w(LOG_TAG, "getStepSize: " + cursor.getCount() + " " + steps.getDescription());
            cursor.close();
        } else {
            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: ");
            cursor.close();
        }
        return steps;
    }

    public Cursor getStepsCursor(int recipeId) {
        Log.w(LOG_TAG, "getLocalSteps: ");

        Uri stepsWithRecipeIdUri = StepsEntry.buildKeyUri(recipeId);
        Cursor cursor = mContext.getContentResolver().query(
                stepsWithRecipeIdUri, DetailFragment.STEPS_COLUMN, null, null, null);

        assert cursor != null;
        return cursor;
    }

    public Cursor getIngredientsCursor(int recipeId) {
        Log.w(LOG_TAG, "getLocalSteps: ");

        Uri ingredientsWithRecipeIdUri = IngredientEntry.buildKeyUri(recipeId);
        Cursor cursor = mContext.getContentResolver().query(
                ingredientsWithRecipeIdUri, DetailFragment.INGREDIENT_COLUMN, null, null, null);

        assert cursor != null;
        return cursor;
    }


    private static Uri buildIdWithNameUri(int position, Recipe recipe) {
        return CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(position))
                .appendPath(String.valueOf(recipe.getId()))
                .build();
    }

    public static int getIdFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

    public static int getKeyFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(2));
    }

}
