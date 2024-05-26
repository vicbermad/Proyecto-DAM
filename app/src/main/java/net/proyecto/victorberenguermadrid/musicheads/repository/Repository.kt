package net.proyecto.victorberenguermadrid.musicheads.repository

import net.proyecto.victorberenguermadrid.musicheads.Firebase.FirebaseAccess

object Repository {
    fun obtenDatosArtista()=FirebaseAccess.obtenDatosArtista()
    fun getDatosArtistaLiveData()=FirebaseAccess.getDatosArtistaLiveData()
    fun obteDatosAlbum()=FirebaseAccess.obtenDatosAlbum()
    fun getDatosAlbumLiveData()=FirebaseAccess.getDatosAlbumLiveData()
}