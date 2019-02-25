package com.renrairah.bukalock;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Date;
import java.util.LinkedList;


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
        createListData();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void createListData() {
        Date date = new Date();
        History history = new History(2,date,1);
        historyList.add(history);
        history = new History(1,date,0);
        historyList.add(history);
        history = new History(2,date,1);
        historyList.add(history);
        history = new History(1,date,1);
        historyList.add(history);
        history = new History(1,date,0);
        historyList.add(history);
        history = new History(3,date,1);
        historyList.add(history);
        adapter.notifyDataSetChanged();
    }
}
