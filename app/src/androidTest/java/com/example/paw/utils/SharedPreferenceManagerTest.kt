package com.example.paw.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferenceManagerTest {
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var context: Context

    @Before
    fun setup() {
        sharedPreferenceManager = SharedPreferenceManager()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testSetAndGetStringValue() {
        val key = "testKey"
        val value = "testValue"

        sharedPreferenceManager.setStringValue(context, key, value)
        val retrievedValue = sharedPreferenceManager.getStringValue(context, key, "")

        assertEquals(value, retrievedValue)
    }

    @Test
    fun testGetStringValue_DefaultValue() {
        val key = "nonExistingKey"
        val defaultValue = "default"

        val retrievedValue = sharedPreferenceManager.getStringValue(context, key, defaultValue)

        assertEquals(defaultValue, retrievedValue)
    }
}