package com.superbigbang.mushelp.adapter.provider;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.SetList;

public class SetListItemProvider extends BaseItemProvider<SetList, BaseViewHolder> {

    @Override
    public int viewType() {
        return 100;
    }

    @Override
    public int layout() {
        return R.layout.item_set_list;
    }

    @SuppressLint("ResourceType")
    @Override
    public void convert(BaseViewHolder helper, SetList data, int position) {
        helper.setText(R.id.setListName, data.getName());
        if (data.isOpen()) {
            helper.setTextColor(R.id.setListName, ExtendApplication.currentThemeColorsTextSelected);
        } else {
            helper.setTextColor(R.id.setListName, Color.parseColor("#66bfff"));
        }
    }
}
