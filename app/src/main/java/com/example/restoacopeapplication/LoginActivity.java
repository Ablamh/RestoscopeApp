package com.example.restoacopeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private TextView forgotPasswordText;
    private ImageView logoImageView;
    private FirebaseAuth mAuth;
    private MaterialButton restaurateurRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInputLayout);
        passwordInput = findViewById(R.id.passwordInputLayout);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        restaurateurRegisterButton = findViewById(R.id.restaurateurRegisterButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordTextView);
        logoImageView = findViewById(R.id.logoImageView);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getEditText().getText().toString().trim();
            String password = passwordInput.getEditText().getText().toString().trim();
            handleLogin(email, password);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        restaurateurRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RestaurateurRegistrationActivity.class);
            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {
            String email = emailInput.getEditText().getText().toString().trim();
            handleForgotPassword(email);
        });
    }

    private void handleLogin(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Veuillez entrer votre email");
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Veuillez entrer votre mot de passe");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Connexion réussie", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Erreur de connexion";
                        Toast.makeText(LoginActivity.this,
                                errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleForgotPassword(String email) {
        if (email.isEmpty()) {
            emailInput.setError("Veuillez entrer votre email pour réinitialiser le mot de passe");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Instructions envoyées à votre email",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Erreur lors de l'envoi de l'email de réinitialisation",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}