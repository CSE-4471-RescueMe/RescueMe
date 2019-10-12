package com.android.rescueme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


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

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String confirmPassword = mConfirmPasswordText.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailText.setError("Required.");
            valid = false;
        } else if (!email.contains("@")) {
            mPasswordText.setError("Email does not meet requirements");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordText.setError("Required.");
            valid = false;
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) { // Minimum eight characters, at least one letter, one number and one special character.
            mPasswordText.setError("Password does not meet requirements.");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            mPasswordText.setError("Password does not match confirm password.");
            valid = false;
        }

        return valid;
    }

    private void createAccount(String email, String password, String first, String last) {
        if (!validateForm()) {
            return;
        }

        final String e = email;
        final String f = first;
        final String l = last;

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> addUser = new HashMap<>();
                    addUser.put("Email", e);
                    addUser.put("First", f);
                    addUser.put("Last", l);

                    if (user != null && user.getEmail() != null) {
                        db.collection("users").document(user.getEmail()).set(addUser);
                        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(f + " " + l).build();
                        user.updateProfile(update);
                    }

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(RegisterActivity.this, "Sign up failed or email is already in use.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_button) {
            createAccount(mEmailText.getText().toString(), mPasswordText.getText().toString(), mFirstName.getText().toString(), mLastName.getText().toString());
        }
    }
}
