package com.superbigbang.mushelp.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.superbigbang.mushelp.adapter.provider.SetListItemProvider;
import com.superbigbang.mushelp.model.SetList;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class SetListItemRvAdapter extends MultipleItemRvAdapter<SetList, BaseViewHolder> implements RealmChangeListener {

    public static final int TYPE_SET_LISTS = 100;
    private RealmResults<SetList> mSetLists;

    public SetListItemRvAdapter(RealmResults<SetList> mSetLists) {
        super(mSetLists.sort("position"));
        this.mSetLists = mSetLists;
        this.mSetLists.addChangeListener(this);
        finishInitialize();
    }

    @Override
    protected int getViewType(SetList entity) {
        return TYPE_SET_LISTS;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new SetListItemProvider());
    }

    @Override
    public void onChange(Object o) {
        notifyDataSetChanged();
    }
}