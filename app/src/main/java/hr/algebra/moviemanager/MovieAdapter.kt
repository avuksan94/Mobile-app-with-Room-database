package hr.algebra.moviemanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.moviemanager.dao.Movie
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MovieAdapter(private val context: Context, private val movies: MutableList<Movie>,
                   private val navigableFragment: NavigableFragment
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)

        fun bind(movie: Movie) {
            tvTitle.text = movie.toString()
            Picasso.get()
                .load(File(movie.picturePath))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(itemView = LayoutInflater.from(context).inflate(R.layout.movie, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivDelete.setOnLongClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    (context?.applicationContext as App).getMovieDao().delete(movies[position])
                    File(movies[position].picturePath).delete()
                }
                movies.removeAt(position)
                notifyDataSetChanged()
            }
            true
        }
        holder.itemView.setOnClickListener {
            navigableFragment.navigate(Bundle().apply {
                putLong(MOVIE_ID, movies[position]._id!!)
            })
        }
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size
}