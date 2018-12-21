package com.superbigbang.mushelp.screen.topLevelActivity;

import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
import com.superbigbang.mushelp.model.NormalMultipleEntity;
import com.superbigbang.mushelp.model.SetList;
import com.superbigbang.mushelp.model.Songs;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {

    public Realm mSetlistsrealm;
    public Realm mSongsrealm;
    private List<NormalMultipleEntity> mDataSongList;
    public static final String APP_PREFERENCES_LAST_SET_LIST_ID = "LAST_SET_LIST_ID";
    private boolean mVolumeUpIsOn_RED = false;
    private int idcurrentsetlist;

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
        SetListItemRvAdapter setListItemRvAdapter = new SetListItemRvAdapter(mSetlistsrealm.where(SetList.class).findAll());
        getViewState().showSetLists(setListItemRvAdapter);
    }

    void showSongsLists() {
        SongsItemRvAdapter songsItemRvAdapter = new SongsItemRvAdapter(mSongsrealm.where(Songs.class)
                .equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true)
                        .findFirst().getId()).findAll());
        getViewState().showSongsLists(songsItemRvAdapter);
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
        SetList lastOpened = mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst();
        SetList openlist = mSetlistsrealm.where(SetList.class).equalTo("position", position).findFirst();
        if (lastOpened.getId() != openlist.getId()) {
            mSetlistsrealm.beginTransaction();
            lastOpened.setOpen(false);
            openlist.setOpen(true);
            mSetlistsrealm.commitTransaction();
            getViewState().changeSetList(openlist.getName());
        }
    }

    void showSetListEditPopup(int position) {
        getViewState().showSetListEditPopup(mSetlistsrealm.where(SetList.class).equalTo("position", position).findFirst().getName(), position);
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

    void playButtonIsClicked(View view, int position) {
        mSongsrealm.beginTransaction();
        Songs editsong = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                .findAll()
                .where().equalTo("position", position).findFirst();
        if (editsong.isPlaystarted()) {
            editsong.setPlaystarted(false);
        } else {
            editsong.setPlaystarted(true);
        }
        mSongsrealm.commitTransaction();
    }

    @Override
    public void onDestroy() {
        mSetlistsrealm.close();
        mSongsrealm.close();
        super.onDestroy();
    }
}

