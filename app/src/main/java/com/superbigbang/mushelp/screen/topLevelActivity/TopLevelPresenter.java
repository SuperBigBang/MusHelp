package com.superbigbang.mushelp.screen.topLevelActivity;

import android.os.Build;
import android.widget.SeekBar;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
import com.superbigbang.mushelp.model.SetList;
import com.superbigbang.mushelp.model.Songs;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {
    public static float[] speedRates = new float[]{
            1f, 0.95f, 0.90f, 0.85f, 0.80f, 0.75f, 1.05f
    };
    public Realm mSetlistsrealm;
    public Realm mSongsrealm;
    private boolean permissionsToFileStorageIsGranted;
    private boolean isPaused;
    private int currentSpeed = 0;
    private boolean countDownIsOn;

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

    void setCountDownButtonState() {
        getViewState().setCountDownButtonState(countDownIsOn);
    }

    void changeCountDownStateAndButton() {
        countDownIsOn = !countDownIsOn;
        setCountDownButtonState();
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
        if (ExtendApplication.isIsFull() || position < 2) {
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
        } else {
            getViewState().showErrorMessages(103);
        }
    }

    void showSetListEditPopup(int position) {
        if (ExtendApplication.isIsFull() || position < 2) {
            getViewState().showSetListEditPopup(mSetlistsrealm.where(SetList.class).equalTo("position", position).findFirst().getName(), position);
        } else {
            getViewState().showErrorMessages(103);
        }
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
                    editsong.getAudiofile(),
                    editsong.getLyrics(),
                    editsong.getMetronombpm(),
                    false);
        } else getViewState().showSongEditPopup("",
                0,
                mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId(),
                false,
                null,
                "",
                0,
                true);
    }

    void playButtonIsClicked(int position, boolean functionIsStop) {
        mSongsrealm.beginTransaction();
        Songs editsong = mSongsrealm.where(Songs.class).equalTo("setlistid", mSetlistsrealm.where(SetList.class).equalTo("isOpen", true).findFirst().getId())
                .findAll()
                .where().equalTo("position", position).findFirst();
        if (editsong != null) {
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
                        startPlaying(editsong.getMetronombpm(), false, editsong.isAudioOn() ? checkAudioFileIsExists(editsong.getAudiofile()) : null, countDownIsOn);
                    }
                }
            } else {
                Songs firststoppedsong = mSongsrealm.where(Songs.class).equalTo("playstarted", true).findFirst();
                if (firststoppedsong != null) {
                    firststoppedsong.setPlaystarted(false);
                    //     Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Stop playing " + firststoppedsong.getTitle() + " Start playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                    currentSpeed = 0;
                    editsong.setPlaystarted(true);
                    startPlaying(editsong.getMetronombpm(), true, editsong.isAudioOn() ? checkAudioFileIsExists(editsong.getAudiofile()) : null, countDownIsOn);
                } else {
                    //    Toast.makeText(ExtendApplication.getBaseComponent().getContext(), "Start playing " + editsong.getTitle(), Toast.LENGTH_LONG).show();
                    editsong.setPlaystarted(true);
                    startPlaying(editsong.getMetronombpm(), false, editsong.isAudioOn() ? checkAudioFileIsExists(editsong.getAudiofile()) : null, countDownIsOn);
                }
                isPaused = false;
            }
        }
        mSongsrealm.commitTransaction();
    }

    private String checkAudioFileIsExists(String audioFile) {
        Timber.e("AudioFile path: %s", audioFile);
        File fileToCheck = new File(audioFile);
        if (fileToCheck.exists()) {
            /** Дополнить проверку на тип файла!*/
            fileToCheck = null;
            return audioFile;
        } else {
            getViewState().showErrorMessages(102);
            fileToCheck = null;
            return null;
        }
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
                    ExtendApplication.getMetroComponent().getMetronomeService().setCountdownIsOn(filepath != null && countdownIsOn);
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

    void changeRate(boolean toDefault) {
        if (toDefault) {
            currentSpeed = 0;
            sendChangeRateToService(speedRates[currentSpeed]);
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

    void changeRateChangeButtonState() {
        getViewState().changeRateChangeButtonState(currentSpeed);
    }

    private void sendChangeRateToService(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ExtendApplication.getMetroComponent().getMetronomeService().changeRate(speed);
            getViewState().showMessage(200, String.valueOf(speed));
            changeRateChangeButtonState();
        } else {
            getViewState().showErrorMessages(101);
        }
    }

    void sendSeekBarOperationsToService(SeekBar seekBar) {
        ExtendApplication.getMetroComponent().getMetronomeService().setmSeekBar(seekBar);
    }
}

