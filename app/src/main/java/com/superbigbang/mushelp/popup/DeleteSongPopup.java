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
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import razerdp.basepopup.BasePopupWindow;

public class DeleteSongPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mCompeleteButton;
    private TopLevelPresenter mTopLevelPresenter;

    public DeleteSongPopup(Context context, String SongName, TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_cancel);
        mCompeleteButton = findViewById(R.id.btn_Compelete);
        TextView mSongName = findViewById(R.id.popupDelWindSongName);
        mSongName.setText(SongName);
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
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_Compelete:
                Toast.makeText(getContext(), "Трэк удалён", Toast.LENGTH_LONG).show();
                dismiss();
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
