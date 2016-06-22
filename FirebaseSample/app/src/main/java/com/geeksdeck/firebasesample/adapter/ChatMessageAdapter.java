package com.geeksdeck.firebasesample.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.geeksdeck.firebasesample.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private static final String TAG = "ChatMessageAdapter";
    private final Activity activity;
    List<ChatMessage> messages = new ArrayList<>();

    public ChatMessageAdapter(Activity activity) {
        this.activity = activity;
    }

    public void addMessage(ChatMessage chat) {
        messages.add(chat);
        notifyItemInserted(messages.size());
    }


    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(activity, activity.getLayoutInflater().inflate(android.R.layout.two_line_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final Activity activity;

        TextView name, message;
        ImageView image;

        public ChatMessageViewHolder(Activity activity, View itemView) {
            super(itemView);
            this.activity = activity;
            name = (TextView) itemView.findViewById(android.R.id.text1);
            message = (TextView) itemView.findViewById(android.R.id.text2);
            image = new ImageView(activity);
            ((ViewGroup) itemView).addView(image);

        }

        //Bind data model with View
        public void bind(ChatMessage chat) {
            name.setText(chat.getName());
            //Message is an image
            if (chat.getMessage().startsWith("https://firebasestorage.googleapis.com/") || chat.getMessage().startsWith("content://")) {
                message.setVisibility(View.INVISIBLE);
                image.setVisibility(View.VISIBLE);
                Glide.with(activity)
                        .load(chat.getMessage())
                        .into(image);
            } else {
                message.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                message.setText(chat.getMessage());
            }
        }
    }
}