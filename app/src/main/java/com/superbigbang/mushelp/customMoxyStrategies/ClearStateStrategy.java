package com.superbigbang.mushelp.customMoxyStrategies;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.ViewCommand;
import com.arellomobile.mvp.viewstate.strategy.StateStrategy;

import java.util.List;

public class ClearStateStrategy implements StateStrategy {
    @Override
    public <View extends MvpView> void beforeApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
        currentState.clear();
    }

    @Override
    public <View extends MvpView> void afterApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
        // pass
    }
}