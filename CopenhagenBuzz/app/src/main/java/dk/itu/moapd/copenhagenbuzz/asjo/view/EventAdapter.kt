package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.EventRowItemBinding
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentTimelineBinding

import dk.itu.moapd.copenhagenbuzz.asjo.model.Event


class EventAdapter(options: FirebaseListOptions<Event>,private val fm: FragmentManager,private val context: Context, private val isGuest:Boolean) :
    FirebaseListAdapter<Event>(options) {

    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "EventAdapter"
    }

    override fun populateView(view: View, event: Event, position: Int) {
        val binding = EventRowItemBinding.bind(view)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        binding.buttonFavorite.isVisible = !isGuest
        Log.d(TAG, "🔥 populateView() called for ${event.eventName} at pos $position")

        val eventKey = getRef(position).key!!

        val allFavRef = FirebaseDatabase
            .getInstance(dotenv)
            .getReference("favorites")

        val userFavRef = FirebaseDatabase
            .getInstance(dotenv)
            .getReference("favorites")
            .child(uid)
            .child(eventKey)

        userFavRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isFav = snapshot.exists()
                binding.buttonFavorite.setIconResource(
                    if (isFav)
                        R.drawable.baseline_favorite_24
                    else
                        R.drawable.outline_favorite_border_24
                )
            }

            override fun onCancelled(err: DatabaseError) { /* ignore */
            }
        })

        with(binding) {

            buttonFavorite.setOnClickListener {
                userFavRef.get().addOnSuccessListener { snap ->
                    if (snap.exists()) {
                        // unfavorite: remove it
                        userFavRef.removeValue()
                            .addOnCompleteListener { buttonFavorite.setIconResource(R.drawable.outline_favorite_border_24) }
                    } else {
                        // favorite: copy the Event object into /favorites/$uid/$eventKey
                        userFavRef.setValue(event)
                            .addOnCompleteListener { buttonFavorite.setIconResource(R.drawable.baseline_favorite_24) }
                    }
                }
            }
            if (uid != event.userId) {
                editButton.visibility = View.GONE
                infoButton.visibility = View.GONE
            } else {
                editButton.visibility = View.VISIBLE
                infoButton.visibility = View.VISIBLE
            }

            editButton.setOnClickListener {
                val dialog = UpdateDataDialogFragment(event, position, this@EventAdapter)
                dialog.show(fm, "UpdateDataDialog")
            }


            textViewName.text = event.eventName
            textViewType.text = event.eventType
            textViewLocation.text = event.eventLocation
            textViewDate.text = "${event.startDate} - ${event.endDate}"
            textViewDescription.text = event.eventDescription
            imageViewPhoto.setImageResource(R.drawable.ic_launcher_background) // Placeholder


            val deleteRef = FirebaseDatabase.getInstance(dotenv)
                .getReference("events")
                .child(eventKey)

            infoButton.setOnClickListener {
                deleteRef.get().addOnSuccessListener {
                    deleteRef.removeValue()
                   // Delete event from all users favorite list
                    allFavRef.get().addOnSuccessListener { favSnapshot ->
                        for (userSnapshot in favSnapshot.children) {
                            val userId = userSnapshot.key
                            if (userSnapshot.hasChild(eventKey)) {
                                allFavRef.child(userId!!).child(eventKey).removeValue()
                            }
                        }
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    }
                    deleteRef.get().addOnFailureListener {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }}




