package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.ui.adapter.GridFilterKVAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GridFilterDialog extends BaseDialog {
    private LinearLayout filterRoot;

    public GridFilterDialog(@NonNull @NotNull Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        setContentView(R.layout.dialog_grid_filter);
        filterRoot = findViewById(R.id.filterRoot);
    }

    public interface Callback {
        void change();
    }

    public void setOnDismiss(Callback callback) {
        setOnDismissListener(dialogInterface -> {
            if (selectChange) callback.change();
        });
    }

    public void setData(MovieSort.SortData sortData) {
        ArrayList<MovieSort.SortFilter> filters = sortData.filters;
        for (MovieSort.SortFilter filter : filters) {
            View line = LayoutInflater.from(getContext()).inflate(R.layout.item_grid_filter, null);
            ((TextView) line.findViewById(R.id.filterName)).setText(filter.name);
            TvRecyclerView gridView = line.findViewById(R.id.mFilterKv);
            gridView.setHasFixedSize(true);
            gridView.setLayoutManager(new V7LinearLayoutManager(getContext(), 0, false));
            GridFilterKVAdapter filterKVAdapter = new GridFilterKVAdapter();
            gridView.setAdapter(filterKVAdapter);
            String key = filter.key;
            ArrayList<String> values = new ArrayList<>(filter.values.keySet());
            ArrayList<String> keys = new ArrayList<>(filter.values.values());
            filterKVAdapter.setOnItemClickListener(new OnItemClickListener() {
                View pre = null;

                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (sortData.filterSelect.get(key) == null || !sortData.filterSelect.get(key).equals(values.get(position))) {
                        sortData.filterSelect.put(key, keys.get(position));
                        selectChange = true;
                        if (pre != null) {
                            TextView val = pre.findViewById(R.id.filterValue);
                            val.getPaint().setFakeBoldText(false);
                            val.setTextColor(getContext().getResources().getColor(R.color.color_FFFFFF));
                        }
                        TextView val = view.findViewById(R.id.filterValue);
                        val.getPaint().setFakeBoldText(true);
                        // takagen99: Added Theme Color
//                        val.setTextColor(getContext().getResources().getColor(R.color.color_theme));
                        TypedArray a = getContext().obtainStyledAttributes(R.styleable.themeColor);
                        int themeColor = a.getColor(R.styleable.themeColor_color_theme, 0);
                        val.setTextColor(themeColor);
                        pre = view;
                    }
                }
            });
            filterKVAdapter.setNewData(values);
            filterRoot.addView(line);
        }
    }

    private boolean selectChange = false;

    public void show() {
        selectChange = false;
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // layoutParams.dimAmount = 0f;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}