package com.renrairah.bukalock;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private ImageView mUserImage;
    private TextView mUserName;
    private TextView mUserEmail;
    private TextView mUserStatus;
    private TextView mUserRFID;


    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Insert set content here
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //userdata
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        Log.e("MESSAGE:",currentUser.getDisplayName());
        Log.e("MESSAGE:",currentUser.getEmail());

        // Insert set content here
        mUserImage = (ImageView) rootView.findViewById(R.id.user_image);
        mUserName = (TextView) rootView.findViewById(R.id.user_name);
        mUserEmail = (TextView) rootView.findViewById(R.id.user_email);
        //mUserImage.setImageResource(R.drawable.ic_profile);
        mUserName.setText(currentUser.getDisplayName());
        mUserEmail.setText(currentUser.getEmail());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

}
