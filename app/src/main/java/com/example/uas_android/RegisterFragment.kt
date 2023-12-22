package com.example.uas_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.uas_android.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterBinding
    private val firestore = FirebaseFirestore.getInstance()
    val usersCollection = firestore.collection("users")
    private var selectedRole: String = "Public"
    private val channelid = "Test_notif"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val btnRegister = binding.root.findViewById<Button>(R.id.btnrgt)
        val usernamergt = binding.root.findViewById<EditText>(R.id.usernamergt)
        val emailrgt = binding.root.findViewById<EditText>(R.id.emailrgt)
        val notlpnrgt = binding.root.findViewById<EditText>(R.id.NotTlpnrgt)
        val passwordrgt = binding.root.findViewById<EditText>(R.id.passwordrgt)


        btnRegister.setOnClickListener {
            val usernamergtt = usernamergt.text.toString()
            val emailrgtt = emailrgt.text.toString()
            val notlpnrgtt = notlpnrgt.text.toString()
            val passwordrgtt = passwordrgt.text.toString()

            // Membuat objek user
            val user = hashMapOf(
                "username" to usernamergtt,
                "email" to emailrgtt,
                "phone" to notlpnrgtt,
                "role" to selectedRole,
                "password" to passwordrgtt
            )

            // Menyimpan data pengguna ke Firestore
            usersCollection.document(usernamergtt).set(user)
                .addOnSuccessListener {
                    // Berhasil disimpan
                    showRegistrationNotification()
                    val intentToMainActivity = Intent(activity, MainActivity::class.java)
                    startActivity(intentToMainActivity)
                }
                .addOnFailureListener {
                    // Gagal menyimpan
                    Toast.makeText(activity, "Gagal menyimpan data ke Firestore", Toast.LENGTH_SHORT).show()
                }
        }
        return binding.root
    }
    private fun showRegistrationNotification() {
        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelid,
                "Registration Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelid)
            .setSmallIcon(R.drawable.baseline_notification_important_24)
            .setContentTitle("Registrasi berhasil ")
            .setContentText("Silahkan menikmati list film yang tersedia")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}