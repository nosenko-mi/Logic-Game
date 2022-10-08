package com.ltl.mpmp_lab3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ltl.mpmp_lab3.data.model.User;
import com.ltl.mpmp_lab3.databinding.ActivityLoginBinding;
import com.ltl.mpmp_lab3.databinding.ActivityRegisterBinding;
import com.ltl.mpmp_lab3.ui.login.LoginViewModel;
import com.ltl.mpmp_lab3.ui.login.LoginViewModelFactory;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private LoginViewModel loginViewModel;


    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.addTextChangedListener(afterTextChangedListener);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = createUser();
                createAccount(user);
            }
        });
    }

    private void init(){
        usernameEditText = binding.usernameEdit;
        emailEditText = binding.emailEdit;
        passwordEditText = binding.passwordEdit;
        registerButton = binding.registerButton;

        mAuth = FirebaseAuth.getInstance();

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
    }

    private void createAccount(User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("register_activity", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            UserProfileChangeRequest profileUpdates
                                    = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getDisplayName())
                                    .build();
                            user.updateProfile(profileUpdates);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("register_activity", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }
    
    private User createUser(){
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        return new User(username, email, password);
    }
}