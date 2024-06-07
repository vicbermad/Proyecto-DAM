package net.proyecto.victorberenguermadrid.musicheads.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import java.util.Locale

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
        val albumImageView = view.findViewById<ImageView>(R.id.ivAlbum)

        // Obtener los datos del Bundle
        val title = arguments?.getString("albumTitle")
        val artist = arguments?.getString("artistRefPath")
        val dateString = arguments?.getString("albumDate")
        val numSongs = arguments?.getInt("albumNumSongs")
        val genre = arguments?.getString("albumGenre")
        val imageUrl = arguments?.getString("albumImageUrl")

        // Convertir la cadena de fecha a Timestamp
        val inputFormat  = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.ENGLISH)
        val dateE = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val formattedDate = outputFormat.format(dateE)
        //DateFormat.getDateInstance(DateFormat.FULL).format(dateString)

        // Configurar los TextViews con los datos del álbum
        albumTitle.text = title
        albumArtist.text = artist
        albumDate.text = formattedDate
        albumNumSongs.text ="·" + numSongs?.toString() + " Canciones ·"
        albumGenre.text = genre

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions().transform(RoundedCorners(16)))
                .placeholder(R.drawable.side_nav_bar) // Imagen de reemplazo mientras se carga la imagen real
                .error(R.drawable.ic_alert_circle) // Imagen en caso de error
                .into(albumImageView)
        }

        (activity as AppCompatActivity).supportActionBar?.title = title

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (artist != null) {
            val artistRef = FirebaseFirestore.getInstance().document(artist)
            artistRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    albumArtist.text = document.getString("nombre")
                } else {
                    albumArtist.text = "Artista Desconocido"
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
                val artistImgUrl = document.getString("imagenUrl")

                albumArtist.text = artistName

                // Configurar el click listener para navegar a ArtistDetailFragment
                albumArtist.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("artistName", artistName)
                        putInt("artistAge", artistAge ?: 0)
                        putString("artistBio", artistBio)
                        putString("artistImageUrl", artistImgUrl)
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
    private fun addAlbumToFavorites(userId: String, title: String?, artistId: String?, fabFavorite: FloatingActionButton) {
        val db = FirebaseFirestore.getInstance()
        val favoriteRef = db.collection("usuarios").document(userId).collection("favoritos").document(title!!)

        //Obtener id de Documento Album
        db.document(artistId!!).collection("albumes").whereEqualTo("titulo", title).get().addOnSuccessListener {
                documents ->
            for (document in documents) {
                val id = document.id
                val albumRef = db.document(artistId!!).collection("albumes").document(id)
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