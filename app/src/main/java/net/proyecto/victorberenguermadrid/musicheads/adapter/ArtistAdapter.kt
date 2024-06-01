package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.model.Artista

class ArtistAdapter (
    private var artistList: List<Artista>,
    private val clickListener: (Artista) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artistName: TextView = itemView.findViewById(R.id.artistName)

        fun bind(artist: Artista, clickListener: (Artista) -> Unit) {
            artistName.text = artist.nombre
            itemView.setOnClickListener {
                clickListener(artist)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val currentArtist = artistList[position]
        holder.bind(currentArtist, clickListener)
    }

    override fun getItemCount() = artistList.size

    fun updateList(newList: List<Artista>) {
        artistList = newList
        notifyDataSetChanged()
    }
}