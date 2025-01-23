package com.example.restoacopeapplication;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restoacopeapplication.databinding.ActivityRegistrationBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;
public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configuration du bouton d'inscription
        binding.registerButton.setOnClickListener(view -> {
            String email = binding.emailInputLayout.getEditText().getText().toString().trim();
            String password = binding.passwordInputLayout.getEditText().getText().toString().trim();
            String nom = binding.nameInputLayout.getEditText().getText().toString().trim();
            String prenom = binding.firstNameInputLayout.getEditText().getText().toString().trim();
            String confirmPassword = binding.confirmPasswordInputLayout.getEditText().getText().toString().trim();

            // Validation des champs
            if (validateFields(email, password, confirmPassword, nom, prenom)) {
                registerUser(email, password);
            }
        });
    }

    private boolean validateFields(String email, String password, String confirmPassword,
                                   String nom, String prenom) {
        boolean isValid = true;

        if (nom.isEmpty()) {
            binding.nameInputLayout.setError("Veuillez entrer votre nom");
            isValid = false;
        } else {
            binding.nameInputLayout.setError(null);
        }

        if (prenom.isEmpty()) {
            binding.firstNameInputLayout.setError("Veuillez entrer votre prénom");
            isValid = false;
        } else {
            binding.firstNameInputLayout.setError(null);
        }

        if (email.isEmpty()) {
            binding.emailInputLayout.setError("Veuillez entrer votre email");
            isValid = false;
        } else {
            binding.emailInputLayout.setError(null);
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.setError("Veuillez entrer un mot de passe");
            isValid = false;
        } else if (password.length() < 6) {
            binding.passwordInputLayout.setError("Le mot de passe doit contenir au moins 6 caractères");
            isValid = false;
        } else {
            binding.passwordInputLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError("Les mots de passe ne correspondent pas");
            isValid = false;
        } else {
            binding.confirmPasswordInputLayout.setError(null);
        }

        return isValid;
    }

    private void registerUser(String email, String password) {
        Log.d(TAG, "Starting registration process...");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Log.d(TAG, "User created with ID: " + userId);

                        Map<String, Object> user = new HashMap<>();
                        user.put("nom", binding.nameInputLayout.getEditText().getText().toString());
                        user.put("prenom", binding.firstNameInputLayout.getEditText().getText().toString());
                        user.put("email", email);
                        user.put("userType", "client");

                        Log.d(TAG, "Starting Firestore document creation...");
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User document created successfully");
                                    Toast.makeText(RegistrationActivity.this,
                                            "Inscription réussie", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegistrationActivity.this, ClientMainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Document creation error: " + e.getMessage());
                                    Toast.makeText(RegistrationActivity.this,
                                            "Erreur lors de la création du profil: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Exception e = task.getException();
                        String errorMessage = e != null ? e.getMessage() : "Erreur inconnue";
                        Log.e(TAG, "Registration error: ", e);
                        Toast.makeText(RegistrationActivity.this,
                                "Erreur d'inscription: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}