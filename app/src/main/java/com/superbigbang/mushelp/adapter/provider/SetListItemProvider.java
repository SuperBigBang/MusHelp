package com.superbigbang.mushelp.adapter.provider;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

public class SetListItemProvider extends BaseItemProvider<NormalMultipleEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return DemoMultipleItemRvAdapter.TYPE_SET_LISTS;
    }

    @Override
    public int layout() {
        return R.layout.item_set_list;
    }

    @Override
    public void convert(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        helper.setText(R.id.setListName, data.setlistname);
    }
}
