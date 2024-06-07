package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.proyecto.victorberenguermadrid.musicheads.R


data class Search(
    val id: String,
    val name: String,
    val type: String,  // "album" o "artist"
    val additionalInfo: String? = null, // Informacion adicional en caso de album, el artista correspondiente
    val imageUrl: String? = null
)
class SearchAdapter(
    private val searchResults: List<Search>,
    private val onItemClick: (Search) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvNombreBusqueda)
        val additionalInfoTextView: TextView = view.findViewById(R.id.tvInfoBusqueda)
        val searchItemImageView: ImageView = itemView.findViewById(R.id.ivItemSearch)


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

        Glide.with(holder.itemView.context)
            .load(searchResult.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_alert_circle)
            .into(holder.searchItemImageView)
    }

    override fun getItemCount() = searchResults.size
}