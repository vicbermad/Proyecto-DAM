package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class FavoritosAdapter(
    private var favoriteAlbums: MutableList<Album>,
    private val onAlbumClick: (Album) -> Unit,
    private val onRemoveFavoriteClick: (Album) -> Unit
) : RecyclerView.Adapter<FavoritosAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumTitle: TextView = itemView.findViewById(R.id.tvAlbumTitle)
        val albumArtist: TextView = itemView.findViewById(R.id.tvAlbumArtist)
        val favoriteButton: ImageView = itemView.findViewById(R.id.ivFavorito)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAlbumClick(favoriteAlbums[position])
                }
            }
            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveFavoriteClick(favoriteAlbums[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val album = favoriteAlbums[position]

        holder.albumTitle.text = album.titulo

        album.artistRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                holder.albumArtist.text = document.getString("nombre")
            } else {
                holder.albumArtist.text = "Unknown Artist"
            }
        }?.addOnFailureListener { exception ->
            holder.albumArtist.text = "Error: ${exception.message}"
        }
    }

    override fun getItemCount() = favoriteAlbums.size

    fun updateAlbums(albums: List<Album>) {
        favoriteAlbums.clear()
        favoriteAlbums.addAll(albums)
        notifyDataSetChanged()
    }
}