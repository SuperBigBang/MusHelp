package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

}
