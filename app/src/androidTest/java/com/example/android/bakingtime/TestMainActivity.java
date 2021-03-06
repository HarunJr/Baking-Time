package com.example.android.bakingtime;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingtime.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by HARUN on 1/17/2018.
 */
@RunWith(AndroidJUnit4.class)
public class TestMainActivity {
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {
        mainActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void clickRecipeCard_opensDetailActivity() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.recyclerView_recipe)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Checks that the DetailsActivity opens with the correct name displayed
        onView(withId(R.id.tv_ingredient_title)).check(matches(isDisplayed()));
    }
}
