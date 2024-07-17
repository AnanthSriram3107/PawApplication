package com.example.paw.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paw.R
import com.example.paw.adapter.BreedAutoCompleteAdapter
import com.example.paw.adapter.DogImagesAdapter
import com.example.paw.db.FavoriteDogDatabaseProvider
import com.example.paw.db.PawDataBase
import com.example.paw.utils.Utils
import com.example.paw.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PawBrowseBreedActivity : ComponentActivity(), DogImagesAdapter.ImageSelectionListener {
    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var browseBreedAutoCompleteTextView: AutoCompleteTextView
    private lateinit var imageRecyclerView: RecyclerView
    private var isInSelectionMode = false
    private val selectedImages = mutableSetOf<String>()
    private lateinit var dogImagesAdapter: DogImagesAdapter
    private lateinit var db: PawDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paw_browse_breed)
        Utils.setStatusBarColor(window, R.color.mild_yellow, this@PawBrowseBreedActivity)
        browseBreedAutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        imageRecyclerView = findViewById(R.id.recyclerView)
        viewModel.fetchBreeds()
        viewModel.breeds.observe(this) { dogBreeds ->
            val adapter = BreedAutoCompleteAdapter(
                this@PawBrowseBreedActivity,
                R.layout.breeds_dropdown_item,
                dogBreeds
            )
            browseBreedAutoCompleteTextView.setAdapter(adapter)

        }
        browseBreedAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedBreed = parent.getItemAtPosition(position) as String
                viewModel.fetchRandomDogImagesByBreed(selectedBreed)
            }
        setupRecyclerView()

        viewModel.dogImages.observe(this) { images ->
            (imageRecyclerView.adapter as DogImagesAdapter).submitList(images)
        }
        val databaseProvider = FavoriteDogDatabaseProvider(this)
        val selectTextView: TextView = findViewById(R.id.selectTextView)
        val doneTextView: TextView = findViewById(R.id.doneTextView)
        db = databaseProvider.providePawDatabase()
        selectTextView.setOnClickListener {
            isInSelectionMode = !isInSelectionMode
            dogImagesAdapter.setSelectionMode(isInSelectionMode)
            selectTextView.visibility = View.GONE
            doneTextView.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE

        }

        doneTextView.setOnClickListener {
                isInSelectionMode = false
                dogImagesAdapter.setSelectionMode(false)
                doneTextView.visibility = View.GONE
                selectTextView.visibility = View.VISIBLE
            val selectedBreed = browseBreedAutoCompleteTextView.text.toString()
            viewModel.saveDogCollection(selectedBreed, getSelectedImages(), selectedBreed)
                AlertDialog.Builder(this@PawBrowseBreedActivity)
                    .setTitle("Success")
                    .setMessage("Dog Collection saved successfully!")
                    .setPositiveButton("OK", null)
                    .show()

        }

        val viewMyCollectionsButton: Button = findViewById(R.id.viewCollectionsButton)
        lifecycleScope.launch {
            val collectionCount = db.favoriteDogDao().getCollectionCount()
            Log.d("Collection Count",collectionCount.toString())
            viewMyCollectionsButton.isVisible = collectionCount > 0
        }
        viewMyCollectionsButton.setOnClickListener {
            val intent = Intent(this, DogCollectionActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupRecyclerView() {
        dogImagesAdapter = DogImagesAdapter(this)
        imageRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        imageRecyclerView.adapter = dogImagesAdapter
    }

    override fun onImageSelected(imageUrl: String) {
        selectedImages.add(imageUrl)
    }

    override fun onImageDeselected(imageUrl: String) {
        selectedImages.remove(imageUrl)
    }

    override fun isImageSelected(imageUrl: String): Boolean {
        return selectedImages.contains(imageUrl)
    }

   private fun getSelectedImages(): List<String> {
        return selectedImages.toList()
    }
}