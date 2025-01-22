package com.example.restoacopeapplication;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialiser Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Définir le layout par défaut d'abord
        setContentView(R.layout.activity_main);
        setupLogout();
        // Vérifier si l'utilisateur est déjà connecté
        if (mAuth.getCurrentUser() == null) {
            // Utilisateur non connecté, rediriger vers LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


    }
    private void setupLogout() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}