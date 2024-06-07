package net.proyecto.victorberenguermadrid.musicheads.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class AlbumAdapter(
    private var albumList: List<Album>,
    private val userId: String,
    private val clickListener: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImageView: ImageView = itemView.findViewById(R.id.ivItemAlbum)
        val albumTitle: TextView = itemView.findViewById(R.id.tvAlbumTitle)
        val albumArtist: TextView = itemView.findViewById(R.id.tvAlbumArtist)
        val favoriteButton: ImageView = itemView.findViewById(R.id.ivFavorito)

        fun bind(album: Album, clickListener: (Album) -> Unit) {
            albumTitle.text = album.titulo

            album.artistRef?.get()?.addOnSuccessListener { document ->
                if (document != null) {
                    albumArtist.text = document.getString("nombre")
                } else {
                    albumArtist.text = "Unknown Artist"
                }
            }?.addOnFailureListener { exception ->
                albumArtist.text = "Error: ${exception.message}"
            }

            itemView.setOnClickListener {
                clickListener(album)
            }
        }
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

        Glide.with(holder.itemView.context)
            .load(currentAlbum.imagenUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_alert_circle)
            .into(holder.albumImageView)

        holder.bind(currentAlbum, clickListener)

        // Verificar si el álbum ya está en favoritos y actualizar el icono
        checkIfAlbumIsFavorite(userId, currentAlbum.titulo!!) { isFavorite ->
            if (isFavorite) {
                holder.favoriteButton.setImageResource(R.drawable.ic_heart)
            } else {
                holder.favoriteButton.setImageResource(R.drawable.ic_heart_vacio)
            }
        }

        holder.favoriteButton.setOnClickListener {
            checkIfAlbumIsFavorite(userId, currentAlbum.titulo) { isFavorite ->
                if (isFavorite) {
                    removeAlbumFromFavorites(userId, currentAlbum.titulo, holder.favoriteButton)
                } else {
                    addAlbumToFavorites(userId, currentAlbum, holder.favoriteButton)
                }
            }
        }
    }

    override fun getItemCount() = albumList.size

    private fun addAlbumToFavorites(userId: String, album: Album, favoriteButton: ImageView) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(album.titulo!!)

        //Obtener id de Documento Album
        db.document(album.artistRef!!.path).collection("albumes").whereEqualTo("titulo", album.titulo).get().addOnSuccessListener {
                documents ->
            for (document in documents) {
                val id = document.id
                val albumRef = db.document(album.artistRef!!.path).collection("albumes").document(id)
                val favoriteData = hashMapOf("albumRef" to albumRef)
                favoriteRef.set(favoriteData)
                    .addOnSuccessListener {
                        // Actualizar el icono a corazón lleno
                        favoriteButton.setImageResource(R.drawable.ic_heart)
                        Toast.makeText(favoriteButton.context, "Álbum añadido a favoritos", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Manejar el error
                        Toast.makeText(favoriteButton.context, "Error al añadir a favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun removeAlbumFromFavorites(userId: String, albumId: String, favoriteButton: ImageView) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(albumId)

        favoriteRef.delete()
            .addOnSuccessListener {
                // Actualizar el icono a corazón vacío
                favoriteButton.setImageResource(R.drawable.ic_heart_vacio)
                Toast.makeText(favoriteButton.context, "Álbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Manejar el error
                Toast.makeText(favoriteButton.context, "Error al eliminar de favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkIfAlbumIsFavorite(userId: String, albumId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(albumId)

        favoriteRef.get().addOnSuccessListener { document ->
            callback(document.exists())
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun updateList(newList: List<Album>) {
        albumList = newList
        notifyDataSetChanged()
    }
}