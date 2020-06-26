package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.R;

import com.example.chatapp.adapter.RoomAdapter;
import com.example.chatapp.model.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.add)
    Button add;
    @BindView(R.id.add_room_name)
    EditText roomName;
    @BindView(R.id.room_recycler)
    RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Chat App");

        bindAdapter();

        firebaseAuth = FirebaseAuth.getInstance();

        add.setOnClickListener(v -> {
            addRoom();
        });

        roomName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                addRoom();
                return true;
            }
            return false;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addRoom() {
        if (!roomName.getText().toString().trim().equals("")) {

            UUID uuid = UUID.randomUUID();
            String roomId = String.valueOf(uuid);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomId);

            HashMap<String, String> roomMap = new HashMap<>();
            roomMap.put("id", roomId);
            roomMap.put("roomname", roomName.getText().toString());

            reference.setValue(roomMap);
        } else {
            Toast.makeText(this, "Set a name", Toast.LENGTH_SHORT).show();
        }
        roomName.setText("");
    }

    private void bindAdapter() {
        roomAdapter = new RoomAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(roomAdapter);
    }

    private void readRooms() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Room room = dataSnapshot.getValue(Room.class);
                roomAdapter.addRoom(room);
                recyclerView.smoothScrollToPosition(roomAdapter.getItemCount());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, StartActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
