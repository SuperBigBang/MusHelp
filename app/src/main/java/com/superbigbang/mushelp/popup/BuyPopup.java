package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.billing.BillingConstants;
import com.superbigbang.mushelp.billing.BillingProvider;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import razerdp.basepopup.BasePopupWindow;

public class BuyPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mBuyButton;
    private ImageButton mRateButton1;
    private ImageButton mRateButton2;
    private ImageButton mRateButton3;
    private ImageButton mRateButton4;
    private ImageButton mRateButton5;
    private TopLevelPresenter mTopLevelPresenter;
    private BillingProvider mBillingProvider;
    private TextView mFirstText;

    public BuyPopup(Context context, TopLevelPresenter mTopLevelPresenter, BillingProvider provider) {
        super(context);
        mCancelButton = findViewById(R.id.btn_PBUY_cancel);
        mBuyButton = findViewById(R.id.btn_PBUY_Accept);
        mFirstText = findViewById(R.id.textView8);
        mRateButton1 = findViewById(R.id.rateStar1);
        mRateButton2 = findViewById(R.id.rateStar2);
        mRateButton3 = findViewById(R.id.rateStar3);
        mRateButton4 = findViewById(R.id.rateStar4);
        mRateButton5 = findViewById(R.id.rateStar5);
        mBillingProvider = provider;

        this.mTopLevelPresenter = mTopLevelPresenter;
        setBlurBackgroundEnable(true);

        if (!ExtendApplication.isIsFull()) {
            mFirstText.setText(R.string.buy_popup_text);
            mBuyButton.setText(R.string.Buy);
        } else {
            mFirstText.setText(R.string.buy_popup_text_pro);
            mBuyButton.setText(R.string.buy_popup_button_donate);
            mBuyButton.setTextSize(12);
        }
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
        mRateButton1.setOnClickListener(this);
        mRateButton2.setOnClickListener(this);
        mRateButton3.setOnClickListener(this);
        mRateButton4.setOnClickListener(this);
        mRateButton5.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rateStar1:
                view.startAnimation(ExtendApplication.getAnimFadein());
                sendRateFeedbackPoor((getContext().getString(R.string.reply_rate1)) + 1 + (getContext().getString(R.string.reply_rate2)));
                break;
            case R.id.rateStar2:
                view.startAnimation(ExtendApplication.getAnimFadein());
                sendRateFeedbackPoor((getContext().getString(R.string.reply_rate1)) + 2 + (getContext().getString(R.string.reply_rate2)));
                break;
            case R.id.rateStar3:
                view.startAnimation(ExtendApplication.getAnimFadein());
                sendRateFeedbackPoor((getContext().getString(R.string.reply_rate1)) + 3 + (getContext().getString(R.string.reply_rate2)));
                break;
            case R.id.rateStar4:
                view.startAnimation(ExtendApplication.getAnimFadein());
                sendRateFeedbackNice();
                break;
            case R.id.rateStar5:
                view.startAnimation(ExtendApplication.getAnimFadein());
                sendRateFeedbackNice();
                break;
            case R.id.btn_PBUY_cancel:
                view.startAnimation(ExtendApplication.getAnimFadein());
                dismiss();
                break;
            case R.id.btn_PBUY_Accept:
                view.startAnimation(ExtendApplication.getAnimFadein());
                if (mBillingProvider.isPremiumPurchased()) {
                    mBillingProvider.getBillingManager().initiatePurchaseFlow(BillingConstants.SKU_DONATE,
                            BillingClient.SkuType.INAPP);
                } else {
                    mBillingProvider.getBillingManager().initiatePurchaseFlow(BillingConstants.SKU_PREMIUM,
                            BillingClient.SkuType.INAPP);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    private void sendRateFeedbackPoor(String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + "superbigbang@yandex.ru"));
        // intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"superbigbang@yandex.ru"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.reply_subject));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        // intent.setType("message/rfc822");
        try {
            getContext().startActivity(Intent.createChooser(intent, getContext().getString(R.string.reply_chooser)));
        } catch (Exception e) {
            Toast.makeText(ExtendApplication.getBaseComponent().getContext(),
                    R.string.no_email_aggregator_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRateFeedbackNice() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.superbigbang.mushelp"));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri
                    .parse("https://play.google.com/store/apps/details?id=com.superbigbang.mushelp"));
            if (!isActivityStarted(intent)) {
                Toast.makeText(ExtendApplication.getBaseComponent().getContext(),
                        R.string.start_intent_play_google_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isActivityStarted(Intent aIntent) {
        try {
            getContext().startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
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
