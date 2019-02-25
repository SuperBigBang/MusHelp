/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.superbigbang.mushelp;

import com.android.billingclient.api.Purchase;
import com.superbigbang.mushelp.billing.BillingConstants;
import com.superbigbang.mushelp.billing.BillingManager;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelViewActivity;

import java.util.List;

import timber.log.Timber;


/**
 * Handles control logic of the BaseGamePlayActivity
 */
public class MainViewController {
    private static final String TAG = "MainViewController";

    private final UpdateListener mUpdateListener;
    private TopLevelViewActivity mActivity;

    // Tracks if we currently own a premium
    private boolean mIsPremium;

    public MainViewController(TopLevelViewActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isPremiumPurchased() {
        return mIsPremium;
    }

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            //    mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case BillingConstants.SKU_PREMIUM:
                        Timber.d("You are Premium! Congratulations!!!");
                        mIsPremium = true;
                        break;
                }
            }

            mActivity.showRefreshedUi();
        }

        @Override
        public void onConsumeFinished(String token, int result) {
        }
    }
/*
    /**
     * Save current tank level to disc
     *
     * Note: In a real application, we recommend you save data in a secure way to
     * prevent tampering.
     * For simplicity in this sample, we simply store the data using a
     * SharedPreferences.

    private void saveData() {
        SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();
        spe.putInt("tank", mTank);
        spe.apply();
        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
    }

    private void loadData() {
        SharedPreferences sp = mActivity.getPreferences(MODE_PRIVATE);
        mTank = sp.getInt("tank", 2);
        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
    }
    */
}