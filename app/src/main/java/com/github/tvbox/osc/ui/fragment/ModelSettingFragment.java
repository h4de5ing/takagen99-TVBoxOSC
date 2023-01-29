package com.github.tvbox.osc.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.osc.BuildConfig;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.IJKCode;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.dialog.AboutDialog;
import com.github.tvbox.osc.ui.dialog.ApiDialog;
import com.github.tvbox.osc.ui.dialog.ApiHistoryDialog;
import com.github.tvbox.osc.ui.dialog.BackupDialog;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.XWalkInitDialog;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;
import okhttp3.HttpUrl;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvDebugOpen;
    private TextView tvMediaCodec;
    private TextView tvParseWebView;
    private TextView tvPlay;
    private TextView tvRender;
    private TextView tvScale;
    private TextView tvApi;
    private TextView tvHomeApi;
    private TextView tvDns;
    private TextView tvHomeRec;
    private TextView tvHomeNum;
    private TextView tvSearchView;
    private TextView tvShowPreviewText;
    private TextView tvHomeShow;
    private TextView tvLocale;
    private TextView tvPIP;
    private TextView tvTheme;
    private TextView tvAbout;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        tvPIP = findViewById(R.id.tvPIP);
        tvPIP.setText(Hawk.get(HawkConfig.PIC_IN_PIC, false) ? "开启" : "关闭");
        tvLocale = findViewById(R.id.tvLocale);
        tvLocale.setText(getLocaleView(Hawk.get(HawkConfig.HOME_LOCALE, 0)));
        tvHomeShow = findViewById(R.id.tvHomeShow);
        tvHomeShow.setText(Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false) ? "开启" : "关闭");
        tvShowPreviewText = findViewById(R.id.showPreviewText);
        tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvRender = findViewById(R.id.tvRenderType);
        tvScale = findViewById(R.id.tvScaleType);
        tvApi = findViewById(R.id.tvApi);
        tvHomeApi = findViewById(R.id.tvHomeApi);
        tvDns = findViewById(R.id.tvDns);
        tvHomeRec = findViewById(R.id.tvHomeRec);
        tvHomeNum = findViewById(R.id.tvHomeNum);
        tvSearchView = findViewById(R.id.tvSearchView);
        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "开启" : "关闭");
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
        tvApi.setText(Hawk.get(HawkConfig.API_URL, ""));
        tvDns.setText(OkGoHelper.dnsHttpsList.get(Hawk.get(HawkConfig.DOH_URL, 0)));
        tvHomeRec.setText(getHomeRecName(Hawk.get(HawkConfig.HOME_REC, 0)));
        tvHomeNum.setText(HistoryHelper.getHomeRecName(Hawk.get(HawkConfig.HOME_NUM, 0)));
        tvSearchView.setText(getSearchView(Hawk.get(HawkConfig.SEARCH_VIEW, 0)));
        tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
        tvScale.setText(PlayerHelper.getScaleName(Hawk.get(HawkConfig.PLAY_SCALE, 0)));
        tvPlay.setText(PlayerHelper.getPlayerName(Hawk.get(HawkConfig.PLAY_TYPE, 0)));
        tvRender.setText(PlayerHelper.getRenderName(Hawk.get(HawkConfig.PLAY_RENDER, 0)));
        tvTheme = findViewById(R.id.tvTheme);
        tvAbout = findViewById(R.id.tvAbout);
        tvTheme.setText(getThemeView(Hawk.get(HawkConfig.THEME_SELECT, 0)));

        //takagen99 : Set HomeApi as default
        findViewById(R.id.llHomeApi).requestFocus();

        findViewById(R.id.llDebug).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            Hawk.put(HawkConfig.DEBUG_OPEN, !Hawk.get(HawkConfig.DEBUG_OPEN, false));
            tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "开启" : "关闭");
        });

        // Input Source URL ------------------------------------------------------------------------
        findViewById(R.id.llApi).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            ApiDialog dialog = new ApiDialog(mActivity);
            EventBus.getDefault().register(dialog);
            dialog.setOnListener(api -> {
                Hawk.put(HawkConfig.API_URL, api);
                tvApi.setText(api);
            });
            dialog.setOnDismissListener(dialog1 -> {
                ((BaseActivity) mActivity).hideSystemUI(true);
                EventBus.getDefault().unregister(dialog1);
            });
            dialog.show();
        });
        findViewById(R.id.llApiHistory).setOnClickListener(v -> {
            ArrayList<String> history = Hawk.get(HawkConfig.API_HISTORY, new ArrayList<>());
            if (history.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.API_URL, "");
            int idx = 0;
            if (history.contains(current))
                idx = history.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip(getString(R.string.dia_history_list));
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String api) {
                    Hawk.put(HawkConfig.API_URL, api);
                    tvApi.setText(api);
                    dialog.dismiss();
                }

                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.API_HISTORY, data);
                }
            }, history, idx);
            dialog.show();
        });
        // 1. HOME Configuration ---------------------------------------------------------------- //
        // Select Home Source ------------------------------------
        findViewById(R.id.llHomeApi).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            List<SourceBean> sites = ApiConfig.get().getSourceBeanList();
            if (sites.size() > 0) {
                SelectDialog<SourceBean> dialog = new SelectDialog<>(mActivity);

                // Multi Column Selection
                int spanCount = (int) Math.floor(sites.size() / 10.0);
                if (spanCount <= 1) spanCount = 1;
                if (spanCount >= 3) spanCount = 3;

                TvRecyclerView tvRecyclerView = dialog.findViewById(R.id.list);
                tvRecyclerView.setLayoutManager(new V7GridLayoutManager(dialog.getContext(), spanCount));
                LinearLayout cl_root = dialog.findViewById(R.id.cl_root);
                ViewGroup.LayoutParams clp = cl_root.getLayoutParams();
                if (spanCount != 1) {
                    clp.width = AutoSizeUtils.mm2px(dialog.getContext(), 400 + 260 * (spanCount - 1));
                }

                dialog.setTip(getString(R.string.dia_source));
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
                    @Override
                    public void click(SourceBean value, int pos) {
                        ApiConfig.get().setSourceBean(value);
                        tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
                    }

                    @Override
                    public String getDisplay(SourceBean val) {
                        return val.getName();
                    }
                }, new DiffUtil.ItemCallback<SourceBean>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                        return oldItem == newItem;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                        return oldItem.getKey().equals(newItem.getKey());
                    }
                }, sites, sites.indexOf(ApiConfig.get().getHomeSourceBean()));
                dialog.show();
            }
        });
        // Switch to show / hide source header --------------------------
        findViewById(R.id.llHomeShow).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            Hawk.put(HawkConfig.HOME_SHOW_SOURCE, !Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false));
            tvHomeShow.setText(Hawk.get(HawkConfig.HOME_SHOW_SOURCE, true) ? "开启" : "关闭");
        });
        // Select Home Display Type : Douban / Recommended / History -----
        findViewById(R.id.llHomeRec).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.HOME_REC, 0);
            ArrayList<Integer> types = new ArrayList<>();
            types.add(0);
            types.add(1);
            types.add(2);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_hm_type));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.HOME_REC, value);
                    tvHomeRec.setText(getHomeRecName(value));
                }

                @Override
                public String getDisplay(Integer val) {
                    return getHomeRecName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, types, defaultPos);
            dialog.show();
        });
        // History to Keep ------------------------------------------
        findViewById(R.id.llHomeNum).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.HOME_NUM, 0);
            ArrayList<Integer> types = new ArrayList<>();
            types.add(0);
            types.add(1);
            types.add(2);
            types.add(3);
            types.add(4);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_history));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.HOME_NUM, value);
                    tvHomeNum.setText(HistoryHelper.getHomeRecName(value));
                }

                @Override
                public String getDisplay(Integer val) {
                    return HistoryHelper.getHomeRecName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, types, defaultPos);
            dialog.show();
        });
        // 2. PLAYER Configuration -------------------------------------------------------------- //
        // Switch for Preview Window -------------------------------
        findViewById(R.id.showPreview).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            Hawk.put(HawkConfig.SHOW_PREVIEW, !Hawk.get(HawkConfig.SHOW_PREVIEW, true));
            tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
        });
        // Select Screen Ratio -------------------------------------
        findViewById(R.id.llScale).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.PLAY_SCALE, 0);
            ArrayList<Integer> players = new ArrayList<>();
            players.add(0);
            players.add(1);
            players.add(2);
            players.add(3);
            players.add(4);
            players.add(5);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_ratio));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.PLAY_SCALE, value);
                    tvScale.setText(PlayerHelper.getScaleName(value));
                }

                @Override
                public String getDisplay(Integer val) {
                    return PlayerHelper.getScaleName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, players, defaultPos);
            dialog.show();
        });
        // Switch to ON / OFF Picture-In-Picture -------------------------
        findViewById(R.id.llPIP).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            Hawk.put(HawkConfig.PIC_IN_PIC, !Hawk.get(HawkConfig.PIC_IN_PIC, false));
            tvPIP.setText(Hawk.get(HawkConfig.PIC_IN_PIC, true) ? "开启" : "关闭");
        });
        // Select PLAYER Type --------------------------------------------
        findViewById(R.id.llPlay).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.PLAY_TYPE, 0);
            ArrayList<Integer> players = new ArrayList<>();
            players.add(0);
            players.add(1);
            players.add(2);
            players.add(10);
            players.add(11);
            players.add(12);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_player));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.PLAY_TYPE, value);
                    tvPlay.setText(PlayerHelper.getPlayerName(value));
                    PlayerHelper.init();
                }

                @Override
                public String getDisplay(Integer val) {
                    return PlayerHelper.getPlayerName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, players, defaultPos);
            dialog.show();
        });
        // Select DECODER Type --------------------------------------------
        findViewById(R.id.llMediaCodec).setOnClickListener(v -> {
            List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
            if (ijkCodes == null || ijkCodes.size() == 0)
                return;
            FastClickCheckUtil.check(v);

            int defaultPos = 0;
            String ijkSel = Hawk.get(HawkConfig.IJK_CODEC, "");
            for (int j = 0; j < ijkCodes.size(); j++) {
                if (ijkSel.equals(ijkCodes.get(j).getName())) {
                    defaultPos = j;
                    break;
                }
            }

            SelectDialog<IJKCode> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_decode));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<IJKCode>() {
                @Override
                public void click(IJKCode value, int pos) {
                    value.selected(true);
                    tvMediaCodec.setText(value.getName());
                }

                @Override
                public String getDisplay(IJKCode val) {
                    return val.getName();
                }
            }, new DiffUtil.ItemCallback<IJKCode>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }
            }, ijkCodes, defaultPos);
            dialog.show();
        });
        // 3. SYSTEM Configuration -------------------------------------------------------------- //
        // Select Webview ---------------------------------------------
        findViewById(R.id.llParseWebVew).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
            Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
            tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
            if (!useSystem) {
                Toast.makeText(mContext, "注意: XWalkView只适用于部分低Android版本，Android5.0以上推荐使用系统自带", Toast.LENGTH_LONG).show();
                XWalkInitDialog dialog = new XWalkInitDialog(mContext);
                dialog.setOnListener(new XWalkInitDialog.OnListener() {
                    @Override
                    public void onchange() {
                    }
                });
                dialog.show();
            }
        });
        // Select System Render ( Surface/Texture View ) ---------------------
        findViewById(R.id.llRender).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.PLAY_RENDER, 0);
            ArrayList<Integer> renders = new ArrayList<>();
            renders.add(0);
            renders.add(1);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_render));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.PLAY_RENDER, value);
                    tvRender.setText(PlayerHelper.getRenderName(value));
                    PlayerHelper.init();
                }

                @Override
                public String getDisplay(Integer val) {
                    return PlayerHelper.getRenderName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, renders, defaultPos);
            dialog.show();
        });
        // Select DNS ---------------------------------------------
        findViewById(R.id.llDns).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int dohUrl = Hawk.get(HawkConfig.DOH_URL, 0);

            SelectDialog<String> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_dns));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override
                public void click(String value, int pos) {
                    tvDns.setText(OkGoHelper.dnsHttpsList.get(pos));
                    Hawk.put(HawkConfig.DOH_URL, pos);
                    String url = OkGoHelper.getDohUrl(pos);
                    OkGoHelper.dnsOverHttps.setUrl(url.isEmpty() ? null : HttpUrl.get(url));
                    IjkMediaPlayer.toggleDotPort(pos > 0);
                }

                @Override
                public String getDisplay(String val) {
                    return val;
                }
            }, new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }
            }, OkGoHelper.dnsHttpsList, dohUrl);
            dialog.show();
        });
        // Select Backup / Restore -------------------------------------
        findViewById(R.id.llBackup).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            BackupDialog dialog = new BackupDialog(mActivity);
            dialog.show();
        });
        // Load Wallpaper from URL -------------------------------------
        findViewById(R.id.llWp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (!ApiConfig.get().wallpaper.isEmpty())
                    Toast.makeText(mContext, getString(R.string.mn_wall_load), Toast.LENGTH_SHORT).show();
                OkGo.<File>get(ApiConfig.get().wallpaper).execute(new FileCallback(requireActivity().getFilesDir().getAbsolutePath(), "wp") {
                    @Override
                    public void onSuccess(Response<File> response) {
                        ((BaseActivity) requireActivity()).changeWallpaper(true);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                    }
                });
            }
        });
        // Restore Default Wallpaper from system -------------------------
        findViewById(R.id.llWpRecovery).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            File wp = new File(requireActivity().getFilesDir().getAbsolutePath() + "/wp");
            if (wp.exists())
                wp.delete();
            ((BaseActivity) requireActivity()).changeWallpaper(true);
        });
        // Select Search Display Results ( Text or Picture ) -------------
        findViewById(R.id.llSearchView).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            int defaultPos = Hawk.get(HawkConfig.SEARCH_VIEW, 0);
            ArrayList<Integer> types = new ArrayList<>();
            types.add(0);
            types.add(1);
            SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
            dialog.setTip(getString(R.string.dia_search));
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    Hawk.put(HawkConfig.SEARCH_VIEW, value);
                    tvSearchView.setText(getSearchView(value));
                }

                @Override
                public String getDisplay(Integer val) {
                    return getSearchView(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                    return oldItem.intValue() == newItem.intValue();
                }
            }, types, defaultPos);
            dialog.show();
        });
        // Select App Language ( English / Chinese ) -----------------
        findViewById(R.id.llLocale).setOnClickListener(new View.OnClickListener() {
            private final int chkLang = Hawk.get(HawkConfig.HOME_LOCALE, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_LOCALE, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_locale));
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_LOCALE, value);
                        tvLocale.setText(getLocaleView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getLocaleView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(dialog12 -> {
                    if (chkLang != Hawk.get(HawkConfig.HOME_LOCALE, 0)) {
                        Intent intent = getActivity().getApplicationContext().getPackageManager().getLaunchIntentForPackage(getActivity().getApplication().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("useCache", true);
                        intent.putExtras(bundle);
                        getActivity().getApplicationContext().startActivity(intent);
                        //  android.os.Process.killProcess(android.os.Process.myPid());
                        //  System.exit(0);
                    }
                });
                dialog.show();
            }
        });
        // Select App Theme Color -------------------------------------
        findViewById(R.id.llTheme).setOnClickListener(new View.OnClickListener() {
            private final int chkTheme = Hawk.get(HawkConfig.THEME_SELECT, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.THEME_SELECT, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                types.add(3);
                types.add(4);
                types.add(5);
                types.add(6);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_theme));
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.THEME_SELECT, value);
                        tvTheme.setText(getThemeView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getThemeView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(dialog13 -> {
                    if (chkTheme != Hawk.get(HawkConfig.THEME_SELECT, 0)) {
                        Intent intent = getActivity().getApplicationContext().getPackageManager().getLaunchIntentForPackage(getActivity().getApplication().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("useCache", true);
                        intent.putExtras(bundle);
                        getActivity().getApplicationContext().startActivity(intent);
                        //  android.os.Process.killProcess(android.os.Process.myPid());
                        //  System.exit(0);
                    }
                });
                dialog.show();
            }
        });
        // About App -----------------------------------------------
        tvAbout.setText("V" + BuildConfig.VERSION_NAME);
        findViewById(R.id.llAbout).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            AboutDialog dialog = new AboutDialog(mActivity);
            dialog.show();
        });
        SettingActivity.callback = () -> findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }

    String getHomeRecName(int type) {
        if (type == 1) {
            return "站点推荐";
        } else if (type == 2) {
            return "观看历史";
        } else {
            return "豆瓣热播";
        }
    }

    String getSearchView(int type) {
        if (type == 0) {
            return "文字列表";
        } else {
            return "缩略图";
        }
    }

    String getLocaleView(int type) {
        if (type == 0) {
            return "中文";
        } else {
            return "英文";
        }
    }

    String getThemeView(int type) {
        if (type == 0) {
            return "奈飞";
        } else if (type == 1) {
            return "哆啦";
        } else if (type == 2) {
            return "百事";
        } else if (type == 3) {
            return "鸣人";
        } else if (type == 4) {
            return "小黄";
        } else if (type == 5) {
            return "八神";
        } else {
            return "樱花";
        }
    }

}