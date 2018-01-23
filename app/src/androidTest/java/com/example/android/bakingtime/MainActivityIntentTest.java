package com.example.android.bakingtime;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingtime.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.example.android.bakingtime.activities.MainActivity.RECIPE_KEY;

/**
 * Created by HARUN on 1/17/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void clickRecipeItem_runsDetailsActivity() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.recyclerView_recipe)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Checks that the DetailsActivity opens with the correct tea name displayed

        intended(
                hasExtraWithKey(RECIPE_KEY)
        );

    }
}
