package com.ltl.mpmp_lab3.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.ltl.mpmp_lab3.databinding.FragmentRegisterBinding
import com.ltl.mpmp_lab3.registration.RegistrationRequest
import java.util.*

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            val request: RegistrationRequest? = createRequest()
            createAccount(request!!)
        }
    }

    private fun createAccount(request: RegistrationRequest) {
        mAuth!!.createUserWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener{
                    task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in request's information
                    Log.d("register_activity", "createUserWithEmail:success")

                    val user = mAuth!!.currentUser!!
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(request.displayName)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {

                            Log.d("register_activity", "createUserWithEmail:success")
                            Log.d("register_activity", "Email: " + user.email)
                            Log.d("register_activity", "Display name:" + user.displayName)

                            returnToLoginActivity()
                        }


                } else {
                    // If sign in fails, display a message to the request.
                    Log.d(
                        "register_activity",
                        "createUserWithEmail:failure",
                        task.exception
                    )
                    Toast
                        .makeText(context, task.exception!!.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }

        /*.addOnCompleteListener(context,
    OnCompleteListener { task: Task<AuthResult?> ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in request's information
            Log.d("register_activity", "createUserWithEmail:success")
            val user = mAuth!!.currentUser!!
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(request.displayName)
                .build()
            user.updateProfile(profileUpdates)
                .addOnSuccessListener { returnToLoginActivity() }
            Log.d("register_activity", "createUserWithEmail:success")
            Log.d("register_activity", "Email: " + user.email)
            Log.d("register_activity", "Display name:" + user.displayName)
        } else {
            // If sign in fails, display a message to the request.
            Log.d(
                "register_activity",
                "createUserWithEmail:failure",
                task.exception
            )
            Toast
                .makeText(context, task.exception!!.localizedMessage, Toast.LENGTH_LONG)
                .show()
        }
    })*/

    }

    private fun returnToLoginActivity() {
        val action = RegisterFragmentDirections.returnToLoginFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    private fun validateData(): Boolean {
        var isValid = true
        if (binding.usernameEdit.text.toString() == "") {
            binding.usernameEdit.error = "Enter username"
            isValid = false
        }
        if (binding.emailEdit.text.toString() == "") {
            binding.emailEdit.error = "Enter username"
            isValid = false
        }
        if (binding.passwordEdit.text.toString() == "") {
            binding.passwordEdit.error = "Enter username"
            isValid = false
        }
        return isValid
    }

    private fun createRequest(): RegistrationRequest? {
        if (!validateData()) {
            return null
        }
        val username: String = binding.usernameEdit.text.toString()
        val email: String = binding.emailEdit.text.toString()
        val password: String = binding.passwordEdit.text.toString()
        return RegistrationRequest(username, email, password)
    }

}