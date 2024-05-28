package net.proyecto.victorberenguermadrid.musicheads.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R

class DatosAlbumFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_datos_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumTitle = view.findViewById<TextView>(R.id.tvAlbumTitle)
        val albumArtist = view.findViewById<TextView>(R.id.tvAlbumArtist)
        val albumDate = view.findViewById<TextView>(R.id.tvAlbumDate)
        val albumNumSongs = view.findViewById<TextView>(R.id.tvSongNum)
        val albumGenre = view.findViewById<TextView>(R.id.tvAlbumGenre)

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
    }
}