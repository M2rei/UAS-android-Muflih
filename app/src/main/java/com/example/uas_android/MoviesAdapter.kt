package com.example.uas_android

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_android.database.Movies

class MoviesAdapter(val listmovie: List<Movies>) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view).listen { position, _ ->
            val item = listmovie[position]
            onClick(parent, item)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listmovie[position])
    }

    override fun getItemCount(): Int {
        return listmovie.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Bind your views here
        private val titleTextView: TextView = itemView.findViewById(R.id.texttitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.Descview)
        private val imageView: ImageView = itemView.findViewById(R.id.imageview_rev)

        fun bind(movie: Movies) {
            titleTextView.text = movie.title
            descriptionTextView.text = movie.description
            // Bind other views as needed for the movie item

            Glide.with(itemView)
                .load(movie.image)
                .into(imageView)
        }
    }
    private fun <T: RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    private fun onClick(parent: ViewGroup, item: Movies) {
        val intent = Intent(parent.context.applicationContext, Edit_Movie_Activity::class.java)
        intent.putExtra("UPDATE_ID", item.id)
        intent.putExtra("TITLE", item.title)
        intent.putExtra("DESCRIPTION", item.description)
        intent.putExtra("IMAGE", item.image)
        parent.context.startActivity(intent)
    }
}