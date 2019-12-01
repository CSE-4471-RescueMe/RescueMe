package com.android.rescueme;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentHome extends Fragment {

    private static final String TAG = "FragmentHome";

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

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            DocumentReference docRef = db.collection("Users").document(user.getEmail()).collection("Emergency").document("contact");

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null && document.exists()) {
                            mName.setText(document.get("First Name").toString() + " " + document.get("Last Name").toString());
                            mEmail.setText(document.get("Email").toString());

                            String phone = document.get("Phone Number").toString();
                            StringBuilder formattedPhone = new StringBuilder();
                            formattedPhone.append("(")
                                    .append(phone.substring(0, 3))
                                    .append(")")
                                    .append("-")
                                    .append(phone.substring(3, 6))
                                    .append("-")
                                    .append(phone.substring(6));

                            mPhoneNumber.setText(formattedPhone.toString());
                        } else {
                            Log.w(TAG, "No such document");
                        }
                    } else {
                        Toast.makeText(getContext(), "ERROR: could not get data from Firebase database.\nPlease make sure you entered your emergency contact.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return view;
    }

    public void sendMessage(View view) {
        // placeholder since fragment_home.xml will have an error with onclick otherwise
    }

}
