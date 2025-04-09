package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FavoriteRowItemBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.R


class FavoriteAdapter(private val data: ArrayList<Event>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {


    companion object {
        private val TAG = FavoriteAdapter::class.qualifiedName
    }

     class ViewHolder(private val binding: FavoriteRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Event) {
            with(binding) {
                // Fill out the Material Design card.
                imageViewPhoto.setImageResource(R.drawable.ic_launcher_background)
                textViewName.text = favorite.eventName
                textViewSubtitle.text = favorite.eventType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = FavoriteRowItemBinding
        .inflate(LayoutInflater.from(parent.context), parent, false)
        .let(::ViewHolder)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Populate an item at position: $position")



        data[position].let(holder::bind)
    }
    override fun getItemCount() = data.size
}