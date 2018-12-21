package com.superbigbang.mushelp.adapter.provider;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.superbigbang.mushelp.ExtendApplication;
import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.adapter.DemoMultipleItemRvAdapter;
import com.superbigbang.mushelp.model.Songs;

public class SongsListItemProvider extends BaseItemProvider<Songs, BaseViewHolder> {

    @Override
    public int viewType() {
        return DemoMultipleItemRvAdapter.TYPE_SONGS_LIST;
    }

    @Override
    public int layout() {
        return R.layout.item_song_list;
    }

    //====================== START начать редактировать отсюда, сделать Songs модель
    @Override
    public void convert(BaseViewHolder helper, Songs data, int position) {
        helper.setText(R.id.songPosition, String.valueOf(data.getPosition() + 1));
        helper.setText(R.id.songName, data.getTitle());
        helper.setText(R.id.songLyrics, data.getLyrics());
        helper.setText(R.id.bpmEditMetro, String.valueOf(data.getMetronombpm()));
        if (!data.isPlaystarted()) {
            helper.setImageDrawable(R.id.playPauseButton, ContextCompat.getDrawable(ExtendApplication.getBaseComponent().getContext(), R.drawable.baseline_play_circle_outline_white_48));
        } else {
            helper.setImageDrawable(R.id.playPauseButton, ContextCompat.getDrawable(ExtendApplication.getBaseComponent().getContext(), R.drawable.baseline_pause_circle_outline_white_48));
        }
        helper.addOnClickListener(R.id.playPauseButton).addOnClickListener(R.id.deleteSongButton)
                .addOnLongClickListener(R.id.playPauseButton);
    }

    @Override
    public void onClick(BaseViewHolder helper, Songs data, int position) {
        if (helper.getView(R.id.songLyrics).getVisibility() == View.VISIBLE) {
            helper.setGone(R.id.songLyrics, false);
        } else {
            helper.setVisible(R.id.songLyrics, true);
        }

    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, Songs data, int position) {
        Toast.makeText(mContext, "longClick Open song edit window", Toast.LENGTH_SHORT).show();
        return true;
    }
}
