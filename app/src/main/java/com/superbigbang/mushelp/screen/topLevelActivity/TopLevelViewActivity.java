package com.superbigbang.mushelp.screen.topLevelActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.popup.BuyPopup;
import com.superbigbang.mushelp.popup.DeleteSongPopup;
import com.superbigbang.mushelp.popup.EditSetListPopup;
import com.superbigbang.mushelp.popup.EditSongPopup;
import com.superbigbang.mushelp.popup.VolumeUpPopup;

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

        mTopLevelPresenter.setVolumeUpButtonState();
    }

    @Override
    public void showSongsLists(DemoMultipleItemRvAdapter songsItemAdapter) {
        mRecyclerSongsList.setAdapter(songsItemAdapter);
        songsItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                Toast.makeText(TopLevelViewActivity.this, "onItemChildClick Play/Pause" + position, Toast.LENGTH_SHORT).show();
            } else if (view.getId() == R.id.deleteSongButton) {
                mTopLevelPresenter.showDeletePopup(position);
            }
        });
        songsItemAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            Toast.makeText(TopLevelViewActivity.this, "onItemChildLongClick Stop" + position, Toast.LENGTH_SHORT).show();
            return true;
        });
        songsItemAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            mTopLevelPresenter.showSongEditPopup(position);
            return true;
        });
    }

    @Override
    public void showSongEditPopup(String SongName, int position, int tempBpm, boolean audioIsOn, boolean countdownIsOn, String audioFile, String lyrics) {
        new EditSongPopup(this, SongName, position, tempBpm, audioIsOn, countdownIsOn, audioFile, lyrics, mTopLevelPresenter).showPopupWindow();
    }

    @Override
    public void showDeletePopup(String songname) {
        new DeleteSongPopup(this, songname, mTopLevelPresenter).showPopupWindow();
    }

    @Override
    public void showSetLists(DemoMultipleItemRvAdapter setListItemAdapter) {
        mRecyclerSetList.setAdapter(setListItemAdapter);
        setListItemAdapter.setOnItemClickListener((adapter, view, position) -> mTopLevelPresenter.changeSetList(position));
        setListItemAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            mTopLevelPresenter.showSetListEditPopup(position);
            return true;
        });
    }

    @Override
    public void showSetListEditPopup(String setListName, int position) {
        new EditSetListPopup(this, setListName, position, mTopLevelPresenter).showPopupWindow();
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
                mTopLevelPresenter.showBuyPopup();
                break;
            case R.id.newItemCircleButton:
                mTopLevelPresenter.showSongEditPopup(999);
                break;
            case R.id.volumeX2button:
                mTopLevelPresenter.showVolumeUpPopup();
                break;
        }
    }

    @Override
    public void clearStateStrategyPull() {
    }

    @Override
    public void changeSetList(String setlistname) {
        Toast.makeText(TopLevelViewActivity.this, "Выбран сет лист " + setlistname, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showVolumeUpPopup() {
        new VolumeUpPopup(this, mTopLevelPresenter).showPopupWindow();
    }

    @Override
    public void showBuyPopup() {
        new BuyPopup(this, mTopLevelPresenter).showPopupWindow();
    }

    @Override
    public void setVolumeUpButtonState(boolean VolumeUpChangeToOFF) {
        if (VolumeUpChangeToOFF) {
            volumeX2button.getDrawable().setColorFilter(
                    getResources().getColor(R.color.VolumeButtonIsOn),
                    PorterDuff.Mode.SRC_ATOP);
        } else {
            volumeX2button.getDrawable().clearColorFilter();
        }
    }
}
