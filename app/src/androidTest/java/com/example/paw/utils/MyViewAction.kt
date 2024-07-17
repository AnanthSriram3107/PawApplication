package com.example.paw.utils

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

object MyViewAction {

    fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return isAssignableFrom(ViewGroup::class.java)
            }

            override fun getDescription(): String {
                return "Click on a child view with id $id"
            }

            override fun perform(uiController: UiController, view: View) {
                val childView = view.findViewById<View>(id)
                if (childView != null) {
                    uiController.loopMainThreadUntilIdle()
                    childView.performClick()
                }
            }
        }
    }
}