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

import razerdp.basepopup.BasePopupWindow;

public class InputPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mCompeleteButton;
    private EditText mInputEdittext;

    public InputPopup(Context context) {
        super(context);
        mCancelButton = findViewById(R.id.btn_cancel);
        mCompeleteButton = findViewById(R.id.btn_Compelete);
        mInputEdittext = findViewById(R.id.ed_input);

        setAutoShowInputMethod(mInputEdittext, false);
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
        return createPopupById(R.layout.popup_input);
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

    //=============================================================click event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_Compelete:
                Toast.makeText(getContext(), mInputEdittext.getText().toString(), Toast.LENGTH_LONG).show();
                dismiss();
                break;
            default:
                break;
        }

    }
}
