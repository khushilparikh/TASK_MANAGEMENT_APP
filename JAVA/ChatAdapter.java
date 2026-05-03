package com.example.taskmanagerapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatModel> list;
    String currentUser;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatModel> list, String currentUser) {
        this.list = list;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {

        ChatModel model = list.get(position);

        if (model.getSender().equals(currentUser)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_sent, parent, false);

            return new SentViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_received, parent, false);

            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatModel model = list.get(position);

        if (holder instanceof SentViewHolder) {

            SentViewHolder vh = (SentViewHolder) holder;

            vh.message.setText(model.getMessage());
            vh.time.setText(model.getTime());

            int seen = model.getSeen();

            // 🔥 STATUS LOGIC
            if (seen == 0) {
                vh.status.setText("✔");
                vh.status.setTextColor(Color.GRAY);
            }
            else if (seen == 1) {
                vh.status.setText("✔✔");
                vh.status.setTextColor(Color.GRAY);
            }
            else {
                vh.status.setText("✔✔");
                vh.status.setTextColor(Color.parseColor("#2196F3")); // 🔥 BLUE
            }

        } else {

            ReceivedViewHolder vh = (ReceivedViewHolder) holder;

            vh.message.setText(model.getMessage());
            vh.time.setText(model.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔥 SENT VIEW HOLDER
    static class SentViewHolder extends RecyclerView.ViewHolder {

        TextView message, time, status;

        public SentViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.timeText);
            status = itemView.findViewById(R.id.statusTick); // 🔥 IMPORTANT
        }
    }

    // RECEIVED VIEW HOLDER
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {

        TextView message, time;

        public ReceivedViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.timeText);
        }
    }
}