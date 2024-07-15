package com.example.paw.view

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.paw.R
import com.example.paw.adapter.BreedAutoCompleteAdapter
import com.example.paw.utils.SharedPreferenceManager
import com.example.paw.utils.Utils
import com.example.paw.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private var selectedMonth: Int = -1
    private var selectedDay: Int = -1
    private var selectedYear: Int = -1
    private val sharedPreferenceManager: SharedPreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.setStatusBarColor(window, R.color.mild_yellow, this@MainActivity)

        imageView = findViewById(R.id.imageView)
        saveButton = findViewById(R.id.saveButton)
        sharedPreferenceManager.getSharedPreferences(this)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        if (sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
                .isNullOrBlank()
        ) {
            viewModel.fetchBreeds()
            showInputDialog { breedSelected ->
                if (breedSelected) {
                    val favoriteBreed =
                        sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
                    favoriteBreed?.let { viewModel.loadRandomDogImageByBreed(it) }
                } else {
                    viewModel.fetchRandomDogImage()
                }
            }
        } else {
            lifecycleScope.launch {
                if (!viewModel.hasFavoriteImage()) {
                    val favoriteBreed =
                        sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
                    favoriteBreed?.let { viewModel.loadRandomDogImageByBreed(it) }
                }
            }
        }

        viewModel.dogImage.observe(this) { imageUrl ->
            if (imageUrl != null) {
                progressBar.visibility = View.VISIBLE // Show ProgressBar
                Glide.with(this)
                    .load(imageUrl)
                    .listener(object :
                        com.bumptech.glide.request.RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: com.bumptech.glide.load.engine.GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE // Hide ProgressBar on failure
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE // Hide ProgressBar on success
                            return false
                        }
                    })
                    .into(imageView)
            }
        }

        saveButton.setOnClickListener {
            val breed = sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
            if (breed.isNullOrBlank()) {
                showBreedSelectionPrompt()
            } else {
                viewModel.dogImage.value?.let { imageUrl ->
                    lifecycleScope.launch {
                        if (viewModel.hasFavoriteImage()) {
                            showReplaceConfirmationDialog(imageUrl, breed)
                        } else {
                            viewModel.dogImage.value?.let { imageUrl ->
                                viewModel.saveFavoriteDogImage(imageUrl, breed.ifBlank { null })
                            }
                        }
                    }
                }
            }
        }
        val browseButton: Button = findViewById(R.id.browseButton)
        browseButton.setOnClickListener {
            val intent = Intent(this, PawBrowseBreedActivity::class.java)
            startActivity(intent)
        }

        val saveDogOfTheDayButton: Button = findViewById(R.id.dogOfTheDayButton)
        saveDogOfTheDayButton.setOnClickListener {
            val selectedImageUrl = viewModel.dogImage.value
            val selectedBreed =
                sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
            saveDogForTheDay(selectedImageUrl, selectedBreed)
        }
    }

    private fun saveDogForTheDay(selectedImageUrl: String?, selectedBreed: String?) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Handle date selection
                selectedYear = year
                selectedMonth = monthOfYear + 1 // DatePickerDialog months are 0-indexed
                selectedDay = dayOfMonth
                viewModel.saveDogOfTheDay(
                    selectedMonth,
                    selectedDay,
                    selectedImageUrl,
                    selectedBreed
                )
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showBreedSelectionPrompt() {
        AlertDialog.Builder(this)
            .setTitle("Select Favorite Breed")
            .setMessage("Please choose your favorite breed to save a dog image.")
            .setPositiveButton("OK") { _, _ ->
                showInputDialog { breedSelected ->
                    if (breedSelected) {
                        val favoriteBreed =
                            sharedPreferenceManager.getStringValue(this@MainActivity, "breed", "")
                        favoriteBreed?.let { viewModel.loadRandomDogImageByBreed(it) }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showInputDialog(onBreedSelected: (Boolean) -> Unit) {
        val alert = AlertDialog.Builder(this)
        val layoutInflater = layoutInflater
        val dialogLayout: View = layoutInflater.inflate(R.layout.breeds_custom_layout, null)
        alert.setView(dialogLayout)
        val dialog = alert.create()
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setCanceledOnTouchOutside(false)

        val breedsAutoCompleteTextView: AutoCompleteTextView =
            dialogLayout.findViewById(R.id.breeds_auto_complete_view)
        viewModel.breeds.observe(this) { dogBreeds ->
            val adapter = BreedAutoCompleteAdapter(
                this@MainActivity,
                R.layout.breeds_dropdown_item,
                dogBreeds
            )
            breedsAutoCompleteTextView.setAdapter(adapter)

        }
        val continueBtn = dialogLayout.findViewById<Button>(R.id.continue_btn)
        continueBtn.setOnClickListener { _: View? ->
            val selectedBreed = breedsAutoCompleteTextView.text.toString()
            sharedPreferenceManager.setStringValue(this@MainActivity, "breed", selectedBreed)
            viewModel.loadRandomDogImageByBreed(selectedBreed)
            dialog.dismiss()
            onBreedSelected(true)
        }
        val closeBtn = dialogLayout.findViewById<Button>(R.id.close_btn)
        closeBtn.setOnClickListener { _: View? ->
            dialog.dismiss()
            onBreedSelected(false)
        }
        dialog.show()
    }

    private fun showReplaceConfirmationDialog(imageUrl: String, breed: String) {
        AlertDialog.Builder(this)
            .setTitle("Replace Favorite Image?")
            .setMessage("You already have a favorite dog image. Do you want to replace it with the new one?")
            .setPositiveButton("Replace") { _, _ ->
                viewModel.saveFavoriteDogImage(imageUrl, breed.ifBlank { null })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}