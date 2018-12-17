package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.model.DataServer;
import com.superbigbang.mushelp.model.NormalMultipleEntity;
import com.superbigbang.mushelp.model.SetList;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {

    Realm mSetlistsrealm;
    private List<NormalMultipleEntity> mDataSongList;
    private int currentsetlist;
    private boolean mVolumeUpIsOn_RED = false;
    Realm mSongsrealm;
    private List<SetList> mDataSetLists;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }


    public void realmsInit() {
        RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                .name("setlistsrealm.realm")
                .build();

        RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                .name("songsrealm.realm")
                .build();
        mSetlistsrealm = Realm.getInstance(setListsRealmConfig);
        mSongsrealm = Realm.getInstance(songsRealmConfig);
    }

    void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

    void showSetLists() {
        //  mDataSetLists = DataServer.getSetListsMultipleEntities();
        SetListItemRvAdapter setListItemRvAdapter = new SetListItemRvAdapter(mSetlistsrealm.where(SetList.class).findAll());
        getViewState().showSetLists(setListItemRvAdapter);
    }

    void showSongsLists() {
        mDataSongList = DataServer.getSongsMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapterSongs = new DemoMultipleItemRvAdapter(mDataSongList);
        getViewState().showSongsLists(multipleItemAdapterSongs);
    }

    void showDeletePopup(int position) {
        getViewState().showDeletePopup(mDataSongList.get(position).songname);
    }

    void showBuyPopup() {
        getViewState().showBuyPopup();
    }

    public void clearStateStrategyPull() {
        getViewState().clearStateStrategyPull();
    }

    void changeSetList(int position) {
        getViewState().changeSetList(mSetlistsrealm.where(SetList.class).equalTo("position", position).findFirst().getName());
    }

    void showSetListEditPopup(int position) {
        getViewState().showSetListEditPopup(mDataSetLists.get(position).getName(), position + 1);
    }

    void showSongEditPopup(int position) {
        if (position != 999) {
            getViewState().showSongEditPopup(mDataSongList.get(position).songname,
                    mDataSongList.get(position).position, mDataSongList.get(position).bitrate,
                    mDataSongList.get(position).audioIsOn,
                    mDataSongList.get(position).countdownIsOn,
                    mDataSongList.get(position).audioFile,
                    mDataSongList.get(position).lyrics);
        } else getViewState().showSongEditPopup("", 0, 0, false, false, "", "");
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

    @Override
    public void onDestroy() {
        mSetlistsrealm.close();
        mSongsrealm.close();
        super.onDestroy();
    }
}

