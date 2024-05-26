package net.proyecto.victorberenguermadrid.musicheads.Firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import net.proyecto.victorberenguermadrid.musicheads.model.Album
import net.proyecto.victorberenguermadrid.musicheads.model.Artista

object FirebaseAccess {

    val TAG = "MusicHeads"

    private val datosArtistaLiveData = MutableLiveData<Artista?>()
    private val datosAlbumLiveData = MutableLiveData<Album?>()

    fun obtenDatosArtista() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(FirebaseContract.COLLECTION_ARTISTA).document(FirebaseContract.ID_DOC_PRINCIPAL_ARTISTA)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val artista = documentSnapshot.toObject(Artista::class.java)
            datosArtistaLiveData.postValue(artista)
        }
    }

    fun getDatosArtistaLiveData(): LiveData<Artista?> {
        return datosArtistaLiveData
    }

    fun obtenDatosAlbum() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(FirebaseContract.COLLECTION_ALBUM).document(FirebaseContract.ID_DOC_PRINCIPAL_ALBUM)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val album = documentSnapshot.toObject(Album::class.java)
            datosAlbumLiveData.postValue(album)
        }
    }

    fun getDatosAlbumLiveData(): LiveData<Album?> {
        return datosAlbumLiveData
    }

}