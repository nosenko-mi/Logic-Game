package com.ltl.mpmp_lab3.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ltl.mpmp_lab3.AppConfig
import com.ltl.mpmp_lab3.databinding.FragmentLoginBinding
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var account: GoogleSignInAccount
    private lateinit var mAuth: FirebaseAuth

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var googleSignInLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
                Log.d("login_activity", "Google sign succeed")
            } catch (e: ApiException) {
                Log.w("login_activity", "Google sign in failed", e)
            }
            handleSignInResult(task)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            updateUiWithUser()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Objects.requireNonNull(AppConfig.getConfigValue(context, "web_client_id")))
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.loginButton.setOnClickListener {
            // loadingProgressBar.setVisibility(View.VISIBLE);
            firebaseAuthWithEmail()
        }

        binding.signInGoogle.setOnClickListener{
            val signInIntent = mGoogleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.registerTextView.setOnClickListener{
            goToRegister()
        }
    }


    private fun updateUiWithUser() {
        val action = LoginFragmentDirections.goToGameFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {
//                databaseReference.push().setValue(user);
                updateUiWithUser()
//                Toast.makeText(context, "Authentication succeed.",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Authentication failed.",Toast.LENGTH_SHORT).show()
            }
    }

    private fun firebaseAuthWithEmail() {
        val email: String = binding.usernameEdit.text.toString()
        val password: String = binding.passwordEdit.text.toString()
        if (email == "" || password == "") {
            Toast.makeText(context, "Bad credentials", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                updateUiWithUser()
//                Toast.makeText(context, "Authentication succeed.",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{ e: Exception ->
                Toast.makeText(context, "Authentication failed.",Toast.LENGTH_SHORT).show()
                Log.d("login_activity", e.toString())
            }
            .addOnCanceledListener{
                Log.d("login_activity", "login canceled")
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, change activity.
            updateUiWithUser()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignIn", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun goToRegister() {
        val action = LoginFragmentDirections.goToRegisterFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }



}