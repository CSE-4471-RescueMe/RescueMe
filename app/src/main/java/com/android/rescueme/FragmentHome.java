package com.android.rescueme;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentHome extends Fragment {

    private TextView mName;
    private TextView mEmail;
    private TextView mPhoneNumber;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FragmentHome() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mName = view.findViewById(R.id.contact_name);
        mEmail = view.findViewById(R.id.contact_email);
        mPhoneNumber = view.findViewById(R.id.contact_phone_number);

        return view;
    }

}
