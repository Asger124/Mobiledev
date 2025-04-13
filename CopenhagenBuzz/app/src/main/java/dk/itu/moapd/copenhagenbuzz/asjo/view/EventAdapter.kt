package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event


class EventAdapter(private val context: Context, private var resource: Int,
                   data: List<Event>) :
    ArrayAdapter<Event>(context, R.layout.event_row_item, data) {

    companion object {
        private val TAG = EventAdapter::class.qualifiedName
    }



    private class ViewHolder(view: View) {
        val textViewName: TextView = view.findViewById(R.id.text_view_name)
        val textViewType: TextView = view.findViewById(R.id.text_view_type)
        val imageViewPhoto: ImageView = view.findViewById(R.id.image_view_photo)
        val textViewLocation: TextView = view.findViewById(R.id.text_view_location)
        val textViewDate: TextView = view.findViewById(R.id.text_view_date)
        val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
        val buttonEdit: Button = view.findViewById(R.id.edit_button)
        val buttonFavorite: MaterialButton = view.findViewById(R.id.button_favorite)
        val buttonInfo: Button = view.findViewById(R.id.button_favorite)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            resource, parent, false
        )
        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)

        Log.d(TAG, "Populate an item at position: $position")
        getItem(position)?.let { event ->
            populateViewHolder(viewHolder, event)
        }
        view.tag = viewHolder
        return view
    }

    private fun populateViewHolder(viewHolder: ViewHolder, event:Event ) {
        with(viewHolder) {

            if(event.isFavorite) {
                buttonFavorite.setIconResource(R.drawable.baseline_favorite_24)
            } else {
                buttonFavorite.setIconResource(R.drawable.outline_favorite_border_24)
            }
            textViewName.text = event.eventName
            textViewType.text = event.eventType
            textViewLocation.text = event.eventLocation
            textViewDate.text = event.eventDateRange.toString()
            textViewDescription.text = event.eventDescription
            textViewLocation.text = event.eventLocation
            textViewLocation.text = event.eventLocation
            imageViewPhoto.setImageResource(R.drawable.ic_launcher_background)

        }
    }
}