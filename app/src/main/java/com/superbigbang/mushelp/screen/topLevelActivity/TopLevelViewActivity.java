package com.superbigbang.mushelp.screen.topLevelActivity;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.superbigbang.mushelp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopLevelViewActivity extends MvpAppCompatActivity implements TopLevelView {
//ca-app-pub-5364969751338385~1161013636  - идентификатор приложения в AdMob
//ca-app-pub-5364969751338385/9526465105  -  ads:adUnitId="" -LIVE (DON'T USE FOR TEST'S)
// ca-app-pub-3940256099942544/6300978111 - ads:adUnitId="" ONLY FOR TEST

    @InjectPresenter
    TopLevelPresenter mTopLevelPresenter;

    @BindView(R.id.adView)
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ButterKnife.bind(this);

        MobileAds.initialize(this, "ca-app-pub-5364969751338385~1161013636");
        mTopLevelPresenter.showAdvertistments();

    }

    @Override
    public void showAdvertistments(AdRequest adRequest) {
        mAdView.loadAd(adRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

