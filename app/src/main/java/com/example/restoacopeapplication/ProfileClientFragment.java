package com.example.restoacopeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ProfileClientFragment extends Fragment {
    private static final String TAG = "ProfileClientFragment";
    private View viewMode;
    private View editMode;
    private Button editButton;
    private Button saveButton;
    private Button logoutButton;

    // TextViews pour le mode lecture
    private TextView lastNameText, firstNameText, emailText;

    // EditTexts pour le mode édition
    private EditText lastNameEdit, firstNameEdit, emailEdit;

    // EditTexts pour le changement de mot de passe
    private EditText currentPasswordEdit, newPasswordEdit, confirmNewPasswordEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_client, container, false);
        initializeViews(view);
        loadUserData();
        setupButtons();
        return view;
    }

    private void initializeViews(View view) {
        // Mode lecture
        viewMode = view.findViewById(R.id.viewMode);
        lastNameText = view.findViewById(R.id.lastNameText);
        firstNameText = view.findViewById(R.id.firstNameText);
        emailText = view.findViewById(R.id.emailText);

        // Mode édition
        editMode = view.findViewById(R.id.editMode);
        lastNameEdit = view.findViewById(R.id.lastNameEdit);
        firstNameEdit = view.findViewById(R.id.firstNameEdit);
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

        Log.d(TAG, "Loading data for user: " + userId);

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Log pour déboguer
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());

                        String nom = documentSnapshot.getString("nom");
                        String prenom = documentSnapshot.getString("prenom");
                        String email = documentSnapshot.getString("email");

                        Log.d(TAG, "Nom: " + nom + ", Prenom: " + prenom + ", Email: " + email);

                        // Mode lecture
                        lastNameText.setText(nom != null ? nom : "");
                        firstNameText.setText(prenom != null ? prenom : "");
                        emailText.setText(email != null ? email : "");

                        // Mode édition
                        lastNameEdit.setText(nom != null ? nom : "");
                        firstNameEdit.setText(prenom != null ? prenom : "");
                        emailEdit.setText(email != null ? email : "");
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getContext(), "Aucune donnée trouvée pour cet utilisateur", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user data", e);
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

        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void updateProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (lastNameEdit.getText().toString().trim().isEmpty() ||
                firstNameEdit.getText().toString().trim().isEmpty() ||
                emailEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nom", lastNameEdit.getText().toString().trim());
        updates.put("prenom", firstNameEdit.getText().toString().trim());
        updates.put("email", emailEdit.getText().toString().trim());

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    loadUserData();
                    viewMode.setVisibility(View.VISIBLE);
                    editMode.setVisibility(View.GONE);
                    editButton.setVisibility(View.VISIBLE);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updateEmail(emailEdit.getText().toString().trim())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Erreur lors de la mise à jour de l'email",
                                                Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile", e);
                    Toast.makeText(getContext(), "Erreur lors de la mise à jour du profil",
                            Toast.LENGTH_SHORT).show();
                });

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
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(getContext(), "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                                    currentPasswordEdit.setText("");
                                    newPasswordEdit.setText("");
                                    confirmNewPasswordEdit.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error updating password", e);
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