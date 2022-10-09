package com.ltl.mpmp_lab3.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ltl.mpmp_lab3.Constants;
import com.ltl.mpmp_lab3.MainActivity;
import com.ltl.mpmp_lab3.R;
import com.ltl.mpmp_lab3.RegisterActivity;
import com.ltl.mpmp_lab3.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    private TextView registerTextView;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private SignInButton signInGoogle;
    private ProgressBar loadingProgressBar;
    private SwitchCompat emailSwitch;
    private RadioGroup radioGroup;
    private RadioButton normalRadioButton, hardRadioButton;

//    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private final DatabaseReference databaseReference = database.getReference("server/saving-data/game");
//    private DatabaseReference usersReference;
    private GoogleSignInAccount account;

    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                            Log.d("login_activity", "Google sign succeed");
                        } catch (ApiException e) {
                            Log.w("login_activity", "Google sign in failed", e);
                        }
                        handleSignInResult(task);
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        init();

        // will be necessary in future
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    // Exist! Do whatever.
//                } else {
//                    // Don't exist! Do something.
//                    if (account == null)
//                        return;
////                    User user = createUser(account);
////                    databaseReference.child("newUser").setValue(user);
////                    DatabaseReference usersRef = databaseReference.child("users");
////                    usersRef.child("alanisawesome").setValue(user);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed, how to handle?
//            }
//        });

        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            updateUiWithUser();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.web_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser();
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                firebaseAuthWithEmail();
            }
        });

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            }
        });

        emailSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(Constants.IS_EMAIL_ENABLED_EXTRA, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", emailSwitch.isChecked());
                editor.apply();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        registerTextView = binding.registerTextView;
        usernameEditText = binding.usernameEdit;
        passwordEditText = binding.passwordEdit;
        loginButton = binding.loginButton;
        signInGoogle = binding.signInGoogle;
        loadingProgressBar = binding.loading;

        radioGroup = binding.radioGroup;
        normalRadioButton = binding.normalRadioButton;
        hardRadioButton = binding.hardRadioButton;

        emailSwitch = binding.sendEmailSwitch;

        SharedPreferences settings = getSharedPreferences(Constants.IS_EMAIL_ENABLED_EXTRA, 0);
        boolean switchState = settings.getBoolean("switchkey", false);
        emailSwitch.setChecked(switchState);

        mAuth = FirebaseAuth.getInstance();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
//                    databaseReference.push().setValue(user);
                    updateUiWithUser();
                    })
                .addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }

    private void firebaseAuthWithEmail() {
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.equals("") || password.equals("")){
            Toast.makeText(getApplicationContext(), "Bad credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, authResult -> {
                    updateUiWithUser();
                    })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Log.d("login_activity", e.toString());
                })
                .addOnCanceledListener(this, new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d("login_activity", "login canceled");
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, change activity.
            updateUiWithUser();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignIn", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void updateUiWithUser() {
        int penalty = getPenalty();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("penalty", penalty);
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private int getPenalty(){
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        int penalty = 0;
        if (checkedRadioButtonId == -1) {
            penalty = 1;
        } else if (checkedRadioButtonId == normalRadioButton.getId()){
            penalty = 1;
        } else if (checkedRadioButtonId == hardRadioButton.getId()){
            penalty = 5;
        }

        return penalty;
    }
}