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
    private boolean mVolumeUpIsOn_RED = false;

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
        if (position != 999) {
            getViewState().showSongEditPopup(mDataSongList.get(position).songname,
                    position, mDataSongList.get(position).bitrate, false, "noFile",
                    mDataSongList.get(position).lyrics);
        } else getViewState().showSongEditPopup("", 0, 0, false, "", "");
    }

    public void showVolumeUpPopup() {
        if (!mVolumeUpIsOn_RED) getViewState().showVolumeUpPopup();
        else changeVolumeUpButtonState();
    }

    public void changeVolumeUpButtonState() {
        //On is Blue (false), Off is Red (true)
        if (!mVolumeUpIsOn_RED) {
            mVolumeUpIsOn_RED = true;
            getViewState().setVolumeUpButtonState(mVolumeUpIsOn_RED);
        } else {
            mVolumeUpIsOn_RED = false;
            getViewState().setVolumeUpButtonState(mVolumeUpIsOn_RED);
        }
    }

    void setVolumeUpButtonState() {
        getViewState().setVolumeUpButtonState(mVolumeUpIsOn_RED);
    }
}

