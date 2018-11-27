package com.superbigbang.mushelp.screen.splash;

import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelViewActivity;

import butterknife.ButterKnife;

public class SplashActivity extends MvpAppCompatActivity implements SplashView {

    @InjectPresenter
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        //getMvpDelegate().onAttach();
        startTopActivity();

    }

    @Override
    public void startTopActivity() {
        setContentView(R.layout.splashlauncher);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, TopLevelViewActivity.class));
    }
}