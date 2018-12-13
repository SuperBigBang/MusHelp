package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import razerdp.basepopup.BasePopupWindow;

public class BuyPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mBuyButton;
    private TopLevelPresenter mTopLevelPresenter;

    public BuyPopup(Context context, TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_PBUY_cancel);
        mBuyButton = findViewById(R.id.btn_PBUY_Accept);

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
        mBuyButton.setOnClickListener(this);
    }

    //=============================================================super methods


    @Override
    public Animator onCreateShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_buy);
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
            case R.id.btn_PBUY_cancel:
                dismiss();
                break;
            case R.id.btn_PBUY_Accept:
                Toast.makeText(getContext(), "Переход на Google Play для покупки", Toast.LENGTH_LONG).show();
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
