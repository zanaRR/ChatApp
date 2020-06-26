package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private static final int MESSAGE_LEFT = 0;
    private static final int MESSAGE_RIGHT = 1;

    private List<Chat> chats = new ArrayList<>();
    private StorageReference reference = FirebaseStorage.getInstance().getReference().child("images");

    public MessageAdapter() {
    }

    @NonNull
    @Override
    public MessageAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolderSender(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolderReceiver(view);
        }
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        notifyItemInserted(chats.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.VH holder, int position) {
        holder.fillView(chats.get(position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(user.getUid())) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }
    }

    abstract class VH extends RecyclerView.ViewHolder {

        VH(@NonNull View itemView) {
            super(itemView);
        }

        abstract void fillView(Chat chat);
    }

    class ViewHolderReceiver extends VH {
        private TextView show_message;
        private TextView user;
        private ImageView imageView;

        ViewHolderReceiver(View view) {
            super(view);
            show_message = view.findViewById(R.id.show_message);
            user = view.findViewById(R.id.user);
            imageView = view.findViewById(R.id.imageMessage);

        }

        @Override
        void fillView(Chat chat) {
            if (chat.getMessage().endsWith(".jpg") || chat.getMessage().endsWith(".png")) {
//                reference.child(chat.getMessage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));
                Picasso.get().load(chat.getMessage()).into(imageView);
                show_message.setVisibility(View.GONE);
            } else {
                show_message.setText(chat.getMessage());
                imageView.setVisibility(View.GONE);
            }
            user.setText(chat.getSenderName());
        }

    }


    class ViewHolderSender extends VH {
        private TextView show_message;
        private ImageView imageView;

        ViewHolderSender(View view) {
            super(view);
            show_message = view.findViewById(R.id.show_message);
            imageView = view.findViewById(R.id.imageMessage);
        }

        @Override
        void fillView(Chat chat) {
            if (chat.getMessage().contains(".jpg") || chat.getMessage().contains(".png")) {
                Picasso.get().load(chat.getMessage()).into(imageView);
//                reference.child(chat.getMessage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));
                show_message.setVisibility(View.GONE);
            } else {
                show_message.setText(chat.getMessage());
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
