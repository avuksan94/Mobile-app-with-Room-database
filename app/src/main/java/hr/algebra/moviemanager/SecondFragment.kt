package hr.algebra.moviemanager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hr.algebra.moviemanager.dao.Movie
import hr.algebra.moviemanager.databinding.FragmentSecondBinding
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
const val MOVIE_ID = "hr.algebra.moviemanager.movie_id"
private const val IMAGE_TYPE = "image/*"

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private lateinit var movie: Movie

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchMovie()
        setupListeners()
    }

    private fun fetchMovie() {
        val movieId = arguments?.getLong(MOVIE_ID)
        if (movieId != null) {
            GlobalScope.launch(Dispatchers.Main) {
                movie = withContext(Dispatchers.IO) {
                    (context?.applicationContext as App).getMovieDao().getMovie(movieId!!)
                        ?: Movie()
                }
                bindMovie()
            }
        } else {
            movie = Movie()
            bindMovie()
        }
    }


    private fun bindMovie() {
        if (movie.picturePath != null) {
            Picasso.get()
                .load(File(movie.picturePath))
                .transform(RoundedCornersTransformation(50, 5))
                .into(binding.ivImage)
        } else {
            binding.ivImage.setImageResource(R.mipmap.ic_launcher)
        }
        binding.tvDate.text = movie.addedDate.format(DateTimeFormatter.ISO_DATE)
        binding.etTitle.setText(movie.title ?: "")
        binding.etDescription.setText(movie.description ?: "")
        binding.etComment.setText(movie.comment ?: "" )
    }

    private fun setupListeners() {
        binding.tvDate.setOnClickListener {
            handleDate()
        }

        binding.ivImage.setOnClickListener {
            Toast.makeText(context, "Image Clicked", Toast.LENGTH_SHORT).show()
            handleImage()
            true
        }

        binding.btnCommit.setOnClickListener {
            if (formValid()) {
                commit()
            }
        }
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                movie.title = text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                movie.description = text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                movie.comment = text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    private fun handleDate() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                movie.addedDate = LocalDate.of(year, month + 1, dayOfMonth)
                bindMovie()
            },
            movie.addedDate.year,
            movie.addedDate.monthValue - 1,
            movie.addedDate.dayOfMonth
        ).show()
    }

    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            imageResult.launch(this)
        }
    }

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (movie.picturePath != null) {
                    File(movie.picturePath).delete()
                }
                val dir = context?.applicationContext?.getExternalFilesDir(null)
                val file =
                    File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")
                context?.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->

                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        movie.picturePath = file.absolutePath
                        bindMovie()

                    }

                }
            }
        }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etTitle, binding.etDescription, binding.etComment).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_value)
                ok = false
            }
        }
        return ok && movie.picturePath != null
    }

    private fun commit() {
        GlobalScope.launch (Dispatchers.Main){
            withContext(Dispatchers.IO) {
                if (movie._id == null) {
                    (context?.applicationContext as App).getMovieDao().insert(movie)
                } else {
                    (context?.applicationContext as App).getMovieDao().update(movie)
                }
            }
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}