package com.example.paw.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class PawBreedResponseTest {
    @Test
    fun testPawBreedResponseCreation() {
        val message = listOf("Golden Retriever", "Labrador", "Poodle")
        val pawBreedResponseTest = PawBreedResponse(message, "success")

        assertEquals(message, pawBreedResponseTest.message)
        assertEquals("success", pawBreedResponseTest.status)
    }

    @Test
    fun testPawBreedResponseEmptyList() {
        val message = emptyList<String>()
        val pawBreedResponseTest = PawBreedResponse(message, "success")

        assertEquals(message, pawBreedResponseTest.message)
        assertEquals("success", pawBreedResponseTest.status)
    }
}