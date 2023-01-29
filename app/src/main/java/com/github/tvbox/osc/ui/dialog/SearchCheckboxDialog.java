package com.github.tvbox.osc.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.adapter.CheckboxSearchAdapter;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class SearchCheckboxDialog extends BaseDialog {

    private TvRecyclerView mGridView;
    private CheckboxSearchAdapter checkboxSearchAdapter;
    private final List<SourceBean> mSourceList;
    LinearLayout checkAll;
    LinearLayout clearAll;

    public HashMap<String, String> mCheckSourcees;

    public SearchCheckboxDialog(@NonNull @NotNull Context context, List<SourceBean> sourceList, HashMap<String, String> checkedSources) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        mSourceList = sourceList;
        mCheckSourcees = checkedSources;
        setContentView(R.layout.dialog_checkbox_search);
        initView(context);
    }

    @Override
    public void dismiss() {
        checkboxSearchAdapter.setMCheckedSources();
        super.dismiss();
    }

    protected void initView(Context context) {
        mGridView = findViewById(R.id.mGridView);
        checkAll = findViewById(R.id.checkAll);
        clearAll = findViewById(R.id.clearAll);
        checkboxSearchAdapter = new CheckboxSearchAdapter(new DiffUtil.ItemCallback<SourceBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem.getKey().equals(newItem.getKey());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        });
        mGridView.setHasFixedSize(true);

        // Multi Column Selection
        int size = mSourceList.size();
        int spanCount = (int) Math.floor(size / 10.0);
        if (spanCount <= 1) spanCount = 2;
        if (spanCount >= 3) spanCount = 3;
        mGridView.setLayoutManager(new V7GridLayoutManager(getContext(), spanCount));
        View root = findViewById(R.id.root);
        ViewGroup.LayoutParams clp = root.getLayoutParams();
        clp.width = AutoSizeUtils.mm2px(getContext(), 400 + 260 * (spanCount - 1));

        mGridView.setAdapter(checkboxSearchAdapter);
        checkboxSearchAdapter.setData(mSourceList, mCheckSourcees);
        int pos = 0;
        if (mCheckSourcees != null) {
            for (int i = 0; i < mSourceList.size(); i++) {
                String key = mSourceList.get(i).getKey();
                if (mCheckSourcees.containsKey(key)) {
                    pos = i;
                    break;
                }
            }
        }
        final int scrollPosition = pos;
        mGridView.post(() -> mGridView.smoothScrollToPosition(scrollPosition));
        checkAll.setOnClickListener(view -> {
            FastClickCheckUtil.check(view);
            mCheckSourcees = new HashMap<>();
            for (SourceBean sourceBean : mSourceList) {
                mCheckSourcees.put(sourceBean.getKey(), "1");
            }
            checkboxSearchAdapter.setData(mSourceList, mCheckSourcees);
        });
        clearAll.setOnClickListener(view -> {
            FastClickCheckUtil.check(view);
            mCheckSourcees = new HashMap<>();
            checkboxSearchAdapter.setData(mSourceList, mCheckSourcees);
        });
    }
}