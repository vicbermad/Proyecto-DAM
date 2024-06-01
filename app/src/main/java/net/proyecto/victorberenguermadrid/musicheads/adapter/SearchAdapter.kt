package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.proyecto.victorberenguermadrid.musicheads.R


data class Search(
    val id: String,
    val name: String,
    val type: String,  // "album" or "artist"
    val additionalInfo: String? = null // Any additional info such as artist name for albums
)
class SearchAdapter(
    private val searchResults: List<Search>,
    private val onItemClick: (Search) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvNombreBusqueda)
        val additionalInfoTextView: TextView = view.findViewById(R.id.tvInfoBusqueda)

        init {
            view.setOnClickListener {
                onItemClick(searchResults[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchResult = searchResults[position]
        holder.nameTextView.text = searchResult.name
        holder.additionalInfoTextView.text = searchResult.additionalInfo
    }

    override fun getItemCount() = searchResults.size
}