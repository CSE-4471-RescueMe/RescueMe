package com.android.rescueme;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentSettings extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.sign_out_button).setOnClickListener(this);
        
        return view;
    }

    private void signOut() {
        mAuth.signOut();
        Intent signOut = new Intent(getActivity(), LoginActivity.class);
        startActivity(signOut);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_button:
                signOut();
        }
    }

}
