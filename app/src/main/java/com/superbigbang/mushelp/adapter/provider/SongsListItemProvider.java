package com.superbigbang.mushelp.adapter.provider;

import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.NormalMultipleEntity;

public class SongsListItemProvider extends BaseItemProvider<NormalMultipleEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return DemoMultipleItemRvAdapter.TYPE_SONGS_LIST;
    }

    @Override
    public int layout() {
        return R.layout.item_song_list;
    }

    @Override
    public void convert(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        helper.setText(R.id.songPosition, String.valueOf(data.id));
        helper.setText(R.id.songName, data.songname);
        helper.setText(R.id.songLyrics, data.lyrics);
        helper.setText(R.id.bpmEditMetro, String.valueOf(data.bitrate));
        helper.addOnClickListener(R.id.playPauseButton).addOnClickListener(R.id.deleteSongButton)
                .addOnLongClickListener(R.id.playPauseButton);
    }

    @Override
    public void onClick(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        if (helper.getView(R.id.songLyrics).getVisibility() == View.VISIBLE) {
            helper.setGone(R.id.songLyrics, false);
        } else {
            helper.setVisible(R.id.songLyrics, true);
        }

    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, NormalMultipleEntity data, int position) {
        Toast.makeText(mContext, "longClick Open song edit window", Toast.LENGTH_SHORT).show();
        return true;
    }
}
