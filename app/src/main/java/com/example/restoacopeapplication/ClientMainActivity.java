package com.example.restoacopeapplication;

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
        setContentView(R.layout.activity_client_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        setupLogout();
    }

    private void setupLogout() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ClientMainActivity.this, LoginActivity.class));
            finish();
        });
    }
}