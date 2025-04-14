package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.DialogUserInfoBinding

class UserInfoDialogFragment : DialogFragment() {

    private var _binding: DialogUserInfoBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Inflate the view using view binding.
        _binding = DialogUserInfoBinding.inflate(layoutInflater)

        // Get the current user from Firebase Authentication.
        val currentUser = FirebaseAuth.getInstance().currentUser


        // Populate the dialog view with user information.
        currentUser?.let { user ->
            with(binding) {
                textViewName.text = user.displayName ?: getString(R.string.unknown_user)
                textViewEmail.text = user.email


            }
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.user_info_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}