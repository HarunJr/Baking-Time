package com.example.android.bakingtime;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bakingtime.data.LocalStore;
import com.example.android.bakingtime.live.BakingWidgetProvider;
import com.example.android.bakingtime.models.Recipe;

/**
 * Created by HARUN on 11/22/2017.
 */

public class BakingIngredientService extends IntentService {
    private static final String LOG_TAG = BakingIngredientService.class.getSimpleName();
    private static final String ACTION_UPDATE_BAKING_WIDGETS = "com.example.android.bakingtime.action.update_baking_widgets";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     **/
    public BakingIngredientService() {
        super("BakingIngredientService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_UPDATE_BAKING_WIDGETS.equals(action)){
                LocalStore localStore = new LocalStore(getApplicationContext());
                Recipe recipe = localStore.getStoredRecipe();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));
                //Trigger data update to handle the GridView widgets and force a data refresh
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                //Now update all widgets
                BakingWidgetProvider.updateStepsWidgets(this, appWidgetManager, recipe,appWidgetIds);
            }
        }
    }

    public static void startActionUpdateStepsWidget(Context context) {
        Log.w(LOG_TAG, "startActionUpdateStepsWidget: " );

        Intent intent = new Intent(context, BakingIngredientService.class);
        intent.setAction(ACTION_UPDATE_BAKING_WIDGETS);
        context.startService(intent);
    }
}
