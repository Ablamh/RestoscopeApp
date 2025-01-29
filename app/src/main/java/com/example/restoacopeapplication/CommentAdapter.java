package com.example.restoacopeapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentModel> comments = new ArrayList<>();
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CommentAdapter(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = comments.get(position);
        holder.textViewUserName.setText(comment.getUserName());
        holder.textViewComment.setText(comment.getComment());
        holder.ratingBarDisplay.setRating(comment.getRating());
        holder.textViewDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(comment.getTimestamp()));

        holder.buttonReply.setOnClickListener(v -> showReplyDialog(comment));

        setupReplies(holder.recyclerReplies, comment.getReplies());
    }

    private void setupReplies(RecyclerView recyclerView, List<Map<String, Object>> replies) {
        ReplyAdapter replyAdapter = new ReplyAdapter(replies);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(replyAdapter);
    }

    private void showReplyDialog(CommentModel comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reply, null);
        EditText editTextReply = view.findViewById(R.id.editTextReply);

        builder.setView(view)
                .setTitle("Répondre au commentaire")
                .setPositiveButton("Envoyer", (dialog, which) -> {
                    String replyText = editTextReply.getText().toString().trim();
                    if (!replyText.isEmpty()) {
                        submitReply(comment, replyText);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void submitReply(CommentModel comment, String replyText) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(context, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Log.d("CommentAdapter", "Document data: " + documentSnapshot.getData());

                            String userName;
                            String userType = documentSnapshot.getString("userType");
                            Log.d("CommentAdapter", "UserType: " + userType);

                            if ("client".equals(userType)) {
                                String prenom = documentSnapshot.getString("prenom");
                                String nom = documentSnapshot.getString("nom");
                                Log.d("CommentAdapter", "Prénom: " + prenom + ", Nom: " + nom);

                                if (prenom != null && nom != null) {
                                    userName = prenom + " " + nom;
                                } else {
                                    userName = "Utilisateur";
                                    Log.e("CommentAdapter", "Prénom ou nom est null");
                                }
                            } else if ("restaurateur".equals(userType)) {
                                userName = documentSnapshot.getString("nomRestaurant");
                                Log.d("CommentAdapter", "Nom Restaurant: " + userName);
                            } else {
                                userName = "Utilisateur";
                                Log.e("CommentAdapter", "Type utilisateur non reconnu: " + userType);
                            }

                            if (userName == null || userName.trim().isEmpty()) {
                                userName = "Utilisateur";
                                Log.e("CommentAdapter", "userName est null ou vide");
                            }

                            Map<String, Object> reply = new HashMap<>();
                            reply.put("userId", userId);
                            reply.put("userName", userName);
                            reply.put("reply", replyText);
                            reply.put("timestamp", FieldValue.serverTimestamp());

                            db.collection("comments").document(comment.getId())
                                    .update("replies", FieldValue.arrayUnion(reply))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Réponse ajoutée", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Erreur lors de l'ajout de la réponse: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        Log.e("CommentAdapter", "Erreur ajout réponse: " + e.getMessage());
                                    });

                        } catch (Exception e) {
                            Toast.makeText(context, "Erreur lors du traitement de la réponse", Toast.LENGTH_SHORT).show();
                            Log.e("CommentAdapter", "Erreur traitement réponse: " + e.getMessage());
                        }
                    } else {
                        Log.e("CommentAdapter", "Document utilisateur non trouvé");
                        Toast.makeText(context, "Erreur: utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erreur de chargement utilisateur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("CommentAdapter", "Erreur chargement utilisateur: " + e.getMessage());
                });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

   // Changez en final

    public void setComments(List<CommentModel> newComments) {
        if (newComments != null) {
            comments.clear();
            comments.addAll(newComments);
            notifyDataSetChanged();
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewComment, textViewDate;
        RatingBar ratingBarDisplay;
        Button buttonReply;
        RecyclerView recyclerReplies;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            ratingBarDisplay = itemView.findViewById(R.id.ratingBarDisplay);
            buttonReply = itemView.findViewById(R.id.buttonReply);
            recyclerReplies = itemView.findViewById(R.id.recyclerReplies);
        }
    }
}