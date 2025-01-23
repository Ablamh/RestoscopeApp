package com.example.restoacopeapplication;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RestaurateurRegistrationActivity extends AppCompatActivity {
    private TextInputEditText restaurantNameInput, addressInput, phoneInput,
            emailInput, passwordInput, confirmPasswordInput;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurateur_registration);

        // Initialize views
        restaurantNameInput = findViewById(R.id.restaurantNameInput);
        addressInput = findViewById(R.id.addressInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String restaurantName = restaurantNameInput.getText().toString();
            String address = addressInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (validateInputs(restaurantName, address, phone, email, password, confirmPassword)) {
                handleRegistration(email, password, restaurantName, address, phone);
            }
        });
    }

    private boolean validateInputs(String restaurantName, String address, String phone,
                                   String email, String password, String confirmPassword) {
        if (restaurantName.isEmpty() || address.isEmpty() || phone.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void handleRegistration(String email, String password, String restaurantName,
                                    String address, String phone) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Map<String, Object> restaurateurData = new HashMap<>();
                    restaurateurData.put("restaurantName", restaurantName);
                    restaurateurData.put("address", address);
                    restaurateurData.put("phone", phone);
                    restaurateurData.put("email", email);
                    restaurateurData.put("userType", "restaurateur");

                    FirebaseFirestore.getInstance()
                            .collection("users")  // Changé de "restaurateurs" à "users"
                            .document(uid)
                            .set(restaurateurData)
                            .addOnSuccessListener(v -> {
                                startActivity(new Intent(this, RestaurateurMainActivity.class)); // Changé de LoginActivity
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erreur d'inscription: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur d'authentification: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

}
