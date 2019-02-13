package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.Songs;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import io.realm.RealmResults;
import razerdp.basepopup.BasePopupWindow;

public class EditSongPopup extends BasePopupWindow implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private Button mCancelButton;
    private Button mSaveButton;
    private ImageButton mClearFileAndLyricsButton;
    private EditText mEditTextPosition;
    private EditText mEditTextSongName;
    private EditText mEditTextTempMetronom;
    private EditText mEditTextLyrics;
    private SwitchCompat mAudioOrMetronomSwitch;
    private int currentSetListID;
    private int currentPosition;
    private String currentAudioFile;
    private boolean actionIsAddNewSong;


    private TopLevelPresenter mTopLevelPresenter;

    public EditSongPopup(Context context, String SongName, int position, int currentSetList, boolean audioIsOn, String audioFile, String lyrics, TopLevelPresenter mTopLevelPresenter, int tempBpm, boolean actionIsAddNewSongg) {
        super(context);
        mCancelButton = findViewById(R.id.btn_e_cancel2);
        mSaveButton = findViewById(R.id.btn_e_Save2);
        mClearFileAndLyricsButton = findViewById(R.id.btn_e_clearLyricsOrClearPathToAudioFile);
        mEditTextPosition = findViewById(R.id.editSongPosition);
        mEditTextSongName = findViewById(R.id.editSongName);
        mEditTextTempMetronom = findViewById(R.id.editMetronomBPM);
        mEditTextLyrics = findViewById(R.id.editSongLyrics);
        mAudioOrMetronomSwitch = findViewById(R.id.edit_audioOrMetronom_switch);
        mAudioOrMetronomSwitch.setSwitchPadding(40);
        mEditTextPosition.setText(String.valueOf(position + 1));
        mEditTextSongName.setText(SongName);
        mEditTextTempMetronom.setText(String.valueOf(tempBpm));
        mEditTextLyrics.setText(lyrics);
        mAudioOrMetronomSwitch.setChecked(audioIsOn);
        mClearFileAndLyricsButton.setImageResource(R.drawable.baseline_delete_sweep_white_48);
        currentSetListID = currentSetList;
        currentPosition = position;
        currentAudioFile = audioFile;
        actionIsAddNewSong = actionIsAddNewSongg;
        //Timber.e("on launch popupw currentaudiofile: " + currentAudioFile);

        this.mTopLevelPresenter = mTopLevelPresenter;
        setBlurBackgroundEnable(true);
        bindEvent();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return null;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return null;
    }

    private void bindEvent() {
        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mClearFileAndLyricsButton.setOnClickListener(this);
        mClearFileAndLyricsButton.setOnLongClickListener(this);
        mAudioOrMetronomSwitch.setOnCheckedChangeListener(this);
    }

    //=============================================================super methods


    @Override
    public Animator onCreateShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_edit_song);
    }

    @Override
    public Animator onCreateDismissAnimator() {
        AnimatorSet set = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            set = new AnimatorSet();
            if (mDisplayAnimateView != null) {
                set.playTogether(
                        ObjectAnimator.ofFloat(mDisplayAnimateView, "translationY", 0, 250).setDuration(400),
                        ObjectAnimator.ofFloat(mDisplayAnimateView, "alpha", 1, 0.4f).setDuration(375));
            }
        }
        return set;
    }

    @Override
    public BasePopupWindow setBlurBackgroundEnable(boolean blur) {
        return super.setBlurBackgroundEnable(blur);
    }

    //=============================================================click event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_e_cancel2:
                dismiss();
                break;
            case R.id.btn_e_Save2:
                String resultPositionEditText = mEditTextPosition.getText().toString();
                String resultNameEditText = mEditTextSongName.getText().toString();
                String resultMetronomBpm = mEditTextTempMetronom.getText().toString();
                String resultLyrics = mEditTextLyrics.getText().toString(); //Можно оставить пустым

                int valueOfResultPositionEditText;
                int valueOfResultMetronomEditText;
                int sizeOfcurrentSetlist = mTopLevelPresenter.mSongsrealm
                        .where(Songs.class)
                        .equalTo("setlistid", currentSetListID)
                        .findAll().size();
                if (resultPositionEditText.isEmpty()) {
                    Toast.makeText(getContext(), R.string.SetListAndSongNoPositionError, Toast.LENGTH_LONG).show();
                } else if (resultNameEditText.isEmpty()) {
                    Toast.makeText(getContext(), R.string.SongNoNameError, Toast.LENGTH_LONG).show();
                } else if (resultMetronomBpm.isEmpty()) {
                    Toast.makeText(getContext(), R.string.SongNoMetronomError, Toast.LENGTH_LONG).show();
                } else if (resultLyrics.isEmpty()) {
                    resultLyrics = "";
                } else {
                    valueOfResultPositionEditText = (Integer.valueOf(resultPositionEditText)) - 1;
                    valueOfResultMetronomEditText = (Integer.valueOf(resultMetronomBpm));
                    if (actionIsAddNewSong) {
                        //=============================================================Add new song
                        if (valueOfResultPositionEditText < 0 || valueOfResultPositionEditText > sizeOfcurrentSetlist) {
                            Toast.makeText(getContext(), ExtendApplication.getBaseComponent().getContext().getResources().getString(R.string.SongPosNumError) + (sizeOfcurrentSetlist + 1), Toast.LENGTH_LONG).show();
                        } else if (valueOfResultMetronomEditText <= 0 || valueOfResultMetronomEditText > 999) {
                            Toast.makeText(getContext(), R.string.SongMetronomNumError, Toast.LENGTH_LONG).show();
                        } else {
                            int newIdForSong = mTopLevelPresenter.mSongsrealm
                                    .where(Songs.class)
                                    .max("songid")
                                    .intValue()
                                    + 1;
                            if (valueOfResultPositionEditText + 1 > sizeOfcurrentSetlist) {
                                //=============================================================Adding new song to the end of current Song List
                                mTopLevelPresenter.mSongsrealm.beginTransaction();
                                Songs newSong = mTopLevelPresenter.mSongsrealm.createObject(Songs.class);
                                newSong.setTitle(resultNameEditText);
                                newSong.setSetlistid(currentSetListID);
                                newSong.setSongid(newIdForSong);
                                newSong.setPosition(valueOfResultPositionEditText);
                                newSong.setMetronombpm(valueOfResultMetronomEditText);
                                newSong.setAudioOn(mAudioOrMetronomSwitch.isChecked());
                                newSong.setLyrics(resultLyrics);
                                newSong.setLyricshasopen(false);
                                newSong.setPlaystarted(false);
                                newSong.setAudiofile(currentAudioFile);
                                mTopLevelPresenter.mSongsrealm.commitTransaction();
                            } else {
//=============================================================Inserting new song in current Song List
                                RealmResults<Songs> songsNoAutoSorting = mTopLevelPresenter.mSongsrealm
                                        .where(Songs.class)
                                        .equalTo("setlistid", currentSetListID)
                                        .findAll()
                                        .where()
                                        .between("position", valueOfResultPositionEditText, sizeOfcurrentSetlist)
                                        .findAll();
                                mTopLevelPresenter.mSongsrealm.beginTransaction();
                                int countposition1 = 0;
                                int countposition2 = 0;
                                Songs firstSong = null;
                                for (int i = 0; i <= songsNoAutoSorting.size(); i++) {
                                    if (i == 0) {
                                        firstSong = songsNoAutoSorting.where().equalTo("position", valueOfResultPositionEditText).findFirst();
                                        countposition1 = firstSong.getPosition();
                                    } else {
                                        Songs secondSong = songsNoAutoSorting.where().equalTo("position", countposition1 + 1).findFirst();
                                        countposition2 = countposition1 + 1;
                                        if (firstSong != null) {
                                            firstSong.setPosition(countposition2);
                                        }
                                        countposition1 = countposition1 + 2;
                                        firstSong = songsNoAutoSorting.where().equalTo("position", countposition2 + 1).findFirst();
                                        countposition2++;
                                        if (secondSong != null) {
                                            secondSong.setPosition(countposition2);
                                        }
                                    }
                                }
                                Songs newSong = mTopLevelPresenter.mSongsrealm.createObject(Songs.class);
                                newSong.setTitle(resultNameEditText);
                                newSong.setSetlistid(currentSetListID);
                                newSong.setSongid(newIdForSong);
                                newSong.setPosition(valueOfResultPositionEditText);
                                newSong.setMetronombpm(valueOfResultMetronomEditText);
                                newSong.setAudioOn(mAudioOrMetronomSwitch.isChecked());
                                newSong.setLyrics(resultLyrics);
                                newSong.setLyricshasopen(false);
                                newSong.setPlaystarted(false);
                                newSong.setAudiofile(currentAudioFile);
                                mTopLevelPresenter.mSongsrealm.commitTransaction();
                            }
                            Toast.makeText(getContext(), R.string.SongHasAdded, Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    }
                    //=============================================================Edit existing song
                    else {
                        if (valueOfResultPositionEditText < 0 || valueOfResultPositionEditText >= sizeOfcurrentSetlist) {
                            Toast.makeText(getContext(), ExtendApplication.getBaseComponent().getContext().getResources().getString(R.string.SongPosNumError) + (sizeOfcurrentSetlist), Toast.LENGTH_LONG).show();
                        } else if (valueOfResultMetronomEditText <= 0 || valueOfResultMetronomEditText > 300) {
                            Toast.makeText(getContext(), R.string.SongMetronomNumError, Toast.LENGTH_LONG).show();
                        } else {
                            if (currentPosition > valueOfResultPositionEditText) {
//=============================================================to Bottom
                                RealmResults<Songs> songsNoAutoSorting = mTopLevelPresenter.mSongsrealm
                                        .where(Songs.class)
                                        .equalTo("setlistid", currentSetListID)
                                        .findAll()
                                        .where()
                                        .between("position", valueOfResultPositionEditText, currentPosition)
                                        .findAll();
                                mTopLevelPresenter.mSongsrealm.beginTransaction();
                                int countposition1 = 0;
                                int countposition2 = 0;
                                Songs firstSong = null;
                                for (int i = 0; i <= songsNoAutoSorting.size(); i++) {
                                    if (i == 0) {
                                        Songs editedSong = songsNoAutoSorting.where().equalTo("position", currentPosition).findFirst();
                                        editedSong.setTitle(resultNameEditText); //применение изменений
                                        editedSong.setMetronombpm(valueOfResultMetronomEditText);
                                        editedSong.setAudioOn(mAudioOrMetronomSwitch.isChecked());
                                        editedSong.setAudiofile(currentAudioFile);
                                        editedSong.setLyrics(resultLyrics);
                                        firstSong = songsNoAutoSorting.where().equalTo("position", valueOfResultPositionEditText).findFirst();
                                        countposition1 = firstSong.getPosition();
                                        editedSong.setPosition(countposition1);
                                    } else {
                                        Songs secondSong = songsNoAutoSorting.where().equalTo("position", countposition1 + 1).findFirst();
                                        countposition2 = countposition1 + 1;
                                        if (firstSong != null) {
                                            firstSong.setPosition(countposition2);
                                        }
                                        countposition1 = countposition1 + 2;
                                        firstSong = songsNoAutoSorting.where().equalTo("position", countposition2 + 1).findFirst();
                                        countposition2++;
                                        if (secondSong != null) {
                                            secondSong.setPosition(countposition2);
                                        }
                                    }
                                }
                                mTopLevelPresenter.mSongsrealm.commitTransaction();
                            } else if (currentPosition < valueOfResultPositionEditText) {
//=============================================================to Top
                                RealmResults<Songs> songsNoAutoSorting2 = mTopLevelPresenter.mSongsrealm
                                        .where(Songs.class)
                                        .equalTo("setlistid", currentSetListID)
                                        .findAll()
                                        .where()
                                        .between("position", currentPosition, valueOfResultPositionEditText)
                                        .findAll();
                                mTopLevelPresenter.mSongsrealm.beginTransaction();
                                int countposition1 = 0;
                                int countposition2 = 0;
                                Songs firstSong = null;
                                for (int i = 0; i <= songsNoAutoSorting2.size(); i++) {
                                    if (i == 0) {
                                        Songs editedSong = songsNoAutoSorting2.where().equalTo("position", currentPosition).findFirst();
                                        editedSong.setTitle(resultNameEditText);
                                        editedSong.setMetronombpm(valueOfResultMetronomEditText);
                                        editedSong.setAudioOn(mAudioOrMetronomSwitch.isChecked());
                                        editedSong.setAudiofile(currentAudioFile);
                                        editedSong.setLyrics(resultLyrics);
                                        firstSong = songsNoAutoSorting2.where().equalTo("position", valueOfResultPositionEditText).findFirst();
                                        countposition1 = firstSong.getPosition();
                                        editedSong.setPosition(countposition1);
                                    } else {
                                        Songs secondSong = songsNoAutoSorting2.where().equalTo("position", countposition1 - 1).findFirst();
                                        countposition2 = countposition1 - 1;
                                        if (firstSong != null) {
                                            firstSong.setPosition(countposition2);
                                        }
                                        countposition1 = countposition1 - 2;
                                        firstSong = songsNoAutoSorting2.where().equalTo("position", countposition2 - 1).findFirst();
                                        countposition2--;
                                        if (secondSong != null) {
                                            secondSong.setPosition(countposition2);
                                        }
                                    }
                                }
                                mTopLevelPresenter.mSongsrealm.commitTransaction();
                            } else if (currentPosition == valueOfResultPositionEditText) {
                                //=============================================================no change position, edit only Name
                                mTopLevelPresenter.mSongsrealm.beginTransaction();
                                Songs song = mTopLevelPresenter.mSongsrealm
                                        .where(Songs.class)
                                        .equalTo("setlistid", currentSetListID)
                                        .findAll()
                                        .where()
                                        .equalTo("position", currentPosition)
                                        .findFirst();
                                song.setTitle(resultNameEditText);
                                song.setMetronombpm(valueOfResultMetronomEditText);
                                song.setAudioOn(mAudioOrMetronomSwitch.isChecked());
                                song.setAudiofile(currentAudioFile);
                                song.setLyrics(resultLyrics);
                                mTopLevelPresenter.mSongsrealm.commitTransaction();
                            }
                            Toast.makeText(getContext(), R.string.SongHasEdit, Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    }
                }
                break;
            case R.id.btn_e_clearLyricsOrClearPathToAudioFile:
                mEditTextLyrics.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.btn_e_clearLyricsOrClearPathToAudioFile) {
            currentAudioFile = null;
            Toast.makeText(getContext(), getContext().getText(R.string.delete_audio_file_path), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void dismiss() {
        mTopLevelPresenter.clearStateStrategyPull();
        mTopLevelPresenter = null;
        super.dismiss();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.edit_audioOrMetronom_switch:
                if (isChecked && currentAudioFile != null) {
                    //             Timber.e("Audio is ON, audiofile not null");
                } else if (isChecked) {
                    if (insertingPathForAudioFile()) {
                        //                Timber.e("Audio is ON, audiofile included");
                    } else mAudioOrMetronomSwitch.setChecked(false);
                }
                break;
        }
    }

    private boolean insertingPathForAudioFile() {
        Toast.makeText(getContext(), getContext().getText(R.string.add_audio_file), Toast.LENGTH_SHORT).show();
        new ChooserDialog().with(EditSongPopup.this.getContext())
                .withStartFile(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath())
                .withRowLayoutView(R.layout.li_row_textview)
                .withChosenListener((path, pathFile) -> {
                    currentAudioFile = path;
                    Toast.makeText(ExtendApplication.getBaseComponent().getContext(), getContext().getText(R.string.add_audio_file_ok) + pathFile.getName(), Toast.LENGTH_SHORT).show();
                })
                .build()
                .show();
        return currentAudioFile != null;
    }
}
