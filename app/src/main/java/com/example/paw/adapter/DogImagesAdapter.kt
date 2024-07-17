package com.example.paw.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.paw.R
import java.io.File
import java.io.FileOutputStream

class DogImagesAdapter(private val listener: ImageSelectionListener) :
    ListAdapter<String, DogImagesAdapter.DogImageViewHolder>(DogImageDiffCallback()) {
    private var isInSelectionMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dog_image, parent, false)
        return DogImageViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: DogImageViewHolder, position: Int) {
        val imageUrl = getItem(position)
        holder.bind(imageUrl,isInSelectionMode, listener.isImageSelected(imageUrl))
    }

    fun setSelectionMode(enabled: Boolean) {
        isInSelectionMode = enabled
        notifyDataSetChanged() // Refresh the list to show/hide checkboxes
    }


    class DogImageViewHolder(itemView: View, private val listener: ImageSelectionListener) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)
        private val saveButton: ImageButton = itemView.findViewById(R.id.saveButton)
        private val checkBox: CheckBox = itemView.findViewById(R.id.imageCheckBox)
        fun bind(imageUrl: String, isInSelectionMode: Boolean, isChecked: Boolean) {
            Glide.with(itemView)
                .load(imageUrl)
                .placeholder(R.drawable.paw_placeholder)
                .into(imageView)
            shareButton.setOnClickListener {
                shareImage(itemView.context, imageUrl)
            }
            saveButton.setOnClickListener {
                saveImageToGallery(itemView.context, imageUrl)
            }
            checkBox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
            checkBox.isChecked = isChecked
            checkBox.setOnCheckedChangeListener { _, checkboxChecked ->
                if (checkboxChecked) {
                    listener.onImageSelected(imageUrl)
                } else {
                    listener.onImageDeselected(imageUrl)
                }
            }
        }

        private fun saveImageToGallery(context: Context, imageUrl: String) {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val fileName = "dog_image_${System.currentTimeMillis()}.jpg"
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES
                            )
                        }

                        val resolver = context.contentResolver
                        val uri: Uri?
                        try {
                            uri = resolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            uri?.let {
                                resolver.openOutputStream(it)?.use { outputStream ->
                                    resource.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                                }
                                Toast.makeText(
                                    context,
                                    "Image saved to gallery",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle case when the image load is cleared
                    }
                })
        }

        private fun shareImage(context: Context, imageUrl: String) {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val cachePath = File(context.cacheDir, "images")
                        cachePath.mkdirs()
                        val file = File(cachePath, "shared_image.png")
                        val fileOutputStream = FileOutputStream(file)
                        resource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                        fileOutputStream.flush()
                        fileOutputStream.close()

                        val uri = FileProvider.getUriForFile(
                            context,
                            "com.example.paw.fileprovider",
                            file
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_TEXT, "Check out this awesome dog!\n\nPowered by Dog API app") // Add the message here
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val chooser = Intent.createChooser(intent, "Share image via")
                        context.startActivity(chooser)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //Do Nothing
                    }
                })
        }
    }

    class DogImageDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    interface ImageSelectionListener {
        fun onImageSelected(imageUrl: String)
        fun onImageDeselected(imageUrl: String)
        fun isImageSelected(imageUrl: String): Boolean
    }
}