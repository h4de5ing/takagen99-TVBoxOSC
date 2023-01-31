package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/12/27
 */
public class HomeIconDialog extends BaseDialog {
    private final TextView tvHomeSearch;
    private final TextView tvHomeMenu;

    public HomeIconDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_homeoption);
        setCanceledOnTouchOutside(true);
        tvHomeSearch = findViewById(R.id.tvHomeSearch);
        tvHomeSearch.setText(Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true) ? "上方" : "下方");
        tvHomeMenu = findViewById(R.id.tvHomeMenu);
        tvHomeMenu.setText(Hawk.get(HawkConfig.HOME_MENU_POSITION, true) ? "上方" : "下方");

        findViewById(R.id.llSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_SEARCH_POSITION, !Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true));
                tvHomeSearch.setText(Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true) ? "上方" : "下方");
            }
        });
        findViewById(R.id.llMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_MENU_POSITION, !Hawk.get(HawkConfig.HOME_MENU_POSITION, true));
                tvHomeMenu.setText(Hawk.get(HawkConfig.HOME_MENU_POSITION, true) ? "上方" : "下方");
            }
        });
    }

}