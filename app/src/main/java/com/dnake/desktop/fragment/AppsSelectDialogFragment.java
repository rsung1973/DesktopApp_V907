package com.dnake.desktop.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.AppsLabel;
import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.GridSpaceItemDecoration;
import com.dnake.desktop.R;
import com.dnake.desktop.SysService;
import com.dnake.desktop.adapter.AppsGridAdapter;
import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.model.DesktopMainLogger;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.desktop.utils.NavigationBarUtil;
import com.dnake.v700.utils;
import com.dnake.widget.Button2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppsSelectDialogFragment extends BaseDialogFragment implements AppsGridAdapter.Callback {
    private View inflaterView;
    private ImageView btnClose;
    private RecyclerView rvApps;
    private AppsGridAdapter mAdapter;
    private List<AppModel> appsList = new ArrayList<>();
    private int mType = 0;
    private List<AppModel> tmpList = new ArrayList<>();

    public AppsSelectDialogFragment(int type) {
        mType = type;
    }

    public AppsSelectDialogFragment(int type, List<AppModel> tmps) {
        mType = type;
        tmpList = tmps;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflaterView == null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_apps_select, null);
        }
        btnClose = (ImageView) inflaterView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rvApps = (RecyclerView) inflaterView.findViewById(R.id.rv_apps);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rvApps.setLayoutManager(gridLayoutManager);
        rvApps.addItemDecoration(new GridSpaceItemDecoration(2, DensityUtil.dip2px(getActivity(), 2), DensityUtil.dip2px(getActivity(), 2)));

        if (mType == 0) {//主界面底部菜单
            listAdd();
//            listAdd(new AppModel(10001, getString(R.string.app_call), null, null));
//            listAdd(new AppModel(10002, getString(R.string.app_monitor), null, null));
//            listAdd(new AppModel(10004, getString(R.string.app_quad_splitter), null, null));
//            listAdd(new AppModel(10005, getString(R.string.app_phonebook), null, null));
//            listAdd(new AppModel(10006, getString(R.string.app_message), null, null));
//            listAdd(new AppModel(10008, getString(R.string.app_qr_code), null, null));
//            listAdd(new AppModel(10009, getString(R.string.app_security), null, null));
//            listAdd(new AppModel(10010, getString(R.string.app_concierge), null, null));
//            listAdd(new AppModel(10013, getString(R.string.app_setting), null, null));
//            listAdd(new AppModel(10007, getString(R.string.app_dnd), null, null));
//            listAdd(new AppModel(10011, getString(R.string.app_lift_ctrl), null, null));
//            listAdd(new AppModel(10012, getString(R.string.app_sos), null, null));
        }
        mAdapter = new AppsGridAdapter(this.getActivity(), appsList);
        mAdapter.setCallback(this);
        rvApps.setAdapter(mAdapter);

        return inflaterView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = DensityUtil.dip2px(getActivity(), 750);
        layoutParams.height = DensityUtil.dip2px(getActivity(), 400);
        getDialog().getWindow().setAttributes(layoutParams);
        NavigationBarUtil.hideNavigationBar(getDialog().getWindow());
        NavigationBarUtil.clearFocusNotAle(getDialog().getWindow());
    }

    public AppModel getSelectedItem() {
        return selectedItem;
    }

    private AppModel selectedItem;

    @Override
    public void onEnterBar(View view, Object bean) {
        selectedItem = (AppModel) bean;
        dismiss();
    }

    @Override
    public void longClickSOS(View view) {
    }

    private void listAdd() {
        List<AppModel> list = DesktopMainLogger.loadSelectDefaultApps();
        list.addAll(loadInstalledApps());
        for (AppModel appModel : list) {
//            if (!DesktopMainLogger.isAppExist(appModel.getId()) && !isAppInTmp(appModel)) {
            if (!isAppInTmp(appModel)) {
                appsList.add(appModel);
            }
        }
    }

    private boolean isAppInTmp(AppModel appModel) {
        for (AppModel model : tmpList) {
            if (model.getName().equals(appModel.getName())) {
                return true;
            }
        }
        return false;
    }

    private List<AppModel> loadInstalledApps() {
        PackageManager pm = getActivity().getPackageManager(); // 获得PackageManager对象
        Intent it = new Intent(Intent.ACTION_MAIN, null);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(it, 0);
        ArrayList<AppModel> mApps = new ArrayList<AppModel>();
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        for (int i = 0; i < resolveInfos.size(); i++) {
            String activityName = resolveInfos.get(i).activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = resolveInfos.get(i).activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) resolveInfos.get(i).loadLabel(pm); // 获得应用程序的Label
            Drawable icon = resolveInfos.get(i).loadIcon(pm); // 获得应用程序图标

            if (!utils.eHomeMode && (pkgName.contains("com.google.android.inputmethod.pinyin") || pkgName.contains("com.android.quicksearchbox") || pkgName.contains("com.android.rk") || pkgName.contains("com.android.documentsui") || pkgName.contains("com.android.email") || pkgName.contains("com.dnake.apps") || pkgName.contains("com.dnake.desktop") || pkgName.contains("com.dnake.talk") || pkgName.contains("com.dnake.security") || pkgName.contains("com.dnake.smart") || pkgName.contains("com.dnake.eSettings") || pkgName.contains("com.android.calculator2") || pkgName.contains("com.android.calendar") || pkgName.contains("com.android.contacts") || pkgName.contains("com.android.gallery3d")
                    || pkgName.contains("com.android.music") || pkgName.contains("com.android.settings") || pkgName.contains("com.android.browser") || pkgName.contains("com.android.inputmethod.latin") || pkgName.contains("com.android.providers.downloads.ui") || pkgName.contains("com.softwinner.explore") || pkgName.contains("com.android.soundrecorder") || pkgName.contains("com.softwinner.fireplayer") || pkgName.contains("com.android.camera2") || pkgName.contains("com.android.chrome")))
                continue;

            // 为应用程序的启动Activity 准备Intent
            it = new Intent();
            it.setComponent(new ComponentName(pkgName, activityName));

            // 创建一个AppInfo对象，并赋值
            AppModel d = new AppModel(20000 + i, appLabel, icon, it, pkgName, activityName);
            mApps.add(d); // 添加至列表中
        }
        return mApps;
    }
}