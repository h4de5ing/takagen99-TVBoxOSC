package com.github.tvbox.osc.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.Subtitle;
import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.ui.adapter.SearchSubtitleAdapter;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.viewmodel.SubtitleViewModel;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchSubtitleDialog extends BaseDialog {

    private Context mContext;
    private TvRecyclerView mGridView;
    private SearchSubtitleAdapter searchAdapter;

    private TextView subtitleSearchBtn;
    private EditText subtitleSearchEt;
    private SubtitleLoader mSubtitleLoader;
    private ProgressBar loadingBar;
    private SubtitleViewModel subtitleViewModel;
    private int page = 1;
    private int maxPage = 5;
    private String searchWord = "";

    private List<Subtitle> zipSubtitles = new ArrayList<>();
    private boolean isSearchPag = true;


    public SearchSubtitleDialog(@NonNull @NotNull Context context) {
        super(context);
        mContext = context;
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        setContentView(R.layout.dialog_search_subtitle);
        initView(context);
        initViewModel();
    }

    protected void initView(Context context) {
        loadingBar = findViewById(R.id.loadingBar);
        mGridView = findViewById(R.id.mGridView);
        subtitleSearchEt = findViewById(R.id.input);
        subtitleSearchBtn = findViewById(R.id.inputSubmit);
        subtitleSearchBtn.setText(HomeActivity.getRes().getString(R.string.vod_sub_search));

        searchAdapter = new SearchSubtitleAdapter();
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(new V7LinearLayoutManager(getContext(), 1, false));
        mGridView.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            Subtitle subtitle = searchAdapter.getData().get(position);
            //加载字幕
            if (mSubtitleLoader != null) {
                if (subtitle.getIsZip()) {
                    isSearchPag = false;
                    loadingBar.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    subtitleViewModel.getSearchResultSubtitleUrls(subtitle);
                } else {
                    loadSubtitle(subtitle);
                    dismiss();
                }
            }
        });
        searchAdapter.getLoadMoreModule().setEnableLoadMore(true);
        searchAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (searchAdapter.getData().get(0).getIsZip()) {
                    subtitleViewModel.searchResult(searchWord, page);
                }
            }
        });

        subtitleSearchBtn.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            String wd = subtitleSearchEt.getText().toString().trim();
            search(wd);
        });
        searchAdapter.setNewData(new ArrayList<>());
    }

    public void setSearchWord(String wd) {
        wd = wd.replaceAll("(?:（|\\(|\\[|【|\\.mp4|\\.mkv|\\.avi|\\.MP4|\\.MKV|\\.AVI)", "");
        wd = wd.replaceAll("(?:：|\\:|）|\\)|\\]|】|\\.)", " ");
        int len = wd.length();
        int finalLen = len >= 36 ? 36 : len;
        wd = wd.substring(0, finalLen).trim();
        subtitleSearchEt.setText(wd);
        subtitleSearchEt.setSelection(wd.length());
        subtitleSearchEt.requestFocus();
    }

    public void search(String wd) {
        isSearchPag = true;
        searchAdapter.setNewData(new ArrayList<>());
        if (!TextUtils.isEmpty(wd)) {
            loadingBar.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            searchWord = wd;
            subtitleViewModel.searchResult(wd, page = 1);
        } else {
            Toast.makeText(getContext(), "输入内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViewModel() {
        subtitleViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SubtitleViewModel.class);
        subtitleViewModel.searchResult.observe((LifecycleOwner) mContext, subtitleData -> {
            List<Subtitle> data = subtitleData.getSubtitleList();
            loadingBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            if (data == null) {
                mGridView.post(() -> Toast.makeText(getContext(), "未查询到匹配字幕", Toast.LENGTH_SHORT).show());
                return;
            }

            if (data.size() > 0) {
                mGridView.requestFocus();
                if (subtitleData.getIsZip()) {
                    if (subtitleData.getIsNew()) {
                        searchAdapter.setNewData(data);
                        zipSubtitles = data;
                    } else {
                        searchAdapter.addData(data);
                        zipSubtitles.addAll(data);
                    }
                    page++;
                    if (page > maxPage) {
                        searchAdapter.getLoadMoreModule().loadMoreEnd();
                        searchAdapter.getLoadMoreModule().setEnableLoadMore(false);
                    } else {
                        searchAdapter.getLoadMoreModule().loadMoreComplete();
                        searchAdapter.getLoadMoreModule().setEnableLoadMore(true);
                    }
                } else {
                    searchAdapter.getLoadMoreModule().loadMoreComplete();
                    searchAdapter.setNewData(data);
                    searchAdapter.getLoadMoreModule().setEnableLoadMore(false);
                }
            } else {
                if (page > maxPage) {
                    searchAdapter.getLoadMoreModule().loadMoreEnd();
                } else {
                    searchAdapter.getLoadMoreModule().loadMoreComplete();
                }
                searchAdapter.getLoadMoreModule().setEnableLoadMore(false);
            }

        });
    }

    private void loadSubtitle(Subtitle subtitle) {
        subtitleViewModel.getSubtitleUrl(subtitle, mSubtitleLoader);
    }

    public void setSubtitleLoader(SubtitleLoader subtitleLoader) {
        mSubtitleLoader = subtitleLoader;
    }

    public interface SubtitleLoader {
        void loadSubtitle(Subtitle subtitle);
    }

    @Override
    public void onBackPressed() {
        if (!isSearchPag) {
            isSearchPag = true;
            loadingBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            searchAdapter.setNewData(zipSubtitles);
            searchAdapter.getLoadMoreModule().setEnableLoadMore(page < maxPage);
            return;
        }
        dismiss();
    }
}