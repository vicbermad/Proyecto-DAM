package net.proyecto.victorberenguermadrid.musicheads.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.adapter.AlbumAdapter
import net.proyecto.victorberenguermadrid.musicheads.adapter.ArtistAdapter
import net.proyecto.victorberenguermadrid.musicheads.databinding.FragmentHomeBinding
import net.proyecto.victorberenguermadrid.musicheads.model.Album
import net.proyecto.victorberenguermadrid.musicheads.model.Artista

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private val albumList = mutableListOf<Album>()
    private val artistList = mutableListOf<Artista>()
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

private var _binding: FragmentHomeBinding? = null
    lateinit var auth:FirebaseAuth
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //inicioUsuario()
        // Agregar MenuProvider para manejar el menú
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

      recyclerView = root.findViewById(R.id.recyclerViewAlbums)
      recyclerView.layoutManager = LinearLayoutManager(requireContext())
      user?.let {
          val userId = it.uid
          albumAdapter = AlbumAdapter(albumList, userId) { album ->
              val bundle = Bundle().apply {
                  putString("albumTitle", album.titulo)
                  album.artistRef?.let { putString("artistRefPath", it.path) }
                  putString("albumDate", album.lanzamiento?.toDate()?.toString())
                  putInt("albumNumSongs", album.num_canciones)
                  putString("albumGenre", album.genero)
                  putString("albumImageUrl", album.imagenUrl)
              }
              findNavController().navigate(R.id.action_homeFragment_to_albumDetailFragment, bundle)
          }
          recyclerView.adapter = albumAdapter
      }


      /*
      artistAdapter = ArtistAdapter(artistList) { artist ->
          val bundle = Bundle().apply {
              putString("artistName", artist.nombre)
              putInt("artistAge", artist.edad)
              putString("artistBio", artist.biografia)
          }
          findNavController().navigate(R.id.action_homeFragment_to_artistDetailFragment, bundle)
      }
       */



      fetchAlbumsFromFirestore()
      //fetchArtistsFromFirestore()

    return root
  }



    /*
    fun inicioUsuario(){
        auth=FirebaseAuth.getInstance()
        val userFireBase=auth.currentUser
        binding.tvUser.text= "${userFireBase?.displayName} - ${userFireBase?.email}"
    }
     */

    private fun fetchAlbumsFromFirestore() {
        albumList.clear()

        db.collection("artistas").get().addOnSuccessListener { result ->
            for (artistDocument in result) {
                artistDocument.reference.collection("albumes").get()
                    .addOnSuccessListener { albumResult ->
                        for (albumDocument in albumResult) {
                            val album = albumDocument.toObject(Album::class.java)
                            albumList.add(album)
                        }
                        albumAdapter.notifyDataSetChanged()
                    }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    private fun fetchArtistsFromFirestore() {
        db.collection("artistas").get().addOnSuccessListener { result ->
            for (artistDocument in result) {
                val artist = artistDocument.toObject(Artista::class.java)
                artistList.add(artist)
            }
            artistAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
        }
    }
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}