package com.android.rescueme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirstName = findViewById(R.id.first_name_prompt);
        mLastName = findViewById(R.id.last_name_prompt);
        mEmailText = findViewById(R.id.register_email_prompt);
        mPasswordText = findViewById(R.id.register_password_prompt);
        mConfirmPasswordText = findViewById(R.id.register_confirm_password_prompt);

        findViewById(R.id.sign_up_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_button) {

        }
    }
}
