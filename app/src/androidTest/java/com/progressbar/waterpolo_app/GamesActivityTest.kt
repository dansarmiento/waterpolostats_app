package com.progressbar.waterpolo_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamesActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<GamesActivity> = ActivityTestRule(GamesActivity::class.java)

    @Test
    fun testAssignCapsButtonEnabled() {
        // Launch the activity
        val scenario = ActivityScenario.launch(GamesActivity::class.java)

        // Add a game to ensure the list has at least one item
        onView(withId(R.id.gameDate)).perform(typeText("2024-05-24"), closeSoftKeyboard())
        onView(withId(R.id.opponentTeam)).perform(typeText("Opponent Team A"), closeSoftKeyboard())
        onView(withId(R.id.opponentTeamLevel)).perform(typeText("Level A"), closeSoftKeyboard())
        onView(withId(R.id.addGameButton)).perform(click())

        // Perform item click on the list view to enable the button
        onView(withId(R.id.gamesListView)).perform(click())

        // Check if the Assign Caps button is enabled
        onView(withId(R.id.assignCapsButton)).check(matches(isEnabled()))
    }
}
