package com.example.paw.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.paw.R
import com.example.paw.db.FavoriteDogDatabaseProvider
import com.example.paw.utils.Utils
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class PawSplashScreenActivity : AppCompatActivity() {
    private lateinit var splashImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paw_splash_screen)
        Utils.setStatusBarColor(window, R.color.mild_yellow, this@PawSplashScreenActivity)
        splashImageView = findViewById(R.id.splashImageView)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val db = FavoriteDogDatabaseProvider(this).providePawDatabase()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        lifecycleScope.launch {
            val dogOfTheDay = db.favoriteDogDao().getDogOfTheDay(currentMonth, currentDay)
            if (dogOfTheDay != null) {
                Glide.with(this@PawSplashScreenActivity)
                    .load(dogOfTheDay.imageUrl)
                    .placeholder(R.drawable.paw_placeholder) // Set a placeholder image
                    .into(splashImageView)
            } else {
                // Handle case where there's no dog of the day (e.g., show a default image)
                splashImageView.setImageResource(R.drawable.paw_placeholder)
            }
        }
        progressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            val i = Intent(this@PawSplashScreenActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)

    }
}