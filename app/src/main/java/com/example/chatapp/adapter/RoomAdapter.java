package com.example.chatapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.activity.ChatDetailActivity;
import com.example.chatapp.model.Room;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {

    private List<Room> rooms = new ArrayList<>();
    private String theLastMessage;
    private String theLastSender;


    public RoomAdapter() {
    }

    @NonNull
    @Override
    public RoomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_room, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.MyViewHolder holder, int position) {
        holder.fillView(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void addRoom(Room room) {
        rooms.add(room);
        notifyItemInserted(getItemCount() - 1);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView roomname;
        TextView last_Msg;
        TextView lastSender;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            roomname = itemView.findViewById(R.id.room_name);
            last_Msg = itemView.findViewById(R.id.last_msg);
            lastSender = itemView.findViewById(R.id.user_sent_msg);
        }

        void fillView(Room room) {
            roomname.setText(room.getRoomname());
            lastMessage(room.getId(), last_Msg, lastSender);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatDetailActivity.class);
                intent.putExtra("roomId", room.getId());
                intent.putExtra("roomName", room.getRoomname());
                v.getContext().startActivity(intent);
            });
        }
    }

    private void lastMessage(String roomid, TextView last_msg, TextView last_sender) {

        theLastMessage = "default";
        Query query = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomid).child("chats").orderByKey().limitToLast(1);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                theLastMessage = dataSnapshot.child("message").getValue().toString();
                theLastSender = dataSnapshot.child("senderName").getValue().toString();
                if (theLastMessage.contains(".jpg") || theLastMessage.contains(".png")) {
                    last_msg.setText("Photo message");
                    last_sender.setText(theLastSender);
                } else if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                    last_sender.setText(theLastSender);
                }
                theLastMessage = "default";
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
