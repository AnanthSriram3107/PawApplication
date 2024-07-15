package com.example.paw.view

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.paw.R
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testInit_noFavoriteBreed() {
        onView(withId(R.id.close_btn)).perform(click())
        onView(withId(R.id.imageView)).check(matches(isDisplayed()))
    }

    @Test
    fun testInit_withFavoriteBreed() {
        val sharedPrefsRule = object : TestRule {
            override fun apply(base: Statement, description: Description): Statement {
                return object : Statement() {
                    override fun evaluate() {
                        val context = InstrumentationRegistry.getInstrumentation().targetContext
                        val sharedPrefs =
                            context.getSharedPreferences("dogbreed", Context.MODE_PRIVATE)
                        sharedPrefs.edit().putString("breed", "hound").apply()
                        base.evaluate()
                    }
                }
            }
        }
        val statement = object : Statement() {
            override fun evaluate() {
                onView(withId(R.id.imageView)).check(matches(isDisplayed()))
            }
        }
        val description = Description.EMPTY
        sharedPrefsRule.apply(statement, description)
    }

    @Test
    fun testBreedSelectionInDialog() {
        onView(withId(R.id.breeds_auto_complete_view)).perform(typeText("hound"), click())
        onView(withId(R.id.continue_btn)).perform(click())
        onView(withId(R.id.imageView)).check(matches(isDisplayed()))
    }

    @Test
    fun testSaveButton_noFavoriteBreed() {
        onView(withId(R.id.close_btn)).perform(click())
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()))
        onView(withId(R.id.saveButton)).perform(click())
        onView(withText(containsString("Select Favorite Breed"))).check(matches(isDisplayed()))
    }
}