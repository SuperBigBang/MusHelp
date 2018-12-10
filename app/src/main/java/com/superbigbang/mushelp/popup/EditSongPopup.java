package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import razerdp.basepopup.BasePopupWindow;

public class EditSongPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mSaveButton;
    private ImageButton mLoadAudioFromFileButton;
    private EditText mEditTextPosition;
    private EditText mEditTextSongName;
    private EditText mEditTextTempMetronom;
    private EditText mEditTextLyrics;
    private SwitchCompat mAudioOrMetronomSwitch;

    private boolean forTestButton;


    private TopLevelPresenter mTopLevelPresenter;

    public EditSongPopup(Context context, String SongName, int position, int tempBpm,
                         boolean audioIsOn, String audioFile, String lyrics,
                         TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_e_cancel2);
        mSaveButton = findViewById(R.id.btn_e_Save2);
        mLoadAudioFromFileButton = findViewById(R.id.btn_e_loadAudioFromFile);
        mEditTextPosition = findViewById(R.id.editSongPosition);
        mEditTextSongName = findViewById(R.id.editSongName);
        mEditTextTempMetronom = findViewById(R.id.editMetronomBPM);
        mEditTextLyrics = findViewById(R.id.editSongLyrics);
        mAudioOrMetronomSwitch = findViewById(R.id.edit_audioOrMetronom_switch);
        mEditTextPosition.setText(String.valueOf(position));
        mEditTextSongName.setText(SongName);
        mEditTextTempMetronom.setText(String.valueOf(tempBpm));
        mEditTextLyrics.setText(lyrics);
        mAudioOrMetronomSwitch.setChecked(audioIsOn);
        if (!audioFile.isEmpty()) {
            mLoadAudioFromFileButton.setImageResource(R.drawable.baseline_delete_sweep_white_48);
            forTestButton = true;
        } else {
            mLoadAudioFromFileButton.setImageResource(R.drawable.baseline_save_alt_white_48);
            forTestButton = false;
        }

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
        mLoadAudioFromFileButton.setOnClickListener(this);
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
                        ObjectAnimator.ofFloat(mDisplayAnimateView, "alpha", 1, 0.4f).setDuration(250 * 3 / 2));
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
                Toast.makeText(getContext(), "Трэк отредактирован", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.btn_e_loadAudioFromFile:
                if (forTestButton) {
                    mLoadAudioFromFileButton.setImageResource(R.drawable.baseline_delete_sweep_white_48);
                    forTestButton = false;
                } else {
                    mLoadAudioFromFileButton.setImageResource(R.drawable.baseline_save_alt_white_48);
                    forTestButton = true;
                }
                break;
            case R.id.edit_audioOrMetronom_switch:
                if (mAudioOrMetronomSwitch.isChecked()) {
                    mAudioOrMetronomSwitch.setChecked(false);
                } else {
                    mAudioOrMetronomSwitch.setChecked(true);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void dismiss() {
        mTopLevelPresenter.clearStateStrategyPull();
        mTopLevelPresenter = null;
        super.dismiss();
    }
}
