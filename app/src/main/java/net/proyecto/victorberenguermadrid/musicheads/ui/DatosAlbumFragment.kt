package net.proyecto.victorberenguermadrid.musicheads.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class DatosAlbumFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_datos_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumTitle = view.findViewById<TextView>(R.id.tvAlbumTitleDatos)
        val albumArtist = view.findViewById<TextView>(R.id.tvAlbumArtistDatos)
        val albumDate = view.findViewById<TextView>(R.id.tvAlbumDate)
        val albumNumSongs = view.findViewById<TextView>(R.id.tvSongNum)
        val albumGenre = view.findViewById<TextView>(R.id.tvAlbumGenre)
        val fabFavorite = view.findViewById<FloatingActionButton>(R.id.fabFav)

        // Obtener los datos del Bundle
        val title = arguments?.getString("albumTitle")
        val artist = arguments?.getString("artistRefPath")
        val date = arguments?.getString("albumDate")
        val numSongs = arguments?.getInt("albumNumSongs")
        val genre = arguments?.getString("albumGenre")



        // Configurar los TextViews con los datos del álbum
        albumTitle.text = title
        albumArtist.text = artist
        albumDate.text = date
        albumNumSongs.text = numSongs?.toString() + " Canciones"
        albumGenre.text = genre

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (artist != null) {
            val artistRef = FirebaseFirestore.getInstance().document(artist)
            artistRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    albumArtist.text = document.getString("nombre")
                } else {
                    albumArtist.text = "Unknown Artist"
                }
            }.addOnFailureListener { exception ->
                albumArtist.text = "Error: ${exception.message}"
            }
        }

        val db = FirebaseFirestore.getInstance()
        val artistDocumentRef = db.document(artist ?: "")
        artistDocumentRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val artistName = document.getString("nombre")
                val artistAge = document.getLong("edad")?.toInt()
                val artistBio = document.getString("biografia")

                albumArtist.text = artistName

                // Configurar el click listener para navegar a ArtistDetailFragment
                albumArtist.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("artistName", artistName)
                        putInt("artistAge", artistAge ?: 0)
                        putString("artistBio", artistBio)
                    }
                    findNavController().navigate(R.id.action_datosAlbumFragment_to_DatosArtistaFragment, bundle)
                }
            }
        }.addOnFailureListener { exception ->
            albumArtist.text = "Error: ${exception.message}"
        }

        // Verificar si el álbum ya está en favoritos y actualizar el icono
        userId?.let { uid ->
            checkIfAlbumIsFavorite(uid, title) { isFavorite ->
                if (isFavorite) {
                    fabFavorite.setImageResource(R.drawable.ic_heart)
                } else {
                    fabFavorite.setImageResource(R.drawable.ic_heart_vacio)
                }
            }

            // Configurar el click listener del FloatingActionButton
            fabFavorite.setOnClickListener {
                checkIfAlbumIsFavorite(uid, title) { isFavorite ->
                    if (isFavorite) {
                        removeAlbumFromFavorites(uid, title, fabFavorite)
                    } else {
                        addAlbumToFavorites(uid, title, artist, fabFavorite)
                    }
                }
            }
        }
    }
    private fun addAlbumToFavorites(userId: String, albumId: String?, artistId: String?, fabFavorite: FloatingActionButton) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(albumId!!)

        val albumRef = db.document(artistId!!).collection("albumes").document(albumId)
        val favoriteData = hashMapOf("albumRef" to albumRef)

        favoriteRef.set(favoriteData)
            .addOnSuccessListener {
                // Actualizar el icono a corazón lleno
                fabFavorite.setImageResource(R.drawable.ic_heart)
                Toast.makeText(fabFavorite.context, "Álbum añadido a favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Manejar el error
                Toast.makeText(fabFavorite.context, "Error al añadir a favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun removeAlbumFromFavorites(userId: String, albumId: String?, fabFavorite: FloatingActionButton) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(albumId ?: "")

        favoriteRef.delete()
            .addOnSuccessListener {
                // Actualizar el icono a corazón vacío
                fabFavorite.setImageResource(R.drawable.ic_heart_vacio)
                Toast.makeText(fabFavorite.context, "Álbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Manejar el error
                Toast.makeText(fabFavorite.context, "Error al eliminar de favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun checkIfAlbumIsFavorite(userId: String, albumId: String?, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(albumId ?: "")

        favoriteRef.get().addOnSuccessListener { document ->
            callback(document.exists())
        }.addOnFailureListener {
            callback(false)
        }
    }
}