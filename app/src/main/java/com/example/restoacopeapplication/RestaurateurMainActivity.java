package com.example.restoacopeapplication;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RestaurateurMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Vérifier si l'utilisateur est connecté
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_restaurateur_main);

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.acceuile) {
                replaceFragment(new AcceuilFragment());
                return true;
            } else if (itemId == R.id.ajouter) {
                replaceFragment(new AjouterFragment());
                return true;
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        // Charger le fragment par défaut
        replaceFragment(new AcceuilFragment());
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    // Méthode pour la déconnexion (à appeler quand nécessaire, par exemple depuis un bouton)
    public void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}