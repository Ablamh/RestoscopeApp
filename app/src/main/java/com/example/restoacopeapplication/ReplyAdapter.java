package com.example.restoacopeapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Map<String, Object>> replies;

    public ReplyAdapter(List<Map<String, Object>> replies) {
        this.replies = replies;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Map<String, Object> reply = replies.get(position);

        holder.textViewReplyUserName.setText((String) reply.get("userName"));
        holder.textViewReplyContent.setText((String) reply.get("reply"));

        // Gérer le timestamp
        Timestamp timestamp = (Timestamp) reply.get("timestamp");
        if (timestamp != null) {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                    Locale.getDefault()).format(timestamp.toDate());
            holder.textViewReplyDate.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return replies != null ? replies.size() : 0;
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReplyUserName, textViewReplyContent, textViewReplyDate;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReplyUserName = itemView.findViewById(R.id.textViewReplyUserName);
            textViewReplyContent = itemView.findViewById(R.id.textViewReplyContent);
            textViewReplyDate = itemView.findViewById(R.id.textViewReplyDate);
        }
    } }