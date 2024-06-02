package net.proyecto.victorberenguermadrid.musicheads.ui.artistas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.databinding.FragmentDatosArtistaBinding
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
        val artistName = view.findViewById<TextView>(R.id.tvNombre)
        val artistAge = view.findViewById<TextView>(R.id.tvEdad)
        val artistBio = view.findViewById<TextView>(R.id.tvBiografia)

        val name = arguments?.getString("artistName")
        val age = arguments?.getInt("artistAge")
        val bio = arguments?.getString("artistBio")

        artistName.text = name
        artistAge.text = age?.toString() + " Años"
        artistBio.text = bio
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}