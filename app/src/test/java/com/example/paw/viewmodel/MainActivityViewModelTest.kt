package com.example.paw.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.paw.data.repository.PawApiRepository
import com.example.paw.db.FavoriteDog
import com.example.paw.db.FavoriteDogDao
import com.example.paw.model.PawResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var favoriteDogDao: FavoriteDogDao
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        PawApiRepository.api = mock()
        favoriteDogDao = mock()
        viewModel = MainActivityViewModel(favoriteDogDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInit_withExistingFavoriteImage() = runTest {
        val favoriteDog = FavoriteDog(id = 1, imageUrl = "null", breed = "test_breed")
        whenever(favoriteDogDao.getFavoriteDog()).thenReturn(favoriteDog)
        val viewModel = MainActivityViewModel(favoriteDogDao)
        viewModel.loadInitialDogImage()
        advanceUntilIdle()
        assertEquals("null", viewModel.dogImage.value)
    }

    @Test
    fun testInit_withExistingFavoriteBreed() = runTest {
        val favoriteDog = FavoriteDog(id = 1, imageUrl = "test_breed", breed = "test_breed")
        whenever(favoriteDogDao.getFavoriteDog()).thenReturn(favoriteDog)
        val viewModel = MainActivityViewModel(favoriteDogDao)
        viewModel.loadInitialDogImage()
        viewModel.dogImage.observeForever {}
        advanceUntilIdle()
        assertEquals(null, viewModel.lastRequestedBreed.value)
    }

    @Test
    fun testInit_withNoFavorite() = runTest {
        whenever(favoriteDogDao.getFavoriteDog()).thenReturn(null)
        viewModel.dogImage
        advanceUntilIdle()
        assertNull(viewModel.dogImage.value)
    }

    @Test
    fun testFetchRandomDogImage_success() = runTest {
        val mockResponse = PawResponse("test_image_url", "success")
        whenever(PawApiRepository.api.getRandomDogImage()).thenReturn(mockResponse)
        viewModel.fetchRandomDogImage()
        advanceUntilIdle()
        assertEquals("test_image_url", viewModel.dogImage.value)
    }

    @Test
    fun testSaveFavoriteDogImage() = runTest {
        val imageUrl = "test_image_url"
        val breed = "test_breed"
        viewModel.saveFavoriteDogImage(imageUrl, breed)
        advanceUntilIdle()
        verify(favoriteDogDao).saveFavoriteDog(argThat { imageUrl == "test_image_url" && breed == "test_breed" })
    }
}