package com.example.uas_android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.uas_android.databinding.ActivityEditMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Edit_Movie_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var updateId = ""
    private val movieCollection = firestore.collection("movie")
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }
    private lateinit var storageRef: StorageReference
    private var imageURI: Uri? = null
    private var imageCurent: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditMovieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference.child("images")

        val intentData = intent
        if (intentData != null) {
            updateId = intentData.getStringExtra("UPDATE_ID") ?: ""
            val title = intentData.getStringExtra("TITLE") ?: ""
            val description = intentData.getStringExtra("DESCRIPTION") ?: ""
            val image = intentData.getStringExtra("IMAGE") ?: ""
            imageCurent = Uri.parse(image)

            // Setel nilai ke dalam field EditText
            binding.txteditTitle.setText(title)
            binding.txteditDesc.setText(description)

            // Tampilkan gambar menggunakan Glide
            Glide.with(this)
                .load(image)
                .into(binding.imageedit)
        }
        with(binding) {
            btnedit.setOnClickListener {
                val title = txteditTitle.text.toString()
                val description = txteditDesc.text.toString()

                binding.barInput.visibility = View.VISIBLE
                val name = System.currentTimeMillis().toString()

                if(imageCurent == imageURI) {
                    storageRef.child(name).putFile(imageURI!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storageRef.child(name).downloadUrl.addOnSuccessListener { uri ->
                                val editedMovie = Movie(
                                    title = title,
                                    Description = description,
                                    image = uri.toString()
                                )
                                editMovie(editedMovie)
                                setEmptyField()
                            }
                        } else {
                            Log.d("Edit_Movie_Activity", "Error uploading image", task.exception)
                            binding.barInput.visibility = View.GONE
                        }
                    }
                }  else {
                    val editedMovie = Movie(
                        title = title,
                        Description = description,
                        image = imageCurent.toString())
                    editMovie(editedMovie)
                    setEmptyField()
                }
            }
            btndelete.setOnClickListener {
                val title = txteditTitle.text.toString()
                val description = txteditDesc.text.toString()
                val movieDelete = Movie(id = updateId, title = title, Description = description)
                deleteMovie(movieDelete)
                true
            }

            imageedit.setOnClickListener {
                resultLauncher.launch("image/*")
            }
        }
    }

    private fun editMovie(movie: Movie) {
        movie.id = updateId
        movieCollection.document(updateId).set(movie)
            .addOnFailureListener {
                Log.d("MainActivity3", "Error updating budget: ", it)
            }
        val intentToHomeActivity = Intent(this@Edit_Movie_Activity, MainActivity3::class.java)
        startActivity(intentToHomeActivity)
    }

    private fun deleteMovie(movie: Movie) {
        if (movie.id.isEmpty()) {
            Log.d("Edit_Movie_Activity", "Error deleting: ID film kosong!")
            return
        }
        movieCollection.document(movie.id)
            .delete()
            .addOnSuccessListener {
                Log.d("Edit_Movie_Activity", "Film berhasil dihapus!")
                val intentToMainActivity = Intent(this@Edit_Movie_Activity, MainActivity3::class.java)
                startActivity(intentToMainActivity)
            }
            .addOnFailureListener {
                Log.d("Edit_Movie_Activity", "Error menghapus film: ", it)
            }
    }

    private fun setEmptyField() {
        with(binding) {
            txteditTitle.setText("")
            txteditDesc.setText("")
            imageedit.setImageResource(android.R.color.transparent)
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        result?.let { uri ->
            imageURI = uri
            binding.imageedit.setImageURI(uri)
            imageCurent = uri
        }
    }

}