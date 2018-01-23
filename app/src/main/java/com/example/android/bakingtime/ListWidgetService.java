package com.example.android.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.fragments.DetailFragment;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Steps;

import static com.example.android.bakingtime.activities.DetailsActivity.STEPS_KEY;
import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;

/**
 * Created by HARUN on 11/15/2017.
 */


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();
    private final Context mContext;
    private Cursor mCursor;
    private static Recipe recipe;


    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        LocalStore localStore = new LocalStore(mContext);
        recipe = localStore.getStoredRecipe();
        int recipe_id = recipe.getId();
        Log.w(LOG_TAG, "onDataSetChanged: " + recipe.getId() + " Recipe: " + recipe.getName());


//        Uri stepsWithRecipeIdUri = Contract.StepsEntry.buildKeyUri(recipe_id);
//        if (mCursor != null) mCursor.close();
//        mCursor = mContext.getContentResolver().query(
//                stepsWithRecipeIdUri, DetailFragment.STEPS_COLUMN, null, null, null);
//
        try {
            mCursor = localStore.getStepsCursor(recipe_id);
        } finally {
            Binder.restoreCallingIdentity(Binder.clearCallingIdentity());
        }
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);

        int id = mCursor.getInt(DetailFragment.COL_STEP_ID);
        String shortDesc = mCursor.getString(DetailFragment.COL_SHORT_DESC);
        String description = mCursor.getString(DetailFragment.COL_DESC);
        String videoUrl = mCursor.getString(DetailFragment.COL_VIDEO_URL);
        String thumbnailUrl = mCursor.getString(DetailFragment.COL_THUMBNAIL_URL);

        Steps steps = new Steps(id, shortDesc, description, videoUrl, thumbnailUrl);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_steps);
        views.setTextViewText(R.id.tv_widget_steps, steps.getShortDescription());
        Log.w(LOG_TAG, "getViewAt: " + getCount());

        Bundle extras = new Bundle();
        extras.putParcelable(STEPS_KEY, steps);
        extras.putParcelable(RECIPE_KEY, recipe);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.row_widget_steps, fillIntent);
        Log.w(LOG_TAG, "getViewAtClick: " + getCount() + " Recipe: " + recipe.getName());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
