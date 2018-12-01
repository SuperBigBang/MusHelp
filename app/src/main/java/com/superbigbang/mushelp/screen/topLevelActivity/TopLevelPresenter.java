package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.DataServer;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

import java.util.List;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {
    private List<NormalMultipleEntity> mData;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

    void showSetLists() {
        mData = DataServer.getSetListsMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapterSet = new DemoMultipleItemRvAdapter(mData);
        getViewState().showSetLists(multipleItemAdapterSet);
    }

    void showSongsLists() {
        mData = DataServer.getSongsMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapterSongs = new DemoMultipleItemRvAdapter(mData);
        getViewState().showSongsLists(multipleItemAdapterSongs);
    }
}
