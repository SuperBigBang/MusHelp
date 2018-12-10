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
    private List<NormalMultipleEntity> mDataSetLists;
    private List<NormalMultipleEntity> mDataSongList;
    private int currentsetlist;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

    void showSetLists() {
        mDataSetLists = DataServer.getSetListsMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapterSet = new DemoMultipleItemRvAdapter(mDataSetLists);
        getViewState().showSetLists(multipleItemAdapterSet);
    }

    void showSongsLists() {
        mDataSongList = DataServer.getSongsMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapterSongs = new DemoMultipleItemRvAdapter(mDataSongList);
        getViewState().showSongsLists(multipleItemAdapterSongs);
    }

    void showDeletePopup(int position) {
        getViewState().showDeletePopup(mDataSongList.get(position).songname);
    }

    public void clearStateStrategyPull() {
        getViewState().clearStateStrategyPull();
    }

    void changeSetList(int position) {
        getViewState().changeSetList(mDataSetLists.get(position).setlistname);
    }

    void showSetListEditPopup(int position) {
        getViewState().showSetListEditPopup(mDataSetLists.get(position).setlistname, position + 1);
    }

    void showSongEditPopup(int position) {
        getViewState().showSongEditPopup(mDataSongList.get(position).songname,
                position + 1, mDataSongList.get(position).bitrate, false, "noFile",
                mDataSongList.get(position).lyrics);
    }
}
