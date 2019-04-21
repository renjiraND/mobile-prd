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

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


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

    private Users curr_user;

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
        // View
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        // View Variable
        mUserImage = (ImageView) rootView.findViewById(R.id.user_image);
        mUserName = (TextView) rootView.findViewById(R.id.user_name);
        mUserEmail = (TextView) rootView.findViewById(R.id.user_email);
        mUserRFID = (TextView) rootView.findViewById(R.id.user_rfidcode);
        mUserStatus = (TextView) rootView.findViewById(R.id.user_status);

        // Userdata
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // RFID
        curr_user = new Users( currentUser.getEmail(), "RFID belum terdaftar" );
        final DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = dbRefUsers.orderByChild("email").equalTo(currentUser.getEmail());

        Log.e("Query listener:",currentUser.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Query listener:", "Ada user data snapshot");
                    for  (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Users curr_user = userSnapshot.getValue(Users.class);
                        mUserName.setText(currentUser.getDisplayName());
                        mUserEmail.setText(currentUser.getEmail());
                        if (curr_user.getRfid() == ""){
                            mUserRFID.setText("RFID belum terdaftar");
                        } else {
                            mUserRFID.setText(curr_user.getRfid());
                        }
                        mUserStatus.setText("Pemegang Akses");
                    }
                } else {
                    Log.d("Query listener:", "Tidak ada user data snapshot");
                    curr_user = new Users( currentUser.getEmail(), "RFID tidak terdaftar" );
                    mUserName.setText(currentUser.getDisplayName());
                    mUserEmail.setText(currentUser.getEmail());
                    mUserRFID.setText(curr_user.getRfid());
                    mUserStatus.setText("Bukan Pemegang Akses");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return rootView;
    }

}
