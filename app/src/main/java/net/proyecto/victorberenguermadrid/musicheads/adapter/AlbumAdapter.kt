package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class AlbumAdapter(private val albumList: List<Album>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumTitle: TextView = itemView.findViewById(R.id.albumTitle)
        val albumArtist: TextView = itemView.findViewById(R.id.albumArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val currentAlbum = albumList[position]
        holder.albumTitle.text = currentAlbum.titulo
        currentAlbum.artistRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                holder.albumArtist.text = document.getString("nombre")
            } else {
                holder.albumArtist.text = "Unknown Artist"
            }
        }?.addOnFailureListener { exception ->
            holder.albumArtist.text = "Error: ${exception.message}"
        }
    }

    override fun getItemCount() = albumList.size
}