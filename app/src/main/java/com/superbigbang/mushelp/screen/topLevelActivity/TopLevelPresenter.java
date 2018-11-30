package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.DataServer;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

import java.util.List;


@InjectViewState
public class TopLevelPresenter extends MvpPresenter<TopLevelView> {
    private List<NormalMultipleEntity> mData;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void showAdvertistments() {
        getViewState().showAdvertistments(new AdRequest.Builder().build());
    }

    public void showSetLists() {
        mData = DataServer.getNormalMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapter = new DemoMultipleItemRvAdapter(mData);
        getViewState().showSetLists(multipleItemAdapter);
    }


}
