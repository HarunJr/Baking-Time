package com.example.android.bakingtime.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bakingtime.data.Contract.IngredientEntry;
import com.example.android.bakingtime.data.Contract.RecipeEntry;
import com.example.android.bakingtime.data.Contract.StepsEntry;

import java.util.Arrays;

import static com.example.android.bakingtime.data.Contract.CONTENT_AUTHORITY;
import static com.example.android.bakingtime.data.Contract.PATH_INGREDIENTS;
import static com.example.android.bakingtime.data.Contract.PATH_RECIPE;
import static com.example.android.bakingtime.data.Contract.PATH_STEPS;

@SuppressWarnings("ConstantConditions")
public class BakeProvider extends ContentProvider {
    private static final String LOG_TAG = BakeProvider.class.getSimpleName();
    private DbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int RECIPES = 100;

    private static final int INGREDIENTS = 200;
    private static final int INGREDIENT_WITH_RECIPE_ID = 201;

    private static final int STEPS = 300;
    private static final int STEPS_WITH_RECIPE_ID = 301;
    private static final int STEPS_WITH_RECIPE_ID_AND_KEY = 302;

    private static final String sIngredientWithKeySelection =
            IngredientEntry.TABLE_NAME +
                    "." + IngredientEntry.COLUMN_RECIPE_KEY + " = ?";

    private static final String sStepsWithKeySelection =
            StepsEntry.TABLE_NAME +
                    "." + StepsEntry.COLUMN_RECIPE_KEY + " = ?";

    private static final String sStepsWithIdAndKeySelection =
            StepsEntry.TABLE_NAME +
                    "." + StepsEntry.COLUMN_ID + " = ? AND "
                    +StepsEntry.TABLE_NAME + "." + StepsEntry.COLUMN_RECIPE_KEY + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each URI you want to add, create a corresponding code.
        matcher.addURI(CONTENT_AUTHORITY, PATH_RECIPE, RECIPES);
//        matcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE + "/#", MOVIE_WITH_ID);

        matcher.addURI(CONTENT_AUTHORITY, PATH_INGREDIENTS, INGREDIENTS);
        matcher.addURI(CONTENT_AUTHORITY, PATH_INGREDIENTS + "/#", INGREDIENT_WITH_RECIPE_ID);

        matcher.addURI(CONTENT_AUTHORITY, PATH_STEPS, STEPS);
        matcher.addURI(CONTENT_AUTHORITY, PATH_STEPS + "/#", STEPS_WITH_RECIPE_ID);
        matcher.addURI(CONTENT_AUTHORITY, PATH_STEPS + "/#/#", STEPS_WITH_RECIPE_ID_AND_KEY);
        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case RECIPES: {
                Log.w(LOG_TAG, "called; RECIPES");
                retCursor = getRecipes(projection);
                break;
            }
            case INGREDIENTS: {
                Log.w(LOG_TAG, "called; INGREDIENTS");
                retCursor = getIngredients(projection);
                break;
            }
            case INGREDIENT_WITH_RECIPE_ID: {
                Log.w(LOG_TAG, "called; INGREDIENT_WITH_RECIPE_ID");
                retCursor = getIngredientWithId(projection, uri);
                break;
            }
            case STEPS: {
                Log.w(LOG_TAG, "called; STEPS");
                retCursor = getSteps(projection);
                break;
            }
            case STEPS_WITH_RECIPE_ID: {
                Log.w(LOG_TAG, "called; STEPS_WITH_RECIPE_ID");
                retCursor = getStepsWithId(projection, uri);
                break;
            }
            case STEPS_WITH_RECIPE_ID_AND_KEY: {
                Log.w(LOG_TAG, "called; STEPS_WITH_RECIPE_ID");
                retCursor = getStepWithIdAndKey(projection, uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        assert retCursor != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getStepWithIdAndKey(String[] projection, Uri uri) {
        Log.w(LOG_TAG, "called; getStepWithIdAndKey ");
        int id = LocalStore.getIdFromUri(uri);
        int key = LocalStore.getKeyFromUri(uri);

        String[] selectionArgs = {String.valueOf(id),String.valueOf(key)};

        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                StepsEntry.TABLE_NAME,
                projection,
                sStepsWithIdAndKeySelection,
                selectionArgs,
                null,
                null,
                null
        );
        Log.w(LOG_TAG, "called; getStepWithIdAndKey "+cursor.getCount()+" "+id +"\n"+ sStepsWithIdAndKeySelection+"\n"+ Arrays.toString(projection));

        return cursor;
    }

    private Cursor getStepsWithId(String[] projection, Uri uri) {
        Log.w(LOG_TAG, "called; getStepsWithId");
        int id = StepsEntry.getKeyIdFromUri(uri);
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                StepsEntry.TABLE_NAME,
                projection,
                sStepsWithKeySelection,
                selectionArgs,
                null,
                null,
                null
        );
        Log.w(LOG_TAG, "called; getStepsWithId "+cursor.getCount()+" "+id +"\n"+ sStepsWithKeySelection+"\n"+ Arrays.toString(projection));

        return cursor;
    }

    private Cursor getIngredientWithId(String[] projection, Uri uri) {
        Log.w(LOG_TAG, "called; getIngredientWithId");
        int id = IngredientEntry.getRecipeKeyFromUri(uri);
        String[] selectionArgs = {String.valueOf(id)};

        return mOpenHelper.getReadableDatabase().query(
                IngredientEntry.TABLE_NAME,
                projection,
                sIngredientWithKeySelection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getSteps(String[] projection) {
        Log.w(LOG_TAG, "called; getStep");

        return mOpenHelper.getReadableDatabase().query(
                StepsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }


    private Cursor getRecipes(String[] projection) {
        Log.w(LOG_TAG, "called; getRecipes");

        return mOpenHelper.getReadableDatabase().query(
                RecipeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getIngredients(String[] projection) {
        Log.w(LOG_TAG, "called; getIngredients");

        return mOpenHelper.getReadableDatabase().query(
                IngredientEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return RecipeEntry.CONTENT_TYPE;
            case INGREDIENTS:
                return IngredientEntry.CONTENT_TYPE;
            case STEPS:
                return StepsEntry.CONTENT_TYPE;
            case STEPS_WITH_RECIPE_ID:
                return StepsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        Log.w(LOG_TAG, "called; delete");
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case RECIPES:
                rowsDeleted = db.delete(
                        RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENTS:
                rowsDeleted = db.delete(
                        IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STEPS:
                rowsDeleted = db.delete(
                        StepsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection) selection = "1";
        switch (match) {
            case RECIPES:
                rowsUpdated = db.update(
                        RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                Log.w(LOG_TAG, "called; update RECIPES");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // this makes delete all rows return the number of rows deleted
        switch (match) {
            case RECIPES:
                int returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(RecipeEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(LOG_TAG, "bulkInsert; RECIPES: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case INGREDIENTS:
                returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(IngredientEntry.TABLE_NAME, null, value);
                        Log.w(LOG_TAG, "bulkInsert; INGREDIENTS: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case STEPS:
                returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(StepsEntry.TABLE_NAME, null, value);
                        Log.w(LOG_TAG, "bulkInsert; STEPS: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
