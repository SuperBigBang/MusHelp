package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.google.android.gms.ads.AdRequest;
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
import com.superbigbang.mushelp.customMoxyStrategies.ClearStateStrategy;

@StateStrategyType(OneExecutionStateStrategy.class)
public interface TopLevelView extends MvpView {

    void showAdvertistments(AdRequest adRequest);

    void showSetLists(SetListItemRvAdapter multipleItemAdapter);

    void showSongsLists(SongsItemRvAdapter songsItemRvAdapter);

    @StateStrategyType(SingleStateStrategy.class)
    void showDeletePopup(String songname, int currentposition, int currentSetList);

    @StateStrategyType(ClearStateStrategy.class)
    void clearStateStrategyPull();

    void changeSetList(String currentSetListName);

    @StateStrategyType(SingleStateStrategy.class)
    void showSetListEditPopup(String setListName, int position);

    @StateStrategyType(SingleStateStrategy.class)
    void showSongEditPopup(String SongName, int position, int tempBpm, boolean audioIsOn, boolean countdownIsOn, String audioFile, String lyrics);

    @StateStrategyType(SingleStateStrategy.class)
    void showVolumeUpPopup();

    void setVolumeUpButtonState(boolean VolumeUpIsOn_RED);

    @StateStrategyType(SingleStateStrategy.class)
    void showBuyPopup();
}
