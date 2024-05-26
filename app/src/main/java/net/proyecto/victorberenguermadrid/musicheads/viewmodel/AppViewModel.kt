package net.proyecto.victorberenguermadrid.musicheads.viewmodel

import androidx.lifecycle.ViewModel
import net.proyecto.victorberenguermadrid.musicheads.repository.Repository

class AppViewModel:ViewModel() {

    val datosArtistaLiveData=Repository.getDatosArtistaLiveData()

    fun obtenDatosArtista()=Repository.obtenDatosArtista()

    val datosAlbumLiveData=Repository.getDatosAlbumLiveData()

    fun obtenDatosAlbum()=Repository.obteDatosAlbum()
}