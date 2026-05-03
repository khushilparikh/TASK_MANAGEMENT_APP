package com.example.taskmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    List<ChatListModel> list;
    Context context;

    public ChatListAdapter(List<ChatListModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_chat_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatListModel model = list.get(position);

        holder.name.setText(model.getUser());
        holder.message.setText(model.getLastMessage());
        holder.time.setText(model.getTime());

        // 🔴 UNREAD COUNT
        if (model.getUnread() > 0) {
            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(String.valueOf(model.getUnread()));
        } else {
            holder.unread.setVisibility(View.GONE);
        }

        // 👤 PROFILE IMAGE (INITIAL)
        holder.avatar.setText(model.getUser().substring(0,1).toUpperCase());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("user", model.getUser());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, message, time, unread, avatar;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chatUser);
            message = itemView.findViewById(R.id.chatMessage);
            time = itemView.findViewById(R.id.chatTime);
            unread = itemView.findViewById(R.id.unreadCount);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}