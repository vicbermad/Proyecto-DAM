package net.proyecto.victorberenguermadrid.musicheads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.adapter.AlbumAdapter
import net.proyecto.victorberenguermadrid.musicheads.databinding.FragmentHomeBinding
import net.proyecto.victorberenguermadrid.musicheads.model.Album

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private val albumList = mutableListOf<Album>()
    private val db = FirebaseFirestore.getInstance()

private var _binding: FragmentHomeBinding? = null
    lateinit var auth:FirebaseAuth
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //inicioUsuario()
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

      recyclerView = root.findViewById(R.id.recyclerViewAlbums)
      recyclerView.layoutManager = LinearLayoutManager(requireContext())
      albumAdapter = AlbumAdapter(albumList)
      recyclerView.adapter = albumAdapter

      fetchAlbumsFromFirestore()

      /*
    val textView: TextView = binding.tvUser
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
       */
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}