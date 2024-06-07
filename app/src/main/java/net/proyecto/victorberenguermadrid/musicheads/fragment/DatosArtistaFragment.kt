package net.proyecto.victorberenguermadrid.musicheads.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import net.proyecto.victorberenguermadrid.musicheads.R
import net.proyecto.victorberenguermadrid.musicheads.databinding.FragmentDatosArtistaBinding

class DatosArtistaFragment : Fragment(){
    private var _binding: FragmentDatosArtistaBinding? = null

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
        val artistName = view.findViewById<TextView>(R.id.tvNombre)
        val artistAge = view.findViewById<TextView>(R.id.tvEdad)
        val artistBio = view.findViewById<TextView>(R.id.tvBiografia)
        val artistImageView = view.findViewById<ImageView>(R.id.ivArtista)

        val name = arguments?.getString("artistName")
        val age = arguments?.getInt("artistAge")
        val bio = arguments?.getString("artistBio")
        val imageUrl = arguments?.getString("artistImageUrl")

        artistName.text = name
        artistAge.text = age?.toString() + " Años"
        artistBio.text = bio

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions().transform(RoundedCorners(16)))
                .placeholder(R.drawable.side_nav_bar) // Imagen de reemplazo mientras se carga la imagen real
                .error(R.drawable.ic_alert_circle) // Imagen en caso de error
                .into(artistImageView)
        }

        (activity as AppCompatActivity).supportActionBar?.title = name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}