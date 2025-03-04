package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentMapsBinding


class MapsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentMapsBinding? = null

    private val binding
        get() = requireNotNull(_binding){
            "Cannot access this"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater,container, false).also {
        _binding = it
    }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}