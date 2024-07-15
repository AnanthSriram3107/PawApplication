package com.example.paw.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paw.R
import com.example.paw.adapter.DogCollectionAdapter
import com.example.paw.utils.Utils
import com.example.paw.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DogCollectionActivity : AppCompatActivity(), DogCollectionAdapter.onCollectionDeleteListener {
    private lateinit var collectionsRecyclerView: RecyclerView
    private lateinit var dogCollectionAdapter: DogCollectionAdapter
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_collection)
        Utils.setStatusBarColor(window, R.color.mild_yellow, this@DogCollectionActivity)
        collectionsRecyclerView = findViewById(R.id.dogImageRecyclerView)
        dogCollectionAdapter = DogCollectionAdapter(this)
        collectionsRecyclerView.adapter = dogCollectionAdapter
        collectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.getCollections().observe(this) { collections ->
            dogCollectionAdapter.submitList(collections)
        }

    }

    override fun onCollectionsDeleted(collectionId: Long) {
        viewModel.deleteCollection(collectionId)
    }
}