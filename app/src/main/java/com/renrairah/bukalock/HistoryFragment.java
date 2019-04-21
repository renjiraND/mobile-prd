package com.renrairah.bukalock;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryListAdapter adapter;
    private LinkedList<History> historyList;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyList = new LinkedList<>();
        adapter = new HistoryListAdapter(getActivity(), historyList);
        recyclerView.setAdapter(adapter);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        Query queryToGetData = dbRef.child("users")
                .orderByChild("email").equalTo(email);
        final ProgressDialog Dialog = new ProgressDialog(getActivity());
        Dialog.setMessage("Please wait...");
        Dialog.show();
        queryToGetData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    createListData();
                }
                Dialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }

    private void createListData() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("history");
        Log.d(TAG, "loading history");
        final ProgressDialog Dialog = new ProgressDialog(getActivity());
        Dialog.setMessage("Please wait...");
        Dialog.show();
        Query historyQuery = dbRef.orderByChild("date");
        historyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        History history = messageSnapshot.getValue(History.class);
                        Log.d(TAG, "got history " + history.dateToString());
                        historyList.add(history);
                        adapter.notifyDataSetChanged();
                    }
                    Dialog.hide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                History history = dataSnapshot.getValue(History.class);
                Log.d(TAG, "got history " + history.dateToString());
                historyList.add(history);
                adapter.notifyDataSetChanged();
                Dialog.hide();
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
        adapter.notifyDataSetChanged();
    }
}
