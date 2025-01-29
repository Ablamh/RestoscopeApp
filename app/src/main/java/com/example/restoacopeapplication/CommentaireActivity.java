package com.example.restoacopeapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentaireActivity extends AppCompatActivity {
    private RecyclerView recyclerComments;
    private EditText editTextComment;
    private RatingBar ratingBar;
    private Button buttonSubmitComment;
    private CommentAdapter commentAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_commentaire);

        restaurantId = getIntent().getStringExtra("restaurantId");
        if (restaurantId == null) {
            Toast.makeText(this, "Erreur: restaurant non identifié", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupFirebase();
        setupRecyclerView();
        loadComments();
        setupSubmitButton();
    }

    private void initializeViews() {
        recyclerComments = findViewById(R.id.recyclerComments);
        editTextComment = findViewById(R.id.editTextComment);
        ratingBar = findViewById(R.id.ratingBar);
        buttonSubmitComment = findViewById(R.id.buttonSubmitComment);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        recyclerComments.setHasFixedSize(true);
        recyclerComments.setItemAnimator(null);  // Désactive les animations
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);  // Pour afficher les plus récents en haut

        recyclerComments.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(this);
        recyclerComments.setAdapter(commentAdapter);

        // Vérification de l'initialisation
        if (recyclerComments.getAdapter() == null) {
            Log.e("CommentaireActivity", "Adapter not set properly");
        }
    }

    private void loadComments() {
        try {
            db.collection("comments")
                    .whereEqualTo("restaurantId", restaurantId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("CommentaireActivity", "Error loading comments:", error);
                            return;
                        }

                        List<CommentModel> comments = new ArrayList<>();
                        if (value != null) {
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                try {
                                    CommentModel comment = new CommentModel();
                                    comment.setId(doc.getId());
                                    comment.setUserId(doc.getString("userId"));
                                    comment.setUserName(doc.getString("userName"));
                                    comment.setComment(doc.getString("comment"));

                                    Double rating = doc.getDouble("rating");
                                    comment.setRating(rating != null ? rating.floatValue() : 0f);

                                    Timestamp timestamp = doc.getTimestamp("timestamp");
                                    comment.setTimestamp(timestamp != null ? timestamp.toDate() : new Date());

                                    @SuppressWarnings("unchecked")
                                    List<Map<String, Object>> replies = (List<Map<String, Object>>) doc.get("replies");
                                    comment.setReplies(replies != null ? replies : new ArrayList<>());

                                    comments.add(comment);
                                } catch (Exception e) {
                                    Log.e("CommentaireActivity", "Error parsing comment:", e);
                                }
                            }
                        }

                        // Mettre à jour l'UI sur le thread principal
                        runOnUiThread(() -> {
                            if (commentAdapter != null) {
                                commentAdapter.setComments(comments);
                                recyclerComments.scrollToPosition(0);
                            }
                        });
                    });
        } catch (Exception e) {
            Log.e("CommentaireActivity", "Error in loadComments:", e);
        }
    }

    private void setupSubmitButton() {
        buttonSubmitComment.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
                return;
            }

            String commentText = editTextComment.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (commentText.isEmpty()) {
                editTextComment.setError("Veuillez écrire un commentaire");
                return;
            }

            if (rating == 0) {
                Toast.makeText(this, "Veuillez donner une note", Toast.LENGTH_SHORT).show();
                return;
            }

            submitComment(commentText, rating);
        });
    }

    private void submitComment(String commentText, float rating) {
        try {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();
            buttonSubmitComment.setEnabled(false);

            // Récupération des informations de l'utilisateur
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Récupérer le type d'utilisateur
                            String userType = documentSnapshot.getString("userType");
                            String userName;

                            // Gestion différente selon le type d'utilisateur
                            if ("client".equals(userType)) {
                                String prenom = documentSnapshot.getString("prenom");
                                String nom = documentSnapshot.getString("nom");

                                if (prenom != null && !prenom.trim().isEmpty() &&
                                        nom != null && !nom.trim().isEmpty()) {
                                    userName = prenom.trim() + " " + nom.trim();
                                } else {
                                    userName = "Utilisateur Anonyme";
                                    Log.w("CommentaireActivity", "Nom ou prénom manquant");
                                }
                            } else if ("restaurateur".equals(userType)) {
                                String nomRestaurant = documentSnapshot.getString("nomRestaurant");
                                userName = (nomRestaurant != null && !nomRestaurant.trim().isEmpty())
                                        ? nomRestaurant.trim()
                                        : "Restaurant Anonyme";
                            } else {
                                userName = "Utilisateur Anonyme";
                                Log.w("CommentaireActivity", "Type utilisateur non reconnu: " + userType);
                            }

                            // Création du commentaire
                            Map<String, Object> comment = new HashMap<>();
                            comment.put("userId", userId);
                            comment.put("userName", userName);
                            comment.put("restaurantId", restaurantId);
                            comment.put("comment", commentText);
                            comment.put("rating", rating);
                            comment.put("timestamp", FieldValue.serverTimestamp());
                            comment.put("replies", new ArrayList<>());

                            // Ajout du commentaire dans Firestore
                            db.collection("comments")
                                    .add(comment)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(CommentaireActivity.this,
                                                "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show();
                                        editTextComment.setText("");
                                        ratingBar.setRating(0);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("CommentaireActivity", "Erreur ajout commentaire: " + e.getMessage());
                                        Toast.makeText(CommentaireActivity.this,
                                                "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnCompleteListener(task -> buttonSubmitComment.setEnabled(true));
                        } else {
                            Log.e("CommentaireActivity", "Document utilisateur non trouvé");
                            Toast.makeText(CommentaireActivity.this,
                                    "Erreur: utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                            buttonSubmitComment.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CommentaireActivity", "Erreur récupération utilisateur: " + e.getMessage());
                        Toast.makeText(this, "Erreur lors de la récupération des données utilisateur",
                                Toast.LENGTH_SHORT).show();
                        buttonSubmitComment.setEnabled(true);
                    });
        } catch (Exception e) {
            Log.e("CommentaireActivity", "Erreur générale: " + e.getMessage());
            Toast.makeText(this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
            buttonSubmitComment.setEnabled(true);
        }
    }
}