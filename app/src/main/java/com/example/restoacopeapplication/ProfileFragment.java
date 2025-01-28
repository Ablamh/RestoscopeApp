package com.example.restoacopeapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private View viewMode;
    private View editMode;
    private Button editButton;
    private Button saveButton;
    private Button logoutButton;

    // TextViews pour le mode lecture
    private TextView restaurantNameText, addressText, phoneText, emailText;

    // EditTexts pour le mode édition
    private EditText restaurantNameEdit, addressEdit, phoneEdit, emailEdit;

    // EditTexts pour le changement de mot de passe
    private EditText currentPasswordEdit, newPasswordEdit, confirmNewPasswordEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        loadUserData();
        setupButtons();
        return view;
    }

    private void initializeViews(View view) {
        // Mode lecture
        viewMode = view.findViewById(R.id.viewMode);
        restaurantNameText = view.findViewById(R.id.restaurantNameText);
        addressText = view.findViewById(R.id.addressText);
        phoneText = view.findViewById(R.id.phoneText);
        emailText = view.findViewById(R.id.emailText);

        // Mode édition
        editMode = view.findViewById(R.id.editMode);
        restaurantNameEdit = view.findViewById(R.id.restaurantNameEdit);
        addressEdit = view.findViewById(R.id.addressEdit);
        phoneEdit = view.findViewById(R.id.phoneEdit);
        emailEdit = view.findViewById(R.id.emailEdit);

        // Champs de mot de passe
        currentPasswordEdit = view.findViewById(R.id.currentPasswordEdit);
        newPasswordEdit = view.findViewById(R.id.newPasswordEdit);
        confirmNewPasswordEdit = view.findViewById(R.id.confirmNewPasswordEdit);

        // Boutons
        editButton = view.findViewById(R.id.editButton);
        saveButton = view.findViewById(R.id.saveButton);
        logoutButton = view.findViewById(R.id.logoutButton);
    }

    private void loadUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String restaurantName = documentSnapshot.getString("restaurantName");
                        String address = documentSnapshot.getString("address");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");

                        // Mode lecture
                        restaurantNameText.setText(restaurantName);
                        addressText.setText(address);
                        phoneText.setText(phone);
                        emailText.setText(email);

                        // Mode édition
                        restaurantNameEdit.setText(restaurantName);
                        addressEdit.setText(address);
                        phoneEdit.setText(phone);
                        emailEdit.setText(email);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupButtons() {
        editButton.setOnClickListener(v -> {
            viewMode.setVisibility(View.GONE);
            editMode.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        saveButton.setOnClickListener(v -> updateProfile());

        logoutButton.setOnClickListener(v -> {
            if (getActivity() instanceof RestaurateurMainActivity) {
                ((RestaurateurMainActivity) getActivity()).logout();
            }
        });
    }

    private void updateProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Vérifier si les champs obligatoires sont remplis
        if (restaurantNameEdit.getText().toString().trim().isEmpty() ||
                addressEdit.getText().toString().trim().isEmpty() ||
                phoneEdit.getText().toString().trim().isEmpty() ||
                emailEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("restaurantName", restaurantNameEdit.getText().toString().trim());
        updates.put("address", addressEdit.getText().toString().trim());
        updates.put("phone", phoneEdit.getText().toString().trim());
        updates.put("email", emailEdit.getText().toString().trim());

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    loadUserData();
                    viewMode.setVisibility(View.VISIBLE);
                    editMode.setVisibility(View.GONE);
                    editButton.setVisibility(View.VISIBLE);

                    // Changer aussi l'email dans Firebase Auth
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updateEmail(emailEdit.getText().toString().trim())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Erreur lors de la mise à jour de l'email",
                                                Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors de la mise à jour du profil",
                            Toast.LENGTH_SHORT).show();
                });

        // Si un nouveau mot de passe a été saisi, le mettre à jour
        if (!currentPasswordEdit.getText().toString().isEmpty() &&
                !newPasswordEdit.getText().toString().isEmpty() &&
                !confirmNewPasswordEdit.getText().toString().isEmpty()) {
            handlePasswordChange();
        }
    }

    private void handlePasswordChange() {
        String currentPassword = currentPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String confirmNewPassword = confirmNewPasswordEdit.getText().toString();

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(getContext(), "Les nouveaux mots de passe ne correspondent pas",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Réauthentifier l'utilisateur avant de changer le mot de passe
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Réauthentification réussie, on peut changer le mot de passe
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(getContext(), "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                                    // Vider les champs de mot de passe
                                    currentPasswordEdit.setText("");
                                    newPasswordEdit.setText("");
                                    confirmNewPasswordEdit.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Erreur lors du changement de mot de passe : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Mot de passe actuel incorrect", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}