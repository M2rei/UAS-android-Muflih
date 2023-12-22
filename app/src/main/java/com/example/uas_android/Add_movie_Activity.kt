package com.example.uas_android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import com.example.uas_android.database.MoviesDao
import com.example.uas_android.database.MoviesRoomDatabase
import com.example.uas_android.databinding.ActivityAddMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Add_movie_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollection = firestore.collection("movie")
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }
    private lateinit var moviedb: MoviesRoomDatabase
    private lateinit var executorService: ExecutorService

    private lateinit var storageRef : StorageReference
    private var imageURI : Uri? = null
    private lateinit var image : String
    private lateinit var mdao: MoviesDao
    private  var imagecurent: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference.child("images")
        inputimage()

        moviedb = MoviesRoomDatabase.getInstance(applicationContext)
        executorService = Executors.newSingleThreadExecutor()
        mdao = moviedb.moviesDao()

        with(binding) {
            btnnn.setOnClickListener {
                val title = txtTitle.text.toString()
                val description = txtDesc.text.toString()

                binding.barInput.visibility = View.VISIBLE
                val name = System.currentTimeMillis().toString()

                imageURI?.let { uri ->
                    storageRef.child(name).putFile(uri).addOnCompleteListener { task ->
                        Toast.makeText(this@Add_movie_Activity, "${task}", Toast.LENGTH_SHORT).show()
                        if (task.isSuccessful) {

                            storageRef.child(name).downloadUrl.addOnSuccessListener { uri ->
                                val newMovie = Movie(title = title, Description = description, image = uri.toString())
                                addMovie(newMovie)
                            }
                        } else {
                            Log.d("Add_movie_Activity", "Error uploading image", task.exception)
                            binding.barInput.visibility = View.GONE
                        }
                    }
                }
            }
        }

    }
        private fun addMovie(movie: Movie) {
            movieCollection.add(movie)
                .addOnSuccessListener { documentReference ->
                    val createdMovieId = documentReference.id
                    movie.id = createdMovieId
                    documentReference.set(movie)
                        .addOnFailureListener{
                            Log.d("Add_movie_Activity", "Movie added with ID: $createdMovieId", it)
                        }
                    val intentToHomeActivity = Intent(this@Add_movie_Activity, MainActivity3::class.java)
                    startActivity(intentToHomeActivity)
                }
                .addOnFailureListener {
                    Log.d("Add_movie_Activity", "Error adding movie", it)
                }
        }
    private fun inputimage(){
        binding.imageadd.setOnClickListener {
           resultLauncher.launch("image/*")
        }
    }
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ result->
        result?.let { uri ->
            imageURI = uri
            binding.imageadd.setImageURI(uri)
            imagecurent = uri
        }

    }

}