package com.android.rescueme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentEmergency extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentEmergency";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPhoneNumber;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FragmentEmergency() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        view.findViewById(R.id.save_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mFirstName = view.findViewById(R.id.emergency_first_name_prompt);
        mLastName = view.findViewById(R.id.emergency_last_name_prompt);
        mEmail = view.findViewById(R.id.emergency_email_prompt);
        mPhoneNumber = view.findViewById(R.id.emergency_phone_number_prompt);

        return view;
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else if (!email.contains("@")) {
            mEmail.setError("Email does not meet requirements");
            valid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneNumber.setError("Required.");
            valid = false;
        } else if (phone.length() == 9) {
            mPhoneNumber.setError("Phone Number does not meet requirements.");
            valid = false;
        }

        return valid;
    }

    private void saveEmergencyContact(String emergencyFirstName, String emergencyLastName, String emergencyEmail, String emergencyPhoneNumber) {
        if (!validateForm()) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> emergencyContact = new HashMap<>();
        emergencyContact.put("First Name", emergencyFirstName);
        emergencyContact.put("Last Name", emergencyLastName);
        emergencyContact.put("Email", emergencyEmail);
        emergencyContact.put("Phone Number", emergencyPhoneNumber);

        if (user != null && user.getEmail() != null) {
            DocumentReference newGroupRef = db.collection("Users").document(user.getEmail()).collection("Emergency").document("contact");
            newGroupRef.set(emergencyContact).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });

            clearFields();
        }

        Intent main = new Intent(getActivity(), MainActivity.class);
        startActivity(main);
    }

    private void clearFields() {
        mFirstName.setText("");
        mLastName.setText("");
        mEmail.setText("");
        mPhoneNumber.setText("");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save_button) {
            saveEmergencyContact(mFirstName.getText().toString(), mLastName.getText().toString(), mEmail.getText().toString(), mPhoneNumber.getText().toString());
        }
    }
}
