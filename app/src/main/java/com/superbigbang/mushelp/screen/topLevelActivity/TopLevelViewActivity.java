package com.superbigbang.mushelp.screen.topLevelActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;

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
    @BindView(R.id.RecyclerSetList)
    RecyclerView mRecyclerSetList;
    @BindView(R.id.RecyclerSongsList)
    RecyclerView mRecyclerSongsList;
    @BindView(R.id.buyButton)
    ImageButton buyButton;
    @BindView(R.id.newItemCircleButton)
    ImageButton newItemCircleButton;
    @BindView(R.id.volumeX2button)
    ImageButton volumeX2button;

    private static String TAG = "ItemClickActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ButterKnife.bind(this);

        mTopLevelPresenter.showAdvertistments();

        LinearLayoutManager managerSetList = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerSetList.setLayoutManager(managerSetList);
        mTopLevelPresenter.showSetLists();

        LinearLayoutManager managerSongsList = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerSongsList.setLayoutManager(managerSongsList);
        mTopLevelPresenter.showSongsLists();
    }

    @Override
    public void showSongsLists(DemoMultipleItemRvAdapter songsItemAdapter) {
        mRecyclerSongsList.setAdapter(songsItemAdapter);
        songsItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Log.d(TAG, "onItemChildClick: ");
            if (view.getId() == R.id.playPauseButton) {
                Toast.makeText(TopLevelViewActivity.this, "onItemChildClick Play" + position, Toast.LENGTH_SHORT).show();
            } else if (view.getId() == R.id.deleteSongButton) {
                Toast.makeText(TopLevelViewActivity.this, "onItemChildClick Delete" + position, Toast.LENGTH_SHORT).show();
            }

        });
        songsItemAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            Toast.makeText(TopLevelViewActivity.this, "onItemChildLongClick" + position, Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void showSetLists(DemoMultipleItemRvAdapter setListItemAdapter) {
        mRecyclerSetList.setAdapter(setListItemAdapter);
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

    @OnClick({R.id.buyButton, R.id.newItemCircleButton, R.id.volumeX2button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyButton:
                break;
            case R.id.newItemCircleButton:
                break;
            case R.id.volumeX2button:
                break;
        }
    }
}

