package com.superbigbang.mushelp.screen.topLevelActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.superbigbang.mushelp.adapter.SetListItemRvAdapter;
import com.superbigbang.mushelp.adapter.SongsItemRvAdapter;
import com.superbigbang.mushelp.popup.BuyPopup;
import com.superbigbang.mushelp.popup.DeleteSongPopup;
import com.superbigbang.mushelp.popup.EditSetListPopup;
import com.superbigbang.mushelp.popup.EditSongPopup;
import com.superbigbang.mushelp.popup.VolumeUpPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Build.VERSION.SDK_INT;

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
    @BindView(R.id.metroSoundChangeButton)
    ImageButton metroSoundChangeButton;

    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
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

        if (checkAndRequestPermissions()) {
            mTopLevelPresenter.setPermissionsToFileStorageIsGranted(true);
        }

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
            if (view.getId() == R.id.songName || view.getId() == R.id.songLyrics) {
                mTopLevelPresenter.changeLyricsOpenOrCloseCondition(position);
            }

        });
        songsItemRvAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.playPauseButton) {
                mTopLevelPresenter.playButtonIsClicked(position, true);
            }
            if (view.getId() == R.id.songName) {
                mTopLevelPresenter.showSongEditPopup(position, false);
            }
            return true;
        });
    }

    @Override
    public void showSongEditPopup(String SongName, int position, int currentSetList, boolean audioIsOn, boolean countdownIsOn, String audioFile, String lyrics, int tempBpm, boolean actionIsAddNewSong) {
        new EditSongPopup(this, SongName, position, currentSetList, audioIsOn, countdownIsOn, audioFile, lyrics, mTopLevelPresenter, tempBpm, actionIsAddNewSong).showPopupWindow();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @OnClick({R.id.buyButton, R.id.newItemCircleButton, R.id.volumeX2button, R.id.metroSoundChangeButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buyButton:
                mTopLevelPresenter.showBuyPopup();
                break;
            case R.id.newItemCircleButton:
                mTopLevelPresenter.showSongEditPopup(0, true);
                break;
            case R.id.volumeX2button:
                mTopLevelPresenter.showVolumeUpPopup();
                break;
            case R.id.metroSoundChangeButton:
                mTopLevelPresenter.metroSoundChangeButton();
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

    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }

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
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions

                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        mTopLevelPresenter.setPermissionsToFileStorageIsGranted(true);
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
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

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void showErrorMessages(int errorId) {
        switch (errorId) {
            case 100:
                Toast.makeText(this, R.string.check_permissions_failed, Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
