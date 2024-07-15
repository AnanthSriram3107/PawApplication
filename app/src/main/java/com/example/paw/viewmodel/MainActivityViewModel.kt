package com.example.paw.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.paw.data.repository.PawApiRepository
import com.example.paw.db.CollectionImage
import com.example.paw.db.DogCollection
import com.example.paw.db.DogOfTheDay
import com.example.paw.db.FavoriteDog
import com.example.paw.db.FavoriteDogDao
import kotlinx.coroutines.launch

class MainActivityViewModel(private val favoriteDogDao: FavoriteDogDao) : ViewModel() {
    private val _dogImage = MutableLiveData<String>()
    val dogImage: LiveData<String> get() = _dogImage
    private val _dogImages = MutableLiveData<List<String>>()
    val dogImages: LiveData<List<String>> get() = _dogImages
    private val _breeds = MutableLiveData<List<String>>()
    val breeds: LiveData<List<String>> get() = _breeds
    val lastRequestedBreed = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            loadInitialDogImage()
        }
    }

    suspend fun loadInitialDogImage() {
        val favoriteDog = favoriteDogDao.getFavoriteDog()
        if (favoriteDog != null) {
            if (favoriteDog.imageUrl != null) {
                _dogImage.value = favoriteDog.imageUrl // Load saved favorite image
            } else if (favoriteDog.breed != null) {
                loadRandomDogImageByBreed(favoriteDog.breed) // Fetch by breed
            }
        }
    }

    fun fetchRandomDogImage() {
        viewModelScope.launch {
            try {
                val response = PawApiRepository.api.getRandomDogImage()
                _dogImage.value = response.message
            } catch (e: Exception) {
                // Handle error, e.g., set a default image
            }
        }
    }

    fun loadRandomDogImageByBreed(breed: String) {
        lastRequestedBreed.value = breed
        viewModelScope.launch {
            try {
                val response = PawApiRepository.api.getRandomDogImageByBreed(breed)
                _dogImage.value = response.message
            } catch (e: Exception) {
                // Handle error, e.g., show an error message
                e.printStackTrace()
            }
        }
    }

    fun fetchRandomDogImagesByBreed(breed: String) {
        viewModelScope.launch {
            try {
                val response = PawApiRepository.api.getImagesByBreed(breed)
                _dogImages.value = response.message // Update LiveData with list of URLs
            } catch (e: Exception) {
                // Handle error, e.g., show an error message
                e.printStackTrace()
            }
        }
    }

    fun saveFavoriteDogImage(imageUrl: String, breed: String?) {
        viewModelScope.launch {
            favoriteDogDao.saveFavoriteDog(FavoriteDog(imageUrl = imageUrl, breed = breed))
        }
    }

    fun fetchBreeds() {
        viewModelScope.launch {
            try {
                val response = PawApiRepository.api.getAllBreads()
                val allBreeds = response.message.keys.toList()
                _breeds.value = allBreeds
            } catch (e: Exception) {
                // Handle error, e.g., show an error message
                e.printStackTrace()
            }
        }
    }

    fun saveDogOfTheDay(month: Int, day: Int, imageUrl: String?, breed: String?) {
        viewModelScope.launch {
            val dogOfTheDay =
                DogOfTheDay(month = month, day = day, imageUrl = imageUrl, breed = breed)
            favoriteDogDao.insertDogOfTheDay(dogOfTheDay)
        }
    }

    suspend fun hasFavoriteImage(): Boolean {
        return favoriteDogDao.getFavoriteDog()?.imageUrl != null
    }

    fun saveDogCollection(collectionName: String, imageUrls: List<String>, breed: String?) {
        viewModelScope.launch {
            val collectionId = favoriteDogDao.insertCollection(DogCollection(name = collectionName))
            imageUrls.forEach { imageUrl ->
                favoriteDogDao.insertImages(
                    CollectionImage(
                        collectionId = collectionId.toInt(),
                        imageUrl = imageUrl,
                        breed = breed
                    )
                )
            }
        }
    }

    fun getCollections(): LiveData<List<DogCollection>> {
        return favoriteDogDao.getAllCollections().asLiveData()
    }

    fun getImagesForCollection(collectionId: Long): LiveData<List<CollectionImage>> {
        return favoriteDogDao.getImagesForCollection(collectionId).asLiveData()
    }

    fun deleteCollection(collectionId: Long) {
        viewModelScope.launch {
            favoriteDogDao.deleteCollectionById(collectionId)
        }
    }
}