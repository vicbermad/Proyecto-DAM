package net.proyecto.victorberenguermadrid.musicheads.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.adapter.FavoritosAdapter
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class FavoritosFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritosAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val albumList = mutableListOf<Album>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view_favorites)
        recyclerView.layoutManager = LinearLayoutManager(context)

        (activity as AppCompatActivity).supportActionBar?.title = "Mis Álbumes Favoritos"

        favoritesAdapter = FavoritosAdapter(albumList, { album ->
            val bundle = Bundle().apply {
                putString("albumTitle", album.titulo)
                album.artistRef?.let { putString("artistRefPath", it.path) }
                putString("albumDate", album.lanzamiento?.toDate()?.toString())
                putInt("albumNumSongs", album.num_canciones)
                putString("albumGenre", album.genero)
                putString("albumImageUrl", album.imagenUrl)
            }
            findNavController().navigate(R.id.action_favoritosFragment_to_datosAlbumFragment, bundle)
        }, { album ->
            removeAlbumFromFavorites(album)
        })
        recyclerView.adapter = favoritesAdapter

        loadFavorites()
    }

    private fun loadFavorites() {
        userId?.let { uid ->
            db.collection("usuarios").document(uid).collection("favoritos")
                .get()
                .addOnSuccessListener { documents ->
                    val favoriteAlbums = mutableListOf<Album>()
                    for (document in documents) {
                        val albumRef = document.getDocumentReference("albumRef")
                        albumRef?.get()?.addOnSuccessListener { albumDoc ->
                            val album = albumDoc.toObject(Album::class.java)
                            album?.let { favoriteAlbums.add(it) }
                            favoritesAdapter.updateAlbums(favoriteAlbums)
                        }
                    }
                    // Si no hay documentos, actualizar el adaptador con una lista vacía
                    if (documents.isEmpty) {
                        favoritesAdapter.updateAlbums(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al cargar favoritos: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeAlbumFromFavorites(album: Album) {
        userId?.let { uid ->
            db.collection("usuarios").document(uid).collection("favoritos").document(album.titulo!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Álbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    loadFavorites()  // Actualizar la lista de favoritos
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al eliminar de favoritos: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}