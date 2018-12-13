package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import razerdp.basepopup.BasePopupWindow;

public class EditSetListPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mSaveButton;
    private EditText mEditTextPosition;
    private EditText mEditTextSetListName;

    private TopLevelPresenter mTopLevelPresenter;

    public EditSetListPopup(Context context, String SongName, int position, TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_e_cancel);
        mSaveButton = findViewById(R.id.btn_e_Save);
        mEditTextPosition = findViewById(R.id.editSetListPosition);
        mEditTextSetListName = findViewById(R.id.editSetListName);
        mEditTextPosition.setHint(String.valueOf(position));
        mEditTextSetListName.setHint(SongName);

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

    }

    //=============================================================super methods


    @Override
    public Animator onCreateShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_edit_set_list);
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
            case R.id.btn_e_cancel:
                dismiss();
                break;
            case R.id.btn_e_Save:
                Toast.makeText(getContext(), "Сет-лист отредактирован", Toast.LENGTH_LONG).show();
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
