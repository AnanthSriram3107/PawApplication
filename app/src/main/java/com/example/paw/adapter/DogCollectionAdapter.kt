package com.example.paw.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paw.R
import com.example.paw.db.DogCollection
import com.example.paw.view.DogCollectionImages

class DogCollectionAdapter(private val listener: OnCollectionDeleteListener) :
    ListAdapter<DogCollection, DogCollectionAdapter.DogCollectionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogCollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_item, parent, false) // Use your collection item layout
        return DogCollectionViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: DogCollectionViewHolder, position: Int) {
        val collectionWithImages = getItem(position)
        holder.bind(collectionWithImages)
    }


    class DogCollectionViewHolder(itemView: View,private val listener: OnCollectionDeleteListener) : RecyclerView.ViewHolder(itemView) {
        private val collectionNameTextView: TextView =
            itemView.findViewById(R.id.collection_textView)
        private val cardView: CardView = itemView.findViewById(R.id.card_view)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.delete_collections_imageView)
        private lateinit var dogCollections: DogCollection

        fun bind(collections: DogCollection) {
            this.dogCollections = collections
            collectionNameTextView.text = collections.name
            cardView.setOnClickListener {
                val intent = Intent(itemView.context, DogCollectionImages::class.java)
                intent.putExtra("collectionId", dogCollections.id)
                itemView.context.startActivity(intent)
            }
            deleteImageView.setOnClickListener {
                listener.onCollectionsDeleted(dogCollections.id)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DogCollection>() {
        override fun areItemsTheSame(
            oldItem: DogCollection,
            newItem: DogCollection
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DogCollection,
            newItem: DogCollection
        ): Boolean {
            return oldItem == newItem
        }
    }
    interface OnCollectionDeleteListener {
        fun onCollectionsDeleted(collectionId: Long)
    }
}