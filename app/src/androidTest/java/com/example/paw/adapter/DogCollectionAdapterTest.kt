package com.example.paw.adapter


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.paw.view.MainActivity
import com.example.paw.R
import com.example.paw.db.DogCollection
import com.example.paw.utils.MyViewAction
import com.example.paw.view.DogCollectionImages
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class DogCollectionAdapterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var adapter: DogCollectionAdapter
    private val listener = mock(DogCollectionAdapter.onCollectionDeleteListener::class.java)

    @Before
    fun setup() {
        adapter = DogCollectionAdapter(listener)
        adapter.submitList(
            listOf(
                DogCollection(1, "Collection 1"),
                DogCollection(2, "Collection 2")
            )
        )
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testViewHolderBinding() {
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter

            // Check if the first item is displayed correctly
            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<DogCollectionAdapter.DogCollectionViewHolder>(
                        0
                    )
                )
            onView(withText("Collection 1")).check(matches(isDisplayed()))

            // Check if the second item is displayed correctly
            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<DogCollectionAdapter.DogCollectionViewHolder>(
                        1
                    )
                )
            onView(withText("Collection 2")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testCardViewClickLaunchesIntent() {
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter

            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<DogCollectionAdapter.DogCollectionViewHolder>(
                        0, click()
                    )
                )

            intended(
                allOf(
                    hasComponent(DogCollectionImages::class.java.name),
                    hasExtra("collectionId", 1L)
                )
            )
        }
    }

    @Test
    fun testDeleteButtonClickTriggersListener() {
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter

            // Find the delete button in the first item and click it
            onView(withId(R.id.recyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<DogCollectionAdapter.DogCollectionViewHolder>(
                        0, MyViewAction.clickChildViewWithId(R.id.delete_collections_imageView)
                    )
                )

            // Verify that the listener's onCollectionsDeleted method was called with the correct ID
            verify(listener).onCollectionsDeleted(1L)
        }
    }
}

// Helper class to click on a child view within a RecyclerView