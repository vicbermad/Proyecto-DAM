package net.proyecto.victorberenguermadrid.musicheads.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.adapter.Search
import net.proyecto.victorberenguermadrid.musicheads.adapter.SearchAdapter
import net.proyecto.victorberenguermadrid.musicheads.model.Album
import net.proyecto.victorberenguermadrid.musicheads.model.AlbumWithArtist
import net.proyecto.victorberenguermadrid.musicheads.model.Artista

class SearchFragment : Fragment() {

    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchResultsAdapter: SearchAdapter
    private val searchResults = mutableListOf<Search>()

    private val artistasList = mutableListOf<Artista>()
    private val albumsList = mutableListOf<AlbumWithArtist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        searchResultsAdapter = SearchAdapter(searchResults) { searchResult ->
            handleSearchResultClick(searchResult)
        }

        searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultsRecyclerView.adapter = searchResultsAdapter

        loadData()

        searchButton.setOnClickListener {
            val query = searchInput.text.toString()
            performSearch(query)
        }
    }

    private fun loadData() {
        val db = FirebaseFirestore.getInstance()

        // Load artists
        db.collection("artistas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val artist = document.toObject(Artista::class.java)
                    artistasList.add(artist)
                }
            }

        // Load albums
        db.collectionGroup("albumes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val album = document.toObject(Album::class.java)
                    album.artistRef?.get()?.addOnSuccessListener { artistSnapshot ->
                        val artist = artistSnapshot.toObject(Artista::class.java)
                        artist?.let { AlbumWithArtist(album, it) }?.let { albumsList.add(it) }
                    }
                }
            }
    }

    private fun performSearch(query: String) {
        searchResults.clear()

        // Search for artists
        val matchedArtists =
            artistasList.filter { it.nombre?.contains(query, ignoreCase = true) == true }
        matchedArtists.forEach { artist ->
            artist.nombre?.let {
                Search(
                    id = it,
                    name = artist.nombre ?: "",
                    type = "artist"
                )
            }?.let {
                searchResults.add(
                    it
                )
            }
        }

        // Search for albums
        val matchedAlbums =
            albumsList.filter { it.album.titulo?.contains(query, ignoreCase = true) == true }
        matchedAlbums.forEach { albumWithArtist ->
            albumWithArtist.album.titulo?.let {
                Search(
                    id = it,
                    name = albumWithArtist.album.titulo ?: "",
                    type = "album",
                    additionalInfo = albumWithArtist.artist.nombre
                )
            }?.let {
                searchResults.add(
                    it
                )
            }
        }

        searchResultsAdapter.notifyDataSetChanged()
    }

    private fun handleSearchResultClick(searchResult: Search) {
        when (searchResult.type) {
            "artist" -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToDatosArtistaFragment()
                findNavController().navigate(action)
            }

            "album" -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToDatosAlbumFragment()
                findNavController().navigate(action)
            }
        }
    }
}