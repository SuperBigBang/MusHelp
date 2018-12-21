package com.superbigbang.mushelp.screen.topLevelActivity;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
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

    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;
//    SharedPreferences.Editor mSettingsEditor;
    //   int lastOpenSetListPosition;
    //   int lastOpenSetListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ButterKnife.bind(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mTopLevelPresenter.showAdvertistments();

        mTopLevelPresenter.realmsInit();
//        mSettingsEditor = mSettings.edit();
        LinearLayoutManager managerSetList = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerSetList.setLayoutManager(managerSetList);
        mTopLevelPresenter.showSetLists();

        LinearLayoutManager managerSongsList = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerSongsList.setLayoutManager(managerSongsList);
        mTopLevelPresenter.showSongsLists();

        mTopLevelPresenter.setVolumeUpButtonState();
    }

    @Override
    public void showSongsLists(SongsItemRvAdapter songsItemRvAdapter) {
        mRecyclerSongsList.setAdapter(songsItemRvAdapter);
        songsItemRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                mTopLevelPresenter.playButtonIsClicked(position, false);
            } else if (view.getId() == R.id.deleteSongButton) {
                mTopLevelPresenter.showDeletePopup(position);
            }
            if (view.getId() == R.id.songName) {
                if (adapter.getViewByPosition(mRecyclerSongsList, position, R.id.songLyrics).getVisibility() == View.VISIBLE) {
                    adapter.getViewByPosition(mRecyclerSongsList, position, R.id.songLyrics).setVisibility(View.GONE);
                } else {
                    adapter.getViewByPosition(mRecyclerSongsList, position, R.id.songLyrics).setVisibility(View.VISIBLE);
                }
            }

        });
        songsItemRvAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                mTopLevelPresenter.playButtonIsClicked(position, true);
            }
            if (view.getId() == R.id.songName) {
                mTopLevelPresenter.showSongEditPopup(position);
            }
            return true;
        });
    }

    @Override
    public void showSongEditPopup(String SongName, int position, int tempBpm, boolean audioIsOn, boolean countdownIsOn, String audioFile, String lyrics) {
        new EditSongPopup(this, SongName, position, tempBpm, audioIsOn, countdownIsOn, audioFile, lyrics, mTopLevelPresenter).showPopupWindow();
        Toast.makeText(this, "Действия в этом окне на данный момент в разработке", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeletePopup(String songname) {
        new DeleteSongPopup(this, songname, mTopLevelPresenter).showPopupWindow();
    }

    @Override
    public void showSetLists(SetListItemRvAdapter setListItemAdapter) {
        mRecyclerSetList.setAdapter(setListItemAdapter);

        setListItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            mTopLevelPresenter.changeSetList(position);
        });
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
    public void changeSetList(String currentSetListName) {
        mTopLevelPresenter.showSongsLists();
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
