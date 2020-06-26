package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapter.MessageAdapter;
import com.example.chatapp.model.Chat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatDetailActivity extends AppCompatActivity {

    @BindView(R.id.sendBtn)
    ImageButton sendBtn;
    @BindView(R.id.text_send)
    EditText text;
    @BindView(R.id.rvMessages)
    RecyclerView recyclerView;
    @BindView(R.id.upload_image)
    ImageButton imageButton;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private DatabaseReference ref;
    private String senderName;
    private String roomId;
    private String roomName;

    private MessageAdapter messageAdapter;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");

        getSupportActionBar().setTitle(roomName.toUpperCase());

        bindAdapter();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("users");

        reference = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomId);

        sendBtn.setOnClickListener(v -> submit(text.getText().toString()));

        text.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                submit(text.getText().toString());
                return true;
            }
            return false;
        });

        imageButton.setOnClickListener(v -> chooseImage());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readMessages(roomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            String image = filePath.getLastPathSegment();
            UUID uuid = UUID.randomUUID();
            String imageId = String.valueOf(uuid);
            String file = image + imageId + "." + getFileExtension(filePath);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(file);
            storageReference.putFile(filePath).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> submit(uri.toString()));
            });
        }
    }

    private String getFileExtension(Uri filePath) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(filePath));
    }

    private void submit(String msg) {
        if (!msg.trim().equals("")) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    senderName = dataSnapshot.child(firebaseUser.getUid()).child("displayName").getValue().toString();
                    sendMessage(firebaseUser.getUid(), roomId, msg, senderName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            Toast.makeText(this, "Write a message!", Toast.LENGTH_SHORT).show();
        }
        text.setText("");
    }

    private void bindAdapter() {
        messageAdapter = new MessageAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    private void sendMessage(String sender, String room, String msg, String senderName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("room", room);
        hashMap.put("message", msg);
        hashMap.put("senderName", senderName);

        databaseReference.child("rooms").child(room).child("chats").push().setValue(hashMap);
    }

    private void readMessages(String roomId) {
        reference = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomId).child("chats");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                messageAdapter.addChat(chat);
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
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
