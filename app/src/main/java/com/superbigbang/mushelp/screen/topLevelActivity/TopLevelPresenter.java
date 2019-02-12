package com.superbigbang.mushelp.screen.topLevelActivity;

import android.os.Build;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.ExtendApplication;
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
    private boolean permissionsToFileStorageIsGranted;
    private boolean isPaused;
    public static float[] speedRates = new float[]{
            1f, 0.95f, 0.90f, 0.85f, 0.80f, 0.75f, 1.05f
    };
    private int currentSpeed = 0;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void realmsInit() {
        if (mSetlistsrealm == null || mSetlistsrealm.isClosed()) {
            RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                    .name("setlistsrealm.realm")
                    .build();

            RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                    .name("songsrealm.realm")
                    .build();
            mSetlistsrealm = Realm.getInstance(setListsRealmConfig);
            mSongsrealm = Realm.getInstance(songsRealmConfig);
            Songs editsong = mSongsrealm.where(Songs.class).equalTo("playstarted", true).findFirst();
            if (editsong != null) {
                mSongsrealm.beginTransaction();
                editsong.setPlaystarted(false);
                mSongsrealm.commitTransaction();
            }
            Songs forCloseLyricsEditSong;
            while ((forCloseLyricsEditSong = mSongsrealm.where(Songs.class).equalTo("lyricshasopen", true).findFirst()) != null) {
                mSongsrealm.beginTransaction();
                forCloseLyricsEditSong.setLyricshasopen(false);
                mSongsrealm.commitTransaction();
            }
        }
    }

    public void setPermissionsToFileStorageIsGranted(boolean permissionsToFileStorageIsGranted) {
        this.permissionsToFileStorageIsGranted = permissionsToFileStorageIsGranted;
    }

    void changeLyricsOpenOrCloseCondition(int position) {
        Songs editSong = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                .findAll()
                .where().equalTo("position", position)
                .findFirst();
        mSongsrealm.beginTransaction();
        if (editSong != null) {
            if (editSong.isLyricshasopen()) {
                editSong.setLyricshasopen(false);
            } else {
                editSong.setLyricshasopen(true);
            }
        }
        mSongsrealm.commitTransaction();
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
        Songs edit = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                .findAll()
                .where().equalTo("position", position)
                .findFirst();
        getViewState().showDeletePopup(edit.getTitle(), position, edit.getSetlistid());
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
            Songs editsong = mSongsrealm.where(Songs.class).equalTo("playstarted", true).findFirst();
            if (editsong != null) {
                mSongsrealm.beginTransaction();
                editsong.setPlaystarted(false);
                mSongsrealm.commitTransaction();
                stopTrueOrPauseFalsePlaying(true);
            }
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

    void showSongEditPopup(int position, boolean actionIsAddNewSong) {
        if (!actionIsAddNewSong) {
            Songs editsong = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                    .findAll()
                    .where().equalTo("position", position).findFirst();
            getViewState().showSongEditPopup(editsong.getTitle(),
                    editsong.getPosition(),
                    editsong.getSetlistid(),
                    editsong.isAudioOn(),
                    editsong.isCountdownOn(),
                    editsong.getAudiofile(),
                    editsong.getLyrics(),
                    editsong.getMetronombpm(),
                    false);
        } else getViewState().showSongEditPopup("",
                0,
                mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId(),
                false,
                false,
                null,
                "",
                0,
                true);
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

    void playButtonIsClicked(int position, boolean functionIsStop) {
        mSongsrealm.beginTransaction();
        Songs editsong = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                .findAll()
                .where().equalTo("position", position).findFirst();
        if (editsong.isPlaystarted()) {
            if (functionIsStop) {
                //     Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Stop playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                currentSpeed = 0;
                stopTrueOrPauseFalsePlaying(true);
                editsong.setPlaystarted(false);
                isPaused = false;
            } else {
                if (!isPaused) {
                    //        Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Pause playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                    stopTrueOrPauseFalsePlaying(false);
                    isPaused = true;
                    editsong.setPlaystarted(false);
                } else {
                    //       Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Start playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                    editsong.setPlaystarted(true);
                    startPlaying(editsong.getMetronombpm(), false, editsong.isAudioOn() ? editsong.getAudiofile() : null, editsong.isCountdownOn());
                }

            }

        } else {
            Songs firststoppedsong = mSongsrealm.where(Songs.class).equalTo("playstarted", true).findFirst();
            if (firststoppedsong != null) {
                firststoppedsong.setPlaystarted(false);
                //     Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Stop playing " + firststoppedsong.getTitle() + " Start playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                currentSpeed = 0;
                editsong.setPlaystarted(true);
                startPlaying(editsong.getMetronombpm(), true, editsong.isAudioOn() ? editsong.getAudiofile() : null, editsong.isCountdownOn());
            } else {
                //    Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Start playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                editsong.setPlaystarted(true);
                startPlaying(editsong.getMetronombpm(), false, editsong.isAudioOn() ? editsong.getAudiofile() : null, editsong.isCountdownOn());
            }
            isPaused = false;
        }
        mSongsrealm.commitTransaction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTrueOrPauseFalsePlaying(true);
        mSetlistsrealm.close();
        mSongsrealm.close();
        ExtendApplication.getMetroComponent().getMetronomeService().stopSelf();
    }

    void metroSoundChangeButton() {
        ExtendApplication.getMetroComponent().getMetronomeService().changeTickSound();
    }

    private void startPlaying(int bpm, boolean stopOneStartTwo, String filepath, boolean countdownIsOn) {
        if (!stopOneStartTwo) {
            if (ExtendApplication.isBound()) {
                if (!ExtendApplication.getMetroComponent().getMetronomeService().isPlaying()) {
                    ExtendApplication.getMetroComponent().getMetronomeService().setBpm(bpm);
                    if (filepath == null) {
                        ExtendApplication.getMetroComponent().getMetronomeService().setFilePathOfCurrentAudio(null);
                        ExtendApplication.getMetroComponent().getMetronomeService().setCountdownIsOn(false);
                    } else if (permissionsToFileStorageIsGranted) {
                        ExtendApplication.getMetroComponent().getMetronomeService().setFilePathOfCurrentAudio(filepath);
                        ExtendApplication.getMetroComponent().getMetronomeService().setCountdownIsOn(countdownIsOn);
                    } else {
                        getViewState().showErrorMessages(100);
                    }
                    ExtendApplication.getMetroComponent().getMetronomeService().play();
                } else {
                    ExtendApplication.getMetroComponent().getMetronomeService().play();
                }
            }
        } else {
            if (ExtendApplication.isBound()) {
                if (ExtendApplication.getMetroComponent().getMetronomeService().isPlaying()) {
                    stopTrueOrPauseFalsePlaying(true);
                }
                ExtendApplication.getMetroComponent().getMetronomeService().setBpm(bpm);
                ExtendApplication.getMetroComponent().getMetronomeService().setFilePathOfCurrentAudio(filepath);
                ExtendApplication.getMetroComponent().getMetronomeService().setCountdownIsOn(filepath != null && countdownIsOn);
                ExtendApplication.getMetroComponent().getMetronomeService().play();
            }
        }
    }

    private void stopTrueOrPauseFalsePlaying(boolean stop) {
        if (stop) {
            if (ExtendApplication.isBound()) {
                ExtendApplication.getMetroComponent().getMetronomeService().stop();
            }
        } else {
            if (ExtendApplication.isBound()) {
                if (ExtendApplication.getMetroComponent().getMetronomeService().isPlaying()) {
                    ExtendApplication.getMetroComponent().getMetronomeService().pause();
                }
            }
        }
    }

    public void changeRate(boolean toDefault) {
            if (toDefault) {
                sendChangeRateToService(speedRates[0]);
            } else {
                if (currentSpeed + 1 == speedRates.length) {
                    currentSpeed = 0;
                    sendChangeRateToService(speedRates[currentSpeed]);
                } else {
                    currentSpeed++;
                    sendChangeRateToService(speedRates[currentSpeed]);
                }
            }
    }

    private void sendChangeRateToService(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ExtendApplication.getMetroComponent().getMetronomeService().changeRate(speed);
            getViewState().showMessage(200, String.valueOf(speed));
        } else {
            getViewState().showErrorMessages(101);
        }
    }
}

