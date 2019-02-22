package com.renrairah.bukalock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    private final LinkedList<History> mHistoryList;
    private LayoutInflater mInflater;

    public HistoryListAdapter(Context context, LinkedList<History> historyList) {
        mInflater = LayoutInflater.from(context);
        this.mHistoryList = historyList;
    }

    @NonNull
    @Override
    public HistoryListAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListAdapter.HistoryViewHolder holder, int position) {
        History mCurrent = mHistoryList.get(position);
        holder.setDetails(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        public final ImageView unlockTypeImage;
        public final TextView txtDate, txtStatus;
        final HistoryListAdapter mAdapter;

        public HistoryViewHolder(View itemView, HistoryListAdapter adapter) {
            super(itemView);
            unlockTypeImage = (ImageView) itemView.findViewById(R.id.imgUnlock);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            this.mAdapter = adapter;
        }

        public void setDetails(History history){
            if (history.getUnlockType() == 1){
                unlockTypeImage.setImageResource(R.drawable.ic_key);
            } else if (history.getUnlockType() == 2){
                unlockTypeImage.setImageResource(R.drawable.ic_game);
            } else if (history.getUnlockType() == 3){
                unlockTypeImage.setImageResource(R.drawable.ic_card);
            }
            txtDate.setText(history.dateToString());
            txtStatus.setText(history.statusToString());
        }
    }
}
