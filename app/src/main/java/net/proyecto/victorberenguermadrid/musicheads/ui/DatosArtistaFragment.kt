package net.proyecto.victorberenguermadrid.musicheads.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.databinding.FragmentDatosArtistaBinding
import net.proyecto.victorberenguermadrid.musicheads.model.Artista
import net.proyecto.victorberenguermadrid.musicheads.viewmodel.AppViewModel

class DatosArtistaFragment : Fragment(){
    private var _binding: FragmentDatosArtistaBinding? = null
    private val viewModel: AppViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDatosArtistaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.obtenDatosArtista()
        viewModel.datosArtistaLiveData.observe(viewLifecycleOwner) { artista ->
            if (artista != null) {
                iniciaDatosArtista(artista)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun iniciaDatosArtista(artista: Artista) {
        binding.tvNombre.text = artista.nombre
        binding.tvEdad.text = String.format(getString(R.string.edad), artista.edad)
        binding.tvBiografia.text = String.format(getString(R.string.biografia), artista.biografia)
    }
}