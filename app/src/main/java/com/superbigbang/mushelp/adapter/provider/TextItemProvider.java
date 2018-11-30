package com.superbigbang.mushelp.adapter.provider;

import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

public class TextItemProvider extends BaseItemProvider<NormalMultipleEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return DemoMultipleItemRvAdapter.TYPE_TEXT;
    }

    @Override
    public int layout() {
        return R.layout.item_set_list;
    }

    @Override
    public void convert(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        helper.setText(R.id.setListName, data.content);
    }

    @Override
    public void onClick(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        Toast.makeText(mContext, "click on " + data.content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        Toast.makeText(mContext, "longClick", Toast.LENGTH_SHORT).show();
        return true;
    }
}