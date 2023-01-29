package com.github.tvbox.osc.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.bean.LiveEpgDate;

import java.util.ArrayList;

public class LiveEpgDateAdapter extends BaseQuickAdapter<LiveEpgDate, BaseViewHolder> {
    private int selectedIndex = -1;
    private int focusedIndex = -1;

    public LiveEpgDateAdapter() {
        super(R.layout.item_live_channel_group, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveEpgDate item) {
        TextView tvGroupName = holder.getView(R.id.tvChannelGroupName);
        tvGroupName.setText(item.getDatePresented());
        tvGroupName.setBackgroundColor(Color.TRANSPARENT);
        if (item.getIndex() == selectedIndex && item.getIndex() != focusedIndex) {
            // takagen99: Added Theme Color
//            tvGroupName.setTextColor(getContext().getResources().getColor(R.color.color_theme));
            tvGroupName.setTextColor(((BaseActivity) getContext()).getThemeColor());
        } else if (item.getIndex() == selectedIndex && item.getIndex() == focusedIndex) {
            tvGroupName.setTextColor(getContext().getResources().getColor(R.color.color_FFFFFF));
        } else {
            tvGroupName.setTextColor(getContext().getResources().getColor(R.color.color_FFFFFF));
        }
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex == this.selectedIndex) return;
        int preSelectedIndex = this.selectedIndex;
        this.selectedIndex = selectedIndex;
        if (preSelectedIndex != -1)
            notifyItemChanged(preSelectedIndex);
        if (this.selectedIndex != -1)
            notifyItemChanged(this.selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setFocusedIndex(int focusedIndex) {
        int preFocusedIndex = this.focusedIndex;
        this.focusedIndex = focusedIndex;
        if (preFocusedIndex != -1)
            notifyItemChanged(preFocusedIndex);
        if (this.focusedIndex != -1)
            notifyItemChanged(this.focusedIndex);
        else if (this.selectedIndex != -1)
            notifyItemChanged(this.selectedIndex);
    }
}