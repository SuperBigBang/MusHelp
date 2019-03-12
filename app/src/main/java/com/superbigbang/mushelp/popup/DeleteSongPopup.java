package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.Songs;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import io.realm.RealmResults;
import razerdp.basepopup.BasePopupWindow;

public class DeleteSongPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mCompeleteButton;
    private TopLevelPresenter mTopLevelPresenter;
    private int currentPosition;
    private int currentSetList;

    public DeleteSongPopup(Context context, String SongName, int currentPosition, int currentSetList, TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_cancel);
        mCompeleteButton = findViewById(R.id.btn_Compelete);
        TextView mSongName = findViewById(R.id.popupDelWindSongName);
        mSongName.setText(SongName);
        this.mTopLevelPresenter = mTopLevelPresenter;
        this.currentPosition = currentPosition;
        this.currentSetList = currentSetList;
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
        mCompeleteButton.setOnClickListener(this);

    }

    //=============================================================super methods


    @Override
    public Animator onCreateShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_delete);
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
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_Compelete:
                //================================================================Delete processing:
                RealmResults<Songs> songsNoAutoSorting2 = mTopLevelPresenter.mSongsrealm
                        .where(Songs.class)
                        .equalTo("setlistid", currentSetList)
                        .findAll().where()
                        .greaterThanOrEqualTo("position", currentPosition)
                        .findAll();
                mTopLevelPresenter.mSongsrealm.beginTransaction();
                for (int i = 0; i <= songsNoAutoSorting2.size(); i++) {
                    if (i == 0) {
                        Songs deletedSong = songsNoAutoSorting2.where().equalTo("position", currentPosition).findFirst();
                        deletedSong.deleteFromRealm();
                    }
                    Songs nextSong = songsNoAutoSorting2.where().equalTo("position", currentPosition + 1 + i).findFirst();
                    if (nextSong != null) {
                        nextSong.setPosition(currentPosition + i);
                    }
                }
                mTopLevelPresenter.mSongsrealm.commitTransaction();
                Toast.makeText(getContext(), "Трэк удалён", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            default:
                break;
        }

    }

    @Override
    public void dismiss() {
        if (mTopLevelPresenter != null) {
            mTopLevelPresenter.clearPopupWindowRef();
            mTopLevelPresenter.clearStateStrategyPull();
            mTopLevelPresenter = null;
        }
        super.dismiss();
    }

    @Override
    public void dismissWithOutAnimate() {
        if (mTopLevelPresenter != null) {
            mTopLevelPresenter.clearPopupWindowRef();
            mTopLevelPresenter = null;
        }
        super.dismissWithOutAnimate();
    }
}
