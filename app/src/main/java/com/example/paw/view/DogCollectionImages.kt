package com.example.paw.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paw.R
import com.example.paw.adapter.ImageCollectionsAdapter
import com.example.paw.utils.Utils
import com.example.paw.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DogCollectionImages : AppCompatActivity() {
    private lateinit var imagesCollectionRecyclerView: RecyclerView
    private lateinit var imageCollectionAdapter: ImageCollectionsAdapter
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_collection_images)
        Utils.setStatusBarColor(window, R.color.mild_yellow, this@DogCollectionImages)

        imagesCollectionRecyclerView = findViewById(R.id.imagesRecyclerView)
        imageCollectionAdapter = ImageCollectionsAdapter()

        imagesCollectionRecyclerView.layoutManager = GridLayoutManager(this, 2)
        imagesCollectionRecyclerView.adapter = imageCollectionAdapter

        val collectionId = intent.getLongExtra("collectionId", -1)
        if (collectionId != -1L) {
            viewModel.getImagesForCollection(collectionId).observe(this) { imageCollections ->
                val imageUrls = imageCollections.map {
                    it.imageUrl
                }
                imageCollectionAdapter.submitList(imageUrls)
            }
        }

    }
}