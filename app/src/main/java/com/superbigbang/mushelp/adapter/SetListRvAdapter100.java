package com.superbigbang.mushelp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.SetList;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class SetListRvAdapter100 extends RecyclerView.Adapter<SetListRvAdapter100.ViewHolder> implements RealmChangeListener {

    private final RealmResults<SetList> mSetLists;

    public SetListRvAdapter100(RealmResults<SetList> mSetLists) {
        this.mSetLists = mSetLists;
        this.mSetLists.addChangeListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_list, parent, false);
        return new ViewHolder((TextView) view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextTitle.setText(mSetLists.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mSetLists.size();
    }

    @Override
    public void onChange(Object o) {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextTitle;

        public ViewHolder(final TextView textView) {
            super(textView);
            mTextTitle = textView;
        }
    }
}
