package com.superbigbang.mushelp.screen.topLevelActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.DataServer;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopLevelViewActivity extends MvpAppCompatActivity implements TopLevelView {
//ca-app-pub-5364969751338385~1161013636  - идентификатор приложения в AdMob
//ca-app-pub-5364969751338385/9526465105  -  ads:adUnitId="" -LIVE (DON'T USE FOR TEST'S)
// ca-app-pub-3940256099942544/6300978111 - ads:adUnitId="" ONLY FOR TEST/

    @InjectPresenter
    TopLevelPresenter mTopLevelPresenter;

    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.editListPenButton)
    ImageButton editListPenButton;
    @BindView(R.id.RecyclerSetList)
    RecyclerView mRecyclerSetList;
    @BindView(R.id.RecyclerSongsList)
    RecyclerView RecyclerSongsList;
    @BindView(R.id.buyButton)
    ImageButton buyButton;
    @BindView(R.id.newItemCircleButton)
    ImageButton newItemCircleButton;
    @BindView(R.id.volumeX2button)
    ImageButton volumeX2button;

    private List<NormalMultipleEntity> mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ButterKnife.bind(this);
        mTopLevelPresenter.showAdvertistments();

        LinearLayoutManager managerSetList = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerSetList.setLayoutManager(managerSetList);
        mData = DataServer.getNormalMultipleEntities();
        DemoMultipleItemRvAdapter multipleItemAdapter = new DemoMultipleItemRvAdapter(mData);

        mRecyclerSetList.setAdapter(multipleItemAdapter);
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

    @OnClick({R.id.editListPenButton, R.id.buyButton, R.id.newItemCircleButton, R.id.volumeX2button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editListPenButton:
                break;
            case R.id.buyButton:
                break;
            case R.id.newItemCircleButton:
                break;
            case R.id.volumeX2button:
                break;
        }
    }
}

