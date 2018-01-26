package com.example.android.bakingtime.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = "com.example.android.bakingtime";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    static final String PATH_RECIPE = "recipe";
    static final String PATH_INGREDIENTS = "ingredients";
    static final String PATH_STEPS = "steps";

    public static final class RecipeEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_RECIPE_ID = "id";
        public static final String COLUMN_RECIPE_NAME= "name";
        public static final String COLUMN_RECIPE_SERVING= "servings";
        public static final String COLUMN_RECIPE_IMAGE= "image";
    }

    public static final class IngredientEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        public static final String TABLE_NAME = "ingredient_table";

        public static final String COLUMN_RECIPE_KEY = "recipe_key";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";

        public static Uri buildKeyUri(int recipeKey) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(recipeKey))
                    .build();
        }
        public static int getRecipeKeyFromUri(Uri uri) {return Integer.parseInt(uri.getPathSegments().get(1));}

    }

    public static final class StepsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();

        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;

        public static final String TABLE_NAME = "steps";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_RECIPE_KEY = "recipe_key";
        public static final String COLUMN_SHORT_DESC = "shortDescription";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "videoURL";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailURL";

        public static Uri buildKeyUri(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id))
                    .build();
        }
        public static int getKeyIdFromUri(Uri uri) {return Integer.parseInt(uri.getPathSegments().get(1));}

    }
}
