package com.example.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.display_name)
    EditText username;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.registerBtn)
    Button registerBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Create account");

        registerBtn.setOnClickListener(v -> {
            final String USERNAME = username.getText().toString();
            String EMAIL = email.getText().toString();
            String PASSWORD = password.getText().toString();

            if (!TextUtils.isEmpty(USERNAME) && !TextUtils.isEmpty(EMAIL) && !TextUtils.isEmpty(PASSWORD)) {
                register_user(USERNAME, EMAIL, PASSWORD);
            } else {
                Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register_user(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String userId = currentUser.getUid();

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("id", userId);
                        userMap.put("displayName", displayName);

                        mDatabase.setValue(userMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
