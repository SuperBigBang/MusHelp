package com.superbigbang.mushelp.screen.topLevelActivity;

import android.content.SharedPreferences;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.R;
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

    public void realmsInit(SharedPreferences mSettings) {
        RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                .name("setlistsrealm.realm")
                .build();

        RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                .name("songsrealm.realm")
                .build();
        mSetlistsrealm = Realm.getInstance(setListsRealmConfig);
        if (!mSettings.contains(APP_PREFERENCES_LAST_SET_LIST_ID)) {
            idcurrentsetlist = 0;
        } else {
            idcurrentsetlist = mSettings.getInt(APP_PREFERENCES_LAST_SET_LIST_ID, 0);
        }
    }

    void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

    void showSetLists() {
        SetListItemRvAdapter setListItemRvAdapter = new SetListItemRvAdapter(mSetlistsrealm.where(SetList.class).findAll());
        SetList currentSetList = mSetlistsrealm.where(SetList.class).equalTo("id", idcurrentsetlist).findFirst();
        int currentOpenedSetPosition = currentSetList.getPosition();
        int currentOpenedSetId = currentSetList.getId();
        getViewState().showSetLists(setListItemRvAdapter, currentOpenedSetPosition, currentOpenedSetId);
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

    void changeSetList(BaseQuickAdapter adapter, View view, int position, int lastOpenSetListId, SharedPreferences.Editor editor) {
        SetList opened = mSetlistsrealm.where(SetList.class).equalTo("position", position).findFirst();
        idcurrentsetlist = opened.getId();
        editor.putInt(APP_PREFERENCES_LAST_SET_LIST_ID, idcurrentsetlist).apply();
        SetList lastOpened = mSetlistsrealm.where(SetList.class).equalTo("id", lastOpenSetListId).findFirst();
        getViewState().changeSetList(view, adapter.getViewByPosition(lastOpened.getPosition(), R.id.setListName));
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

    @Override
    public void onDestroy() {
        mSetlistsrealm.close();
        mSongsrealm.close();
        super.onDestroy();
    }
}

