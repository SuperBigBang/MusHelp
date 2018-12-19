package com.superbigbang.mushelp.popup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.SetList;
import com.superbigbang.mushelp.screen.topLevelActivity.TopLevelPresenter;

import io.realm.RealmResults;
import razerdp.basepopup.BasePopupWindow;
import timber.log.Timber;

public class EditSetListPopup extends BasePopupWindow implements View.OnClickListener {
    private Button mCancelButton;
    private Button mSaveButton;
    private EditText mEditTextPosition;
    private EditText mEditTextSetListName;
    private int currentPosition;

    private TopLevelPresenter mTopLevelPresenter;

    public EditSetListPopup(Context context, String SongName, int position, TopLevelPresenter mTopLevelPresenter) {
        super(context);
        mCancelButton = findViewById(R.id.btn_e_cancel);
        mSaveButton = findViewById(R.id.btn_e_Save);
        mEditTextPosition = findViewById(R.id.editSetListPosition);
        mEditTextSetListName = findViewById(R.id.editSetListName);
        mEditTextPosition.setText(String.valueOf(position + 1));
        mEditTextSetListName.setText(SongName);

        currentPosition = position;
        this.mTopLevelPresenter = mTopLevelPresenter;
        setBlurBackgroundEnable(true);
        bindEvent();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return null;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return null;
    }

    private void bindEvent() {
        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

    }

    //=============================================================super methods


    @Override
    public Animator onCreateShowAnimator() {
        return getDefaultSlideFromBottomAnimationSet();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_edit_set_list);
    }

    @Override
    public Animator onCreateDismissAnimator() {
        AnimatorSet set = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            set = new AnimatorSet();
            if (mDisplayAnimateView != null) {
                set.playTogether(
                        ObjectAnimator.ofFloat(mDisplayAnimateView, "translationY", 0, 250).setDuration(400),
                        ObjectAnimator.ofFloat(mDisplayAnimateView, "alpha", 1, 0.4f).setDuration(375));
            }
        }
        return set;
    }

    @Override
    public BasePopupWindow setBlurBackgroundEnable(boolean blur) {
        return super.setBlurBackgroundEnable(blur);
    }

    //=============================================================click event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_e_cancel:
                dismiss();
                break;
            case R.id.btn_e_Save:
                String resultPositionEditText = mEditTextPosition.getText().toString();
                String resultNameEditText = mEditTextSetListName.getText().toString();
                int valueOfResultPositionEditText;
                if (resultPositionEditText.isEmpty()) {
                    Toast.makeText(getContext(), R.string.SetListNoPositionError, Toast.LENGTH_LONG).show();
                } else if (resultNameEditText.isEmpty()) {
                    Toast.makeText(getContext(), R.string.SetListNoNameError, Toast.LENGTH_LONG).show();
                } else {
                    valueOfResultPositionEditText = (Integer.valueOf(resultPositionEditText)) - 1;
                    if (valueOfResultPositionEditText < 0 || valueOfResultPositionEditText >= 10) {
                        Toast.makeText(getContext(), R.string.SetListPosNumError, Toast.LENGTH_LONG).show();
                    } else {
                        if (currentPosition > valueOfResultPositionEditText) {
//=============================================================to Bottom
                            RealmResults<SetList> setlistNoAutoSorting = mTopLevelPresenter.mSetlistsrealm
                                    .where(SetList.class)
                                    .between("position", valueOfResultPositionEditText, currentPosition)
                                    .findAll();
                            mTopLevelPresenter.mSetlistsrealm.beginTransaction();
                            int countposition1 = 0;
                            int countposition2 = 0;
                            SetList firstSetList = null;
                            SetList lastbeforeSetList = null;
                            for (int i = 0; i <= currentPosition - valueOfResultPositionEditText; i++) {
                                if (i == 0) {
                                    SetList editedSetList = setlistNoAutoSorting.where().equalTo("position", currentPosition).findFirst();
                                    editedSetList.setName(resultNameEditText);
                                    //    lastbeforeSetList = setlistNoAutoSorting.where().equalTo("position",currentPosition-1).findFirst();
                                    firstSetList = setlistNoAutoSorting.where().equalTo("position", valueOfResultPositionEditText).findFirst();
                                    countposition1 = firstSetList.getPosition();
                                    editedSetList.setPosition(countposition1);
                                } else {
                                    SetList secondSetList = setlistNoAutoSorting.where().equalTo("position", countposition1 + 1).findFirst();
                                    countposition2 = countposition1 + 1;
                                    if (firstSetList != null) {
                                        firstSetList.setPosition(countposition2);
                                    } else {
                                        Timber.e("ONENULL1!!!");
                                    }
                                    countposition1++;
                                    firstSetList = setlistNoAutoSorting.where().equalTo("position", countposition2 + 1).findFirst();
                                    countposition2++;
                                    if (secondSetList != null) {
                                        secondSetList.setPosition(countposition2);
                                    } else {
                                        Timber.e("ONENULL2!!!");
                                    }
                                    countposition1++;
                                }
                            }
                            mTopLevelPresenter.mSetlistsrealm.commitTransaction();
                        } else if (currentPosition < valueOfResultPositionEditText) {
//=============================================================to Top
                            RealmResults<SetList> setlistsforedit = mTopLevelPresenter.mSetlistsrealm
                                    .where(SetList.class)
                                    .between("position", currentPosition, valueOfResultPositionEditText)
                                    .findAll();
                            mTopLevelPresenter.mSetlistsrealm.beginTransaction();
                            String buffer1 = "";
                            String buffer2 = "";
                            for (int i = 0; i <= valueOfResultPositionEditText - currentPosition; i++) {
                                int currentEditSetList = (valueOfResultPositionEditText - currentPosition) - i;
                                if (i == 0) {
                                    buffer1 = setlistsforedit.get(currentEditSetList).getName();
                                    setlistsforedit.get(currentEditSetList).setName(resultNameEditText);
                                } else {
                                    buffer2 = setlistsforedit.get(currentEditSetList).getName();
                                    setlistsforedit.get(currentEditSetList).setName(buffer1);
                                    buffer1 = buffer2;
                                }
                            }
                            mTopLevelPresenter.mSetlistsrealm.commitTransaction();
                        } else if (currentPosition == valueOfResultPositionEditText) {
                            //=============================================================no change position, edit only Name
                            mTopLevelPresenter.mSetlistsrealm.beginTransaction();
                            SetList setList = mTopLevelPresenter.mSetlistsrealm
                                    .where(SetList.class)
                                    .equalTo("position", currentPosition)
                                    .findFirst();
                            setList.setName(resultNameEditText);
                            mTopLevelPresenter.mSetlistsrealm.commitTransaction();
                        }
                        Toast.makeText(getContext(), R.string.SetListHasEdit, Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void dismiss() {
        mTopLevelPresenter.clearStateStrategyPull();
        mTopLevelPresenter = null;
        super.dismiss();
    }
}
