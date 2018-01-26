package com.example.android.bakingtime.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.bakingtime.data.Contract.IngredientEntry;
import com.example.android.bakingtime.data.Contract.RecipeEntry;
import com.example.android.bakingtime.data.Contract.StepsEntry;

class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "baking.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        try {
            String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE "
                    + RecipeEntry.TABLE_NAME + " ("
                    + RecipeEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, "
                    + RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                    + RecipeEntry.COLUMN_RECIPE_SERVING + " INTEGER NOT NULL, "
                    + RecipeEntry.COLUMN_RECIPE_IMAGE + " TEXT, "

                    + "UNIQUE (" + RecipeEntry.COLUMN_RECIPE_ID + ") ON CONFLICT REPLACE );";

            String SQL_CREATE_INGREDIENTS_TABLE = "CREATE TABLE "
                    + IngredientEntry.TABLE_NAME + " ("
                    + IngredientEntry.COLUMN_RECIPE_KEY + " INTEGER NOT NULL, "
                    + IngredientEntry.COLUMN_QUANTITY + " REAL NOT NULL, "
                    + IngredientEntry.COLUMN_MEASURE + " TEXT NOT NULL, "
                    + IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL );";

            String SQL_CREATE_STEPS_TABLE = "CREATE TABLE "
                    + StepsEntry.TABLE_NAME + " ("
                    + StepsEntry.COLUMN_ID + " INTEGER NOT NULL, "
                    + StepsEntry.COLUMN_RECIPE_KEY + " INTEGER NOT NULL, "
                    + StepsEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, "
                    + StepsEntry.COLUMN_DESCRIPTION + " TEXT, "
                    + StepsEntry.COLUMN_VIDEO_URL + " TEXT, "
                    + StepsEntry.COLUMN_THUMBNAIL_URL + " TEXT );";

            db.execSQL(SQL_CREATE_RECIPE_TABLE);
            db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
            db.execSQL(SQL_CREATE_STEPS_TABLE);

        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String SQL_DROP_RECIPE_TABLE = "DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME;
            String SQL_DROP_INGREDIENTS_TABLE = "DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME;
            String SQL_DROP_STEPS_TABLE = "DROP TABLE IF EXISTS " + StepsEntry.TABLE_NAME;

            db.execSQL(SQL_DROP_RECIPE_TABLE);
            db.execSQL(SQL_DROP_INGREDIENTS_TABLE);
            db.execSQL(SQL_DROP_STEPS_TABLE);

            onCreate(db);
        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }
}
