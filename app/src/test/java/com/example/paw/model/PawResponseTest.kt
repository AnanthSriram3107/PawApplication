package com.example.paw.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class PawResponseTest {

    @Test
    fun testPawResponseCreation() {
        val message = "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg"
        val pawResponse = PawResponse(message, "success")

        assertEquals(message, pawResponse.message)
        assertEquals("success", pawResponse.status)
    }

    @Test
    fun testPawResponseEmptyMessage() {
        val message = ""
        val pawResponse = PawResponse(message, "success")

        assertEquals(message, pawResponse.message)
        assertEquals("success", pawResponse.status)
    }

    @Test
    fun testPawResponseErrorStatus() {
        val message = "Error fetching image"
        val pawResponse = PawResponse(message, "error")

        assertEquals(message, pawResponse.message)
        assertEquals("error", pawResponse.status)
    }
}