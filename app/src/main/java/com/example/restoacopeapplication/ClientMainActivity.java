package com.example.restoacopeapplication;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
public class ClientMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        setupLogout();
        setupNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new AcceuilClientFragment())
                    .commit();
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.acceuile) {
                selectedFragment = new AcceuilClientFragment();
            } else if (item.getItemId() == R.id.find) {
                selectedFragment = new RechercheFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileClientFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void setupLogout() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ClientMainActivity.this, LoginActivity.class));
            finish();
        });
    }
}