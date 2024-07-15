package com.example.paw

import org.junit.After
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class PawApplicationTest : KoinTest {

    @Test
    fun testKoinModulesConfiguration() {
        startKoin {
            modules(appModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin() // Stop Koin after each test
    }

}