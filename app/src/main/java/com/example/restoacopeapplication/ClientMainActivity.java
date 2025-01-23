package com.example.restoacopeapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
public class ClientMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_client_main);
        setupLogout();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(ClientMainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void setupLogout() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ClientMainActivity.this, LoginActivity.class));
            finish();
        });
    }
}