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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ltl.mpmp_lab3.AppConfig
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.databinding.FragmentLoginBinding
import com.ltl.mpmp_lab3.user.UserModel
import com.ltl.mpmp_lab3.user.UserRepository
import com.ltl.mpmp_lab3.user.UserViewModel
import com.ltl.mpmp_lab3.utility.DatabaseCallback
import java.util.*

class LoginFragment : Fragment() {

    private val TAG: String = "login_fragment"

    private val userRepository: UserRepository = UserRepository()
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.my_nav)

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

                val userModel = UserModel(account.displayName.toString(), account.email.toString(), 0)
                userRepository.addUserIfNotExist(userModel)

                firebaseAuthWithGoogle(account)
                Log.d(TAG, "Google sign succeed")
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
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
            Log.d(TAG, currentUser.email.toString())

            val userModel = UserModel(currentUser.displayName.toString(), currentUser.email.toString(), 0)
            userRepository.addUserIfNotExist(userModel)

            updateUiWithUser(userModel)
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

    private fun updateUiWithUser(user: UserModel) {

//      it should look like: userViewModel.set(findByEmail()) then navigate
//      however firestore fetches data asynchronously so you can't use return statement
        userRepository.findByEmail1(user.email, userViewModel!!)

        val action = LoginFragmentDirections.goToGameFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {

                val userModel = UserModel(
                    acct.displayName!!,
                    acct.email!!,
                    0
                )

                updateUiWithUser(userModel)
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
                val userModel = UserModel(account.displayName.toString(), account.email.toString(), 0)
                userRepository.addUserIfNotExist(userModel)

                updateUiWithUser(userModel)
            }
            .addOnFailureListener{ e: Exception ->
                Toast.makeText(context, "Authentication failed.",Toast.LENGTH_SHORT).show()
                Log.d(TAG, e.toString())
            }
            .addOnCanceledListener{
                Log.d(TAG, "login canceled")
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, change activity.

            val userModel = UserModel(
                account.displayName!!,
                account.email!!,
                0
            )

            updateUiWithUser(userModel)
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