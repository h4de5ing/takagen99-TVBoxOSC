package com.github.tvbox.osc.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.activity.DetailActivity;
import com.github.tvbox.osc.ui.activity.FastSearchActivity;
import com.github.tvbox.osc.ui.adapter.GridAdapter;
import com.github.tvbox.osc.ui.dialog.GridFilterDialog;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import java.util.Stack;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class GridFragment extends BaseLazyFragment {
    private MovieSort.SortData sortData = null;
    private TvRecyclerView mGridView;
    private SourceViewModel sourceViewModel;
    private GridFilterDialog gridFilterDialog;
    private GridAdapter gridAdapter;
    private int page = 1;
    private int maxPage = 1;
    private boolean isLoad = false;
    private boolean isTop = true;
    private View focusedView = null;

    private class GridInfo {
        public String sortID = "";
        public TvRecyclerView mGridView;
        public GridAdapter gridAdapter;
        public int page = 1;
        public int maxPage = 1;
        public boolean isLoad = false;
        public View focusedView = null;
    }

    Stack<GridInfo> mGrids = new Stack<GridInfo>(); //ui栈

    public static GridFragment newInstance(MovieSort.SortData sortData) {
        return new GridFragment().setArguments(sortData);
    }

    public GridFragment setArguments(MovieSort.SortData sortData) {
        this.sortData = sortData;
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_grid;
    }

    @Override
    protected void init() {
        initView();
        initViewModel();
        initData();
    }

    private void changeView(String id) {
        initView();
        this.sortData.id = id; // 修改sortData.id为新的ID
        initViewModel();
        initData();
    }

    public boolean isFolederMode() {
        return (getUITag() == '1');
    }

    // 获取当前页面UI的显示模式 ‘0’ 正常模式 '1' 文件夹模式 '2' 显示缩略图的文件夹模式
    public char getUITag() {
        return (sortData.flag == null || sortData.flag.length() == 0) ? '0' : sortData.flag.charAt(0);
    }

    // 是否允许聚合搜索 sortData.flag的第二个字符为‘1’时允许聚搜
    public boolean enableFastSearch() {
        return (sortData.flag == null || sortData.flag.length() < 2) ? true : (sortData.flag.charAt(1) == '1');
    }

    // 保存当前页面
    private void saveCurrentView() {
        if (this.mGridView == null) return;
        GridInfo info = new GridInfo();
        info.sortID = this.sortData.id;
        info.mGridView = this.mGridView;
        info.gridAdapter = this.gridAdapter;
        info.page = this.page;
        info.maxPage = this.maxPage;
        info.isLoad = this.isLoad;
        info.focusedView = this.focusedView;
        this.mGrids.push(info);
    }

    // 丢弃当前页面，将页面还原成上一个保存的页面
    public boolean restoreView() {
        if (mGrids.empty()) return false;
        this.showSuccess();
        ((ViewGroup) mGridView.getParent()).removeView(this.mGridView); // 重父窗口移除当前控件
        GridInfo info = mGrids.pop();// 还原上次保存的控件
        this.sortData.id = info.sortID;
        this.mGridView = info.mGridView;
        this.gridAdapter = info.gridAdapter;
        this.page = info.page;
        this.maxPage = info.maxPage;
        this.isLoad = info.isLoad;
        this.focusedView = info.focusedView;
        this.mGridView.setVisibility(View.VISIBLE);
//        if(this.focusedView != null){ this.focusedView.requestFocus(); }
        if (mGridView != null) mGridView.requestFocus();
        return true;
    }

    // 更改当前页面
    private void createView() {
        this.saveCurrentView(); // 保存当前页面
        if (mGridView == null) { // 从layout中拿view
            mGridView = findViewById(R.id.mGridView);
        } else { // 复制当前view
            TvRecyclerView v3 = new TvRecyclerView(this.mContext);
            v3.setSpacingWithMargins(10, 10);
            v3.setLayoutParams(mGridView.getLayoutParams());
            v3.setPadding(mGridView.getPaddingLeft(), mGridView.getPaddingTop(), mGridView.getPaddingRight(), mGridView.getPaddingBottom());
            v3.setClipToPadding(mGridView.getClipToPadding());
            ((ViewGroup) mGridView.getParent()).addView(v3);
            mGridView.setVisibility(View.GONE);
            mGridView = v3;
            mGridView.setVisibility(View.VISIBLE);
        }
        mGridView.setHasFixedSize(true);
        gridAdapter = new GridAdapter(isFolederMode());
        this.page = 1;
        this.maxPage = 1;
        this.isLoad = false;
    }

    private void initView() {
        this.createView();
        mGridView.setAdapter(gridAdapter);
        if (isFolederMode()) {
            mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        } else {
            mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 5 : 6));
        }
        gridAdapter.getLoadMoreModule().setEnableLoadMore(true);
        gridAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> sourceViewModel.getList(sortData, page));
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });
        mGridView.setOnInBorderKeyEventListener((direction, focused) -> false);
        gridAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            Movie.Video video = gridAdapter.getData().get(position);
            if (video != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", video.id);
                bundle.putString("sourceKey", video.sourceKey);
                bundle.putString("title", video.name);

                SourceBean homeSourceBean = ApiConfig.get().getHomeSourceBean();
                if (("12".indexOf(getUITag()) != -1) && video.tag.equals("folder")) {
                    focusedView = view;
                    changeView(video.id);
                } else {
                    if (video.id == null || video.id.isEmpty() || video.id.startsWith("msearch:")) {
                        jumpActivity(FastSearchActivity.class, bundle);
                    } else {
                        jumpActivity(DetailActivity.class, bundle);
                    }
                }

            }
        });
        // takagen99 : Long Press to Fast Search
        gridAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            Movie.Video video = gridAdapter.getData().get(position);
            if (video != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", video.id);
                bundle.putString("sourceKey", video.sourceKey);
                bundle.putString("title", video.name);
                jumpActivity(FastSearchActivity.class, bundle);
            }
            return true;
        });
        setLoadSir(mGridView);
    }

    private void initViewModel() {
        if (sourceViewModel != null) {
            return;
        }
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.listResult.observe(this, absXml -> {
//                if(mGridView != null) mGridView.requestFocus();
            if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                if (page == 1) {
                    showSuccess();
                    isLoad = true;
                    gridAdapter.setNewData(absXml.movie.videoList);
                } else {
                    gridAdapter.addData(absXml.movie.videoList);
                }
                page++;
                maxPage = absXml.movie.pagecount;
                if (page > maxPage) {
                    gridAdapter.getLoadMoreModule().loadMoreEnd();
                    gridAdapter.getLoadMoreModule().setEnableLoadMore(false);
                } else {
                    gridAdapter.getLoadMoreModule().loadMoreComplete();
                    gridAdapter.getLoadMoreModule().setEnableLoadMore(true);
                }
            } else {
                if (page == 1) {
                    showEmpty();
                }
                if (page > maxPage) {
                    Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    gridAdapter.getLoadMoreModule().loadMoreEnd();
                } else {
                    gridAdapter.getLoadMoreModule().loadMoreComplete();
                }
                gridAdapter.getLoadMoreModule().setEnableLoadMore(false);
            }
        });
    }

    public boolean isLoad() {
        return isLoad || !mGrids.empty(); //如果有缓存页的话也可以认为是加载了数据的
    }

    private void initData() {
        showLoading();
        isLoad = false;
        scrollTop();
        sourceViewModel.getList(sortData, page);
    }

    public boolean isTop() {
        return isTop;
    }

    public void scrollTop() {
        isTop = true;
        mGridView.scrollToPosition(0);
    }

    public void showFilter() {
        if (!sortData.filters.isEmpty() && gridFilterDialog == null) {
            gridFilterDialog = new GridFilterDialog(mContext);
            gridFilterDialog.setData(sortData);
            gridFilterDialog.setOnDismiss(() -> {
                page = 1;
                initData();
            });
        }
        if (gridFilterDialog != null) gridFilterDialog.show();
    }

    public void forceRefresh() {
        page = 1;
        initData();
    }
}