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

    void clearCurrentOpenedPopupWindow();

    void checkSongAddLimitations();

    void showErrorMessages(int errorID);

    void changeRateChangeButtonState(int state);

    void setCountDownButtonState(boolean state);

    void showMessage(int messageId, String additionalText);

    @StateStrategyType(SingleStateStrategy.class)
    void showDeletePopup(String songname, int currentposition, int currentSetList);

    @StateStrategyType(ClearStateStrategy.class)
    void clearStateStrategyPull();

    void changeSetList(String currentSetListName);

    @StateStrategyType(SingleStateStrategy.class)
    void showSetListEditPopup(String setListName, int position);

    @StateStrategyType(SingleStateStrategy.class)
    void showSongEditPopup(String SongName, int position, int currentSetList, boolean audioIsOn, String audioFile, String lyrics, int tempBpm, boolean actionIsAddNewSong);

    @StateStrategyType(SingleStateStrategy.class)
    void showBuyPopup();
}
