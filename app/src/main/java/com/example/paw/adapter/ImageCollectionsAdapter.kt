package com.example.paw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paw.R

class ImageCollectionsAdapter : ListAdapter<String, ImageCollectionsAdapter.ImageCollectionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_collections, parent, false) // Use your grid item layout
        return ImageCollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageCollectionViewHolder, position: Int) {
        val imageUrl = getItem(position)
        holder.bind(imageUrl)
    }

    class ImageCollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView =
            itemView.findViewById(R.id.imageView) // Assuming you have an ImageView with this ID

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.paw_placeholder) // Use a placeholder image
                .into(imageView)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}