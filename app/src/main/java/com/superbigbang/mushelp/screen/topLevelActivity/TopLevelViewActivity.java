package com.superbigbang.mushelp.screen.topLevelActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.MainViewController;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
import com.superbigbang.mushelp.billing.BillingManager;
import com.superbigbang.mushelp.billing.BillingProvider;
import com.superbigbang.mushelp.popup.BuyPopup;
import com.superbigbang.mushelp.popup.DeleteSongPopup;
import com.superbigbang.mushelp.popup.EditSetListPopup;
import com.superbigbang.mushelp.popup.EditSongPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;

public class TopLevelViewActivity extends MvpAppCompatActivity implements TopLevelView, BillingProvider {
//ca-app-pub-5364969751338385~1161013636  - идентификатор приложения в AdMob
//ca-app-pub-5364969751338385/9526465105  -  ads:adUnitId="" -LIVE (DON'T USE FOR TEST'S)
// ca-app-pub-3940256099942544/6300978111 - ads:adUnitId="" ONLY FOR TEST/

    public static final String APP_PREFERENCES = "mysettings";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
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
    @BindView(R.id.countdownChangeButton)
    ImageButton countdownChangeButton;
    @BindView(R.id.metroSoundChangeButton)
    ImageButton metroSoundChangeButton;
    @BindView(R.id.rateChangeButton)
    ImageButton rateChangeButton;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.themeChangeButton)
    ImageButton themeChangeButton;

    SharedPreferences mSettings;

    private int themeValue;
  /*  private SharedPreferences.OnSharedPreferenceChangeListener callback = (sharedPreferences, key) -> {
    };*/

    private BillingManager mBillingManager;
    private MainViewController mViewController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // Start the controller and load data
        mViewController = new MainViewController(this);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());

        //Get the theme save on sharedpreference
        themeValue = mSettings.getInt("theme", 1);
        //Theme settings
        switch (themeValue) {
            case 1:
                setTheme(R.style.Theme_1);
               /* toolbarMain.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));*/
                break;
            case 2:
                setTheme(R.style.Theme_2);
                break;
            case 3:
                setTheme(R.style.Theme_3);
                break;
        }
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.selectedTextColor, typedValue, true);
        @ColorInt int color = typedValue.data;
        ExtendApplication.currentThemeColorsTextSelected = color;
        typedValue = null;

        TypedValue typedValue2 = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.UNselectedTextColor, typedValue2, true);
        @ColorInt int color2 = typedValue2.data;
        ExtendApplication.currentThemeColorsTextUNSelected = color2;
        typedValue2 = null;

        TypedValue typedValue3 = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.UnavailableObj, typedValue3, true);
        @ColorInt int color3 = typedValue3.data;
        ExtendApplication.currentThemeColorsUnavailable = color3;
        typedValue3 = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ButterKnife.bind(this);


        if (!ExtendApplication.isIsFull()) {
            mTopLevelPresenter.showAdvertistments();
        } else {
            mAdView.setVisibility(View.GONE);
        }

        mTopLevelPresenter.realmsInit();
        LinearLayoutManager managerSetList = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerSetList.setLayoutManager(managerSetList);
        mTopLevelPresenter.showSetLists();

        LinearLayoutManager managerSongsList = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerSongsList.setLayoutManager(managerSongsList);
        mTopLevelPresenter.showSongsLists();

        mTopLevelPresenter.changeRateChangeButtonState();
        mTopLevelPresenter.setCountDownButtonState();

        if (checkAndRequestPermissions()) {
            mTopLevelPresenter.setPermissionsToFileStorageIsGranted(true);
        }

        //mSettings.registerOnSharedPreferenceChangeListener(callback);

        //Clicks
        themeChangeButton.setOnClickListener(v -> {
            themeValue++;
            if (themeValue == 4) {
                themeValue = 1;
            }
            recreate();
        });

        try {
            mTopLevelPresenter.sendSeekBarOperationsToService(mSeekBar);
        } catch (Exception e) {
            Timber.e("service not started (null)");
        }
    }

    public void showRefreshedUi() {
        updateUi();
    }

    public void checkSongAddLimitations() {
        if (mRecyclerSongsList.getAdapter().getItemCount() >= 5 && !ExtendApplication.isIsFull()) {
            newItemCircleButton.setColorFilter(ExtendApplication.currentThemeColorsUnavailable);
        } else {
            newItemCircleButton.clearColorFilter();
        }
        Timber.e("Items on list: %s", String.valueOf(mRecyclerSongsList.getAdapter().getItemCount()));
    }

    /**
     * Update UI to reflect model
     */
    @UiThread
    private void updateUi() {
        if (isPremiumPurchased()) {
            ExtendApplication.setIsFull(true);
            SharedPreferences.Editor spe = mSettings.edit();
            spe.putBoolean("Premium", true);
            spe.apply();
            Toast.makeText(this, "You On full PREMIUM!", Toast.LENGTH_LONG).show();
        } else {
            ExtendApplication.setIsFull(false);
            SharedPreferences.Editor spe = mSettings.edit();
            spe.putBoolean("Premium", false);
            spe.apply();
            Toast.makeText(this, "You On FREE demo!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return mViewController.isPremiumPurchased();
        // return true; //FOR TEST
    }

    @Override
    public void showSongsLists(SongsItemRvAdapter songsItemRvAdapter) {
        mRecyclerSongsList.setAdapter(songsItemRvAdapter);
        songsItemRvAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                mTopLevelPresenter.sendSeekBarOperationsToService(mSeekBar);
                mTopLevelPresenter.playButtonIsClicked(position, false);
            } else if (view.getId() == R.id.deleteSongButton) {
                mTopLevelPresenter.showDeletePopup(position);
            }
            if (view.getId() == R.id.songName || view.getId() == R.id.songLyrics) {
                mTopLevelPresenter.changeLyricsOpenOrCloseCondition(position);
            }

        });
        songsItemRvAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                mTopLevelPresenter.sendSeekBarOperationsToService(mSeekBar);
                mTopLevelPresenter.playButtonIsClicked(position, true);
            }
            if (view.getId() == R.id.songName) {
                mTopLevelPresenter.showSongEditPopup(position, false);
            }
            return true;
        });
        checkSongAddLimitations();
    }

    @VisibleForTesting
    public MainViewController getViewController() {
        return mViewController;
    }



    @Override
    public void showSongEditPopup(String SongName, int position, int currentSetList, boolean audioIsOn, String audioFile, String lyrics, int tempBpm, boolean actionIsAddNewSong) {
        new EditSongPopup(this, SongName, position, currentSetList, audioIsOn, audioFile, lyrics, mTopLevelPresenter, tempBpm, actionIsAddNewSong).showPopupWindow();
    }

    @Override
    public void showDeletePopup(String songname, int currentposition, int currentSetList) {
        new DeleteSongPopup(this, songname, currentposition, currentSetList, mTopLevelPresenter).showPopupWindow();
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
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Save on sharedpreference when app stop
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("theme", themeValue);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Timber.d("Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        //mSettings.unregisterOnSharedPreferenceChangeListener(callback);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @OnClick({R.id.buyButton, R.id.newItemCircleButton, R.id.countdownChangeButton, R.id.metroSoundChangeButton, R.id.rateChangeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyButton:
                mTopLevelPresenter.showBuyPopup();
                break;
            case R.id.newItemCircleButton:
                if (mRecyclerSongsList.getAdapter().getItemCount() >= 5 && !ExtendApplication.isIsFull()) {
                    showErrorMessages(104);
                } else {
                    mTopLevelPresenter.showSongEditPopup(0, true);
                }
                break;
            case R.id.countdownChangeButton:
                mTopLevelPresenter.changeCountDownStateAndButton();
                break;
            case R.id.metroSoundChangeButton:
                mTopLevelPresenter.metroSoundChangeButton();
                break;
            case R.id.rateChangeButton:
                mTopLevelPresenter.changeRate(false);
        }
    }

    @OnLongClick({R.id.rateChangeButton})
    public boolean onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.rateChangeButton:
                mTopLevelPresenter.changeRate(true);
                break;
        }
        return true;
    }

    public void changeRateChangeButtonState(int state) {
        switch (state) {
            case 0:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_0);
                break;
            case 1:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_1);
                break;
            case 2:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_2);
                break;
            case 3:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_3);
                break;
            case 4:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_4);
                break;
            case 5:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_5);
                break;
            case 6:
                rateChangeButton.setImageResource(R.drawable.baseline_change_rate_button_48_6);
                break;
        }
    }

    public void setCountDownButtonState(boolean state) {
        countdownChangeButton.setImageResource(state ? R.drawable.baseline_timer_white_48_on : R.drawable.baseline_timer_white_48_off);
    }

    @Override
    public void clearStateStrategyPull() {
    }

    @Override
    public void changeSetList(String currentSetListName) {
        mTopLevelPresenter.showSongsLists();
    }

    @Override
    public void showBuyPopup() {
        new BuyPopup(this, mTopLevelPresenter, this).showPopupWindow();
    }

    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            //   int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

        /*    if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }*/

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        String TAG = "LOG_PERMISSION";
        Timber.d("Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                /*    perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);*/
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions

                  /*  if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    )*/
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        /*     Timber.d("Phone state and storage permissions granted");*/
                        Timber.d("Storage permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        mTopLevelPresenter.setPermissionsToFileStorageIsGranted(true);
                    } else {
                        Timber.d("Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                     /*   if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
                        */
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK(
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                break;
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.alert_window_message))
                .setPositiveButton(getString(R.string.alert_window_positive_button), okListener)
                .setNegativeButton(getString(R.string.alert_window_negative_button), okListener)
                .create()
                .show();
    }

    public void showErrorMessages(int errorId) {
        switch (errorId) {
            case 100:
                Toast.makeText(this, getText(R.string.check_permissions_failed), Toast.LENGTH_SHORT).show();
                break;
            case 101:
                Toast.makeText(this, getText(R.string.rate_change_feature_error), Toast.LENGTH_LONG).show();
                break;
            case 102:
                Toast.makeText(this, getText(R.string.check_file_exist_error), Toast.LENGTH_LONG).show();
                break;
            case 103:
                Toast.makeText(this, getText(R.string.need_full_version_for_setlists_error), Toast.LENGTH_LONG).show();
                break;
            case 104:
                Toast.makeText(this, getText(R.string.need_full_version_for_addsong_error), Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void showMessage(int messageId, String additionalText) {
        switch (messageId) {
            case 200:
                Toast.makeText(this, getText(R.string.rate_change_message) + additionalText, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
