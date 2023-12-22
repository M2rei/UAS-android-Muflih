package com.example.uas_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uas_android.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding

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
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        val usernamelgn = binding.root.findViewById<EditText>(R.id.usernamelgn)
        val passwordlgn = binding.root.findViewById<EditText>(R.id.passwordlgn)
        val btnLogin = binding.root.findViewById<Button>(R.id.btnlgn)

        btnLogin.setOnClickListener {
            val username = usernamelgn.text.toString()
            val password = passwordlgn.text.toString()

            authenticateUser(username, password)
        }
        return binding.root

    }
    private fun authenticateUser(username: String, password: String) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                } else {
                    val role = documents.documents[0].getString("role")
                    val notelepon = documents.documents[0].getString("phone")
                    val email = documents.documents[0].getString("email")

                    saveLoginStatus(true)
                    saveLoggedInUserRole(role!!)
                    if (role == "Admin") {
                        navigateToAdminPage()
                    } else {
                        navigateToUserPage(username, notelepon, email)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToAdminPage() {
        val intentToMainActivity3 = Intent(activity, MainActivity3::class.java)
        startActivity(intentToMainActivity3)
        activity?.finish()
    }

    private fun navigateToUserPage(username: String, notelepon: String?, email: String?) {
        val intentToMainActivity2 = Intent(activity, MainActivity2::class.java)
        intentToMainActivity2.putExtra("USERNAME", username)
        intentToMainActivity2.putExtra("PHONE_NUMBER", notelepon.orEmpty())
        intentToMainActivity2.putExtra("EMAIL", email.orEmpty())
        startActivity(intentToMainActivity2)
        activity?.finish()
    }
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences =
            activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("isLoggedIn", isLoggedIn)
        editor?.apply()
    }

    // Fungsi untuk mendapatkan status login dari SharedPreferences
    private fun isLoggedIn(): Boolean {
        val sharedPreferences =
            activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getBoolean("isLoggedIn", false) ?: false
    }

    // Fungsi untuk menyimpan peran pengguna yang sudah login
    private fun saveLoggedInUserRole(role: String) {
        val sharedPreferences =
            activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("userRole",role)
        editor?.apply()
    }

    // Fungsi untuk mendapatkan peran pengguna yang sudah login
    private fun getLoggedInUserRole(): String {
        val sharedPreferences =
            activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("userRole", "") ?: ""
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}