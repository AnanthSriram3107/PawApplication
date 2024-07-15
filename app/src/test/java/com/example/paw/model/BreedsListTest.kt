package com.example.paw.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class BreedsListTest {
    @Test
    fun testBreedsListCreation() {
        val message = mapOf("hound" to listOf("afghan", "basset"))
        val breedsList = BreedsList(message, "success")

        assertEquals(message, breedsList.message)
        assertEquals("success", breedsList.status)
    }
}