package com.superbigbang.mushelp.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.superbigbang.mushelp.adapter.provider.SongsListItemProvider;
import com.superbigbang.mushelp.model.Songs;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class SongsItemRvAdapter extends MultipleItemRvAdapter<Songs, BaseViewHolder> implements RealmChangeListener {

    public static final int TYPE_SONG_LIST = 400;
    private final RealmResults<Songs> mSongsList;

    public SongsItemRvAdapter(RealmResults<Songs> mSongsList) {
        super(mSongsList.sort("position"));
        this.mSongsList = mSongsList;
        this.mSongsList.addChangeListener(this);
        finishInitialize();
    }

    @Override
    protected int getViewType(Songs entity) {
        return TYPE_SONG_LIST;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new SongsListItemProvider());
    }

    @Override
    public void onChange(Object o) {
        notifyDataSetChanged();
    }
}