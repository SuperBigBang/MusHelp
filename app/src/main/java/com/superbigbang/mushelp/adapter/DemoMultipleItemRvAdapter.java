package com.superbigbang.mushelp.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.superbigbang.mushelp.adapter.provider.SetListItemProvider;
import com.superbigbang.mushelp.adapter.provider.SongsListItemProvider;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

import java.util.List;

public class DemoMultipleItemRvAdapter extends MultipleItemRvAdapter<NormalMultipleEntity, BaseViewHolder> {

    public static final int TYPE_SET_LISTS = 100;
    public static final int TYPE_SONGS_LIST = 400;

    public DemoMultipleItemRvAdapter(@Nullable List<NormalMultipleEntity> data) {
        super(data);
        finishInitialize();
    }

    @Override
    protected int getViewType(NormalMultipleEntity entity) {
        if (entity.type == NormalMultipleEntity.SET_LISTS) {
            return TYPE_SET_LISTS;
        } else if (entity.type == NormalMultipleEntity.SONGS_LISTS) {
            return TYPE_SONGS_LIST;
        }
        return 0;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new SetListItemProvider());
        mProviderDelegate.registerProvider(new SongsListItemProvider());
    }
}