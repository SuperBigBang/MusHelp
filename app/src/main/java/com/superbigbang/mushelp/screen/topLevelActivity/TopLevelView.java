package com.superbigbang.mushelp.screen.topLevelActivity;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.google.android.gms.ads.AdRequest;

@StateStrategyType(OneExecutionStateStrategy.class)
public interface TopLevelView extends MvpView {

 void showAdvertistments(AdRequest adRequest);

}
