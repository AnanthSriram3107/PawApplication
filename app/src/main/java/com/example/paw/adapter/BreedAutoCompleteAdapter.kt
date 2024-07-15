package com.example.paw.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.paw.R

class BreedAutoCompleteAdapter(
    context: Context,
    resource: Int,
    private val breedList: List<String>
) : ArrayAdapter<String>(context, resource, breedList) {

    private var filteredBreedList: List<String> = breedList
    private var filter: Filter? = null

    override fun getCount(): Int = filteredBreedList.size

    override fun getItem(position: Int): String = filteredBreedList[position]


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.breeds_dropdown_item, parent, false)
        val breedTextView = view.findViewById<TextView>(R.id.breedTextView)
        breedTextView.text = filteredBreedList[position]
        return view
    }

    fun setFilter(customFilter: Filter) {
        this.filter = customFilter
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    val filteredList = breedList.filter { breed ->
                        breed.contains(constraint, ignoreCase = true)
                    }
                    results.values = filteredList
                    results.count = filteredList.size
                } else {
                    results.values = breedList
                    results.count = breedList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredBreedList = results?.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}