package com.dnake.desktop;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.dnake.desktop.adapter.BaseFragmentPagerAdapter;
import com.dnake.desktop.adapter.PhonebookListAdapter;
import com.dnake.desktop.fragment.AppsFragment;
import com.dnake.desktop.fragment.LiftCtrlDialogFragment;
import com.dnake.desktop.fragment.MainScreenFragment;
import com.dnake.desktop.fragment.OneKeySecurityDialogFragment;
import com.dnake.desktop.fragment.QrCodeDialogFragment;
import com.dnake.desktop.fragment.SosDialogFragment;
import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.model.ContactLogger;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.desktop.utils.NavigationBarUtil;
import com.dnake.desktop.utils.Utils;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;
import com.dnake.widget.MyViewPager;
import com.hjq.toast.ToastUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DesktopActivity extends FragmentActivity implements PhonebookListAdapter.Callback, BaseDialogFragment.Callback {
    public void clearFragmentsBeforeCreate() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commitNow();
    }

    private final int MAX_ITEM = 18;
    private DrawerLayout layoutDrawer;
    private LinearLayout layoutStatusBar;
    public static LinearLayout layoutMain;
    private MagicIndicator dotsIndicator;

    private ImageView ivPhonebookMini;
    //    private ImageView ivLogo;
    private ImageView ivComStatus;
    private ImageView ivMainSipStatus;
    //    private ImageView ivMainComStatus;
    public static TextView tvDateTop;
    private BaseFragmentPagerAdapter pagerAdapter;

    private RecyclerView rvPhonebook;
    private PhonebookListAdapter mAdapter;
    //    private List<ContactLogger.data> phonebookList = new ArrayList<>();
    //    private List<ContactLogger.data> syncDevList = new ArrayList<>();

    public MyViewPager vpMain;
    private LinearLayout layoutTopMenu;
    public static ImageView btnSecurity, btnSwitch, btnMore;
    private boolean isTopMenuExpand = false;
    private Handler e_timer = null;
    public static ScheduledThreadPoolExecutor timer = null;

    public static int sms_nread = -1;
    public static int msg_nread = -1;
    public static int miss_nread = -1;
    public static int security = -1;

    //Apps
    private int totalAppPage = 1;
    private final int PAGE_MAX_ITEM = 18;
    public List<AppModel> appsList = new ArrayList<>();
    public List<BaseFragment> appsFragments;
    private int cur_page = 0;
    private boolean isDeskFront = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        } else {
            hideBottomUIMenu();
            this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    hideBottomUIMenu();
                }
            });
            setContentView(R.layout.activity_desktop);
            if (savedInstanceState != null) {
                clearFragmentsBeforeCreate();
            }
            initApps();
            initView();
        }
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = 0x00200000 | 0x00400000 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.INVISIBLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initPhonebookData() {
        List<ContactLogger.data> list = new ArrayList<>();
        dmsg req = new dmsg();
        dxml p = new dxml();
        req.to("/ui/desktop/load_slaves", null);
        p.parse(req.mBody);
        int max = p.getInt("/params/max", 0);
        for (int i = 0; i < max; i++) {
            String name = p.getText("/params/slaves_" + i + "/name", "");
            String sipid = p.getText("/params/slaves_" + i + "/sipid", "");
            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(sipid)) {
                ContactLogger.data c = new ContactLogger.data();
                c.name = name;
                c.type = 3;
                c.sipid = sipid;
                list.add(c);
            }
        }
        for (ContactLogger.data data : ContactLogger.loadAllList()) {
            list.add(data);
        }
        mAdapter = new PhonebookListAdapter(DesktopActivity.this, list);
        mAdapter.setCallback(this);
        rvPhonebook.setAdapter(mAdapter);
    }

    private void initApps() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        appsList = new ArrayList<>();
        appsList.add(new AppModel(10001, getString(R.string.app_call), null, null, "", ""));
        appsList.add(new AppModel(10002, getString(R.string.app_monitor), null, null, "", ""));
//        appsList.add(new AppModel(10003, getString(R.string.app_camera), null, null));
        appsList.add(new AppModel(10004, getString(R.string.app_quad_splitter), null, null, "", ""));
        appsList.add(new AppModel(10005, getString(R.string.app_phonebook), null, null, "", ""));
        appsList.add(new AppModel(10006, getString(R.string.app_message), null, null, "", ""));
        appsList.add(new AppModel(10007, getString(R.string.app_dnd), null, null, "", ""));
        appsList.add(new AppModel(10008, getString(R.string.app_qr_code), null, null, "", ""));
        appsList.add(new AppModel(10009, getString(R.string.app_security), null, null, "", ""));
        appsList.add(new AppModel(10010, getString(R.string.app_concierge), null, null, "", ""));
        appsList.add(new AppModel(10011, getString(R.string.app_lift_ctrl), null, null, "", ""));
        appsList.add(new AppModel(10012, getString(R.string.app_sos), null, null, "", ""));
        appsList.add(new AppModel(10013, getString(R.string.app_setting), null, null, "", ""));
        PackageManager pm = DesktopActivity.this.getPackageManager(); // 获得PackageManager对象
        Intent it = new Intent(Intent.ACTION_MAIN, null);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(it, 0);
        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo reInfo = resolveInfos.get(i);
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            if (!utils.eHomeMode && (pkgName.contains("com.google.android.inputmethod.pinyin") || pkgName.contains("com.android.quicksearchbox") || pkgName.contains("com.android.rk") || pkgName.contains("com.android.documentsui") || pkgName.contains("com.android.email") || pkgName.contains("com.dnake.apps") || pkgName.contains("com.dnake.desktop") || pkgName.contains("com.dnake.talk") || pkgName.contains("com.dnake.security") || pkgName.contains("com.dnake.smart") || pkgName.contains("com.dnake.eSettings") || pkgName.contains("com.android.calculator2") || pkgName.contains("com.android.calendar") || pkgName.contains("com.android.contacts") || pkgName.contains("com.android.gallery3d") || pkgName.contains("com.android.music") || pkgName.contains("com.android.settings") || pkgName.contains("com.android.browser") || pkgName.contains("com.android.inputmethod.latin") || pkgName.contains("com.android.providers.downloads.ui") || pkgName.contains("com.softwinner" + ".explore") || pkgName.contains("com.android.soundrecorder") || pkgName.contains("com.softwinner.fireplayer") || pkgName.contains("com.android.camera2") || pkgName.contains("com.android.chrome")))
                continue;
            it = new Intent();
            it.setComponent(new ComponentName(pkgName, activityName));
            AppModel d = new AppModel(10020 + i, appLabel, icon, it, pkgName, activityName);
            appsList.add(d); // 添加至列表中
        }
//                mainHandler.sendEmptyMessageAtTime(0, 100);
//            }
//        }).start();
    }

    private void initAppsFragments() {
        totalAppPage = (int) Math.ceil(appsList.size() * 1.0 / MAX_ITEM);
        for (int i = 0; i < totalAppPage; i++) {
            AppsFragment fragment = AppsFragment.newInstance();
            fragment.setTAG(i + "");
            if ((i + 1) * MAX_ITEM > appsList.size()) {
                List<AppModel> list1 = appsList.subList(i * MAX_ITEM, appsList.size());
                fragment.setAppsList(list1);
            } else {
                List<AppModel> list2 = appsList.subList(i * MAX_ITEM, (i + 1) * MAX_ITEM);
                fragment.setAppsList(list2);
            }
            appsFragments.add(fragment);
        }
    }

    private void refreshAppsPages() {
        initApps();
        MainScreenFragment fragment = (MainScreenFragment) appsFragments.get(0);
        appsFragments = new ArrayList<>();
        appsFragments.add(fragment);
        initAppsFragments();
        pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), appsFragments);
        vpMain.setAdapter(pagerAdapter);
    }

    private void initView() {
        appsFragments = new ArrayList<>();
        appsFragments.add(MainScreenFragment.newInstance());
        initAppsFragments();

        layoutTopMenu = (LinearLayout) findViewById(R.id.layout_top_menu);
        btnSecurity = (ImageView) findViewById(R.id.btn_security);
        btnSwitch = (ImageView) findViewById(R.id.btn_switch);
        btnMore = (ImageView) findViewById(R.id.btn_more);
        btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupOnekeySecurity();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTopMenuExpand) {
                    isTopMenuExpand = false;
                    initClose();
                } else {
                    isTopMenuExpand = true;
                    initExpand();
                }
            }
        });
        ivPhonebookMini = (ImageView) findViewById(R.id.iv_mini_phonebook);
//        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivComStatus = (ImageView) findViewById(R.id.iv_com_status);
        tvDateTop = (TextView) findViewById(R.id.tv_date_top);
        layoutMain = (LinearLayout) findViewById(R.id.layout_main);
        layoutStatusBar = (LinearLayout) findViewById(R.id.layout_status_bar);

        ivMainSipStatus = (ImageView) findViewById(R.id.iv_main_sip_status);
//        ivMainComStatus = (ImageView) findViewById(R.id.iv_main_com_status);
        vpMain = (MyViewPager) findViewById(R.id.vp_main);
        pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), appsFragments);
        vpMain.setAdapter(pagerAdapter);
//        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
//        vpMain.setAdapter(pagerAdapter);

        dotsIndicator = (MagicIndicator) findViewById(R.id.dots_indicator);
        CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(totalAppPage + 1);
        circleNavigator.setRadius(5);
        circleNavigator.setCircleSpacing(20);
        circleNavigator.setCircleColor(Color.WHITE);
        circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                vpMain.setCurrentItem(index);
            }
        });
        dotsIndicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(dotsIndicator, vpMain);

        rvPhonebook = (RecyclerView) findViewById(R.id.rv_phonebook);
        rvPhonebook.setLayoutManager(new LinearLayoutManager(this));
        rvPhonebook.addItemDecoration(new GridSpaceItemDecoration(1, DensityUtil.dip2px(DesktopActivity.this, 1), DensityUtil.dip2px(DesktopActivity.this, 1)));
//        initPhonebookData();

        layoutDrawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        layoutDrawer.setScrimColor(Color.TRANSPARENT);
        int screenWidth = getScreenWidth();
        layoutDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                int width = screenWidth - Math.round(DensityUtil.dip2px(DesktopActivity.this, 300) * slideOffset);
                ViewGroup.LayoutParams params1 = layoutStatusBar.getLayoutParams();
                ViewGroup.LayoutParams params2 = vpMain.getLayoutParams();
                params1.width = width;
                params2.width = width;
                layoutStatusBar.setLayoutParams(params1);
                vpMain.setLayoutParams(params2);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                ivPhonebookMini.setImageResource(R.mipmap.ic_phonebook_mini);
                initPhonebookData();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                ivPhonebookMini.setImageResource(R.mipmap.ic_phonebook_mini_off);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (mViewPagerState == 1 && mViewPagerIndex == position) {
                if ((mViewPagerState == 1 && vpMain.getCurrentItem() == 0) || (mViewPagerState == 1 && Utils.isRTL() && vpMain.getCurrentItem() == appsFragments.size())) {
                    if (position == 0 && positionOffset == 0.0 && positionOffsetPixels == 0.0 && !layoutDrawer.isDrawerOpen(Gravity.LEFT)) {
                        layoutDrawer.openDrawer(Gravity.LEFT);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    cur_page = 0;
                    layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    tvDateTop.setVisibility(View.GONE);
                    layoutTopMenu.setVisibility(View.VISIBLE);
//                    ivLogo.setVisibility(View.VISIBLE);
//                    ivComStatus.setVisibility(View.GONE);
                } else {
                    cur_page = position;
                    layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    tvDateTop.setVisibility(View.VISIBLE);
                    layoutTopMenu.setVisibility(View.GONE);
//                    ivLogo.setVisibility(View.GONE);
//                    ivComStatus.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mViewPagerState = state;
                if (state == 1) {
                    mViewPagerIndex = vpMain.getCurrentItem();
                }
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmsg req = new dmsg();
                req.to("/apps/screen_off", null);
            }
        });
        e_timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (utils.getVersionNameAll().contains("902")) {
                    utils.setCpuMaxScale(1080000);
                }
                if (isDeskFront) {
                    refreshStatus();
                }
            }
        };
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleWithFixedDelay(new tRun(), 1000, 1000, TimeUnit.MILLISECONDS);
        if (SysService.mContext == null) {
            Intent intent = new Intent(this, SysService.class);
            this.startService(intent);
        }
    }


    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            initView();
        }
    };

    private int mViewPagerIndex = 0;
    private int mViewPagerState = 0;

    private Thread timerThread = null;

    private void tStart() {
        if (e_timer != null) {
            e_timer.removeCallbacksAndMessages(null);
            e_timer = null;
        }
        if (timer != null) {
//            timer.cancel();
//            timer = null;
            timer.shutdownNow();
            timer = null;
        }
        e_timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isDeskFront) {
                    refreshStatus();
                }
            }
        };
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleWithFixedDelay(new tRun(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void tStop() {
        if (e_timer != null) {
            e_timer.removeCallbacksAndMessages(null);
            e_timer = null;
        }
        if (timer != null) {
//            timer.cancel();
//            timer = null;
            timer.shutdownNow();
            timer = null;
        }
    }

    @Override
    public void onEnterDetail(int index, ContactLogger.data bean) {
    }

    @Override
    public void doCancel() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onClickSelect(View view, int index, ContactLogger.data bean) {
        onScaleAnimationBySpringWayOne(view);
        if (!TextUtils.isEmpty(bean.apartmentNo) || !TextUtils.isEmpty(bean.sipid) || !TextUtils.isEmpty(bean.ipAddr)) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallQuickActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            it.putExtra("dev_name", bean.name);
            it.putExtra("call_type", bean.type);
            it.putExtra("room_no", bean.apartmentNo);
            it.putExtra("sip_id", bean.sipid);
            it.putExtra("ip_addr", bean.ipAddr);
            startActivity(it);
        } else {
            ToastUtils.show(DesktopActivity.this.getString(R.string.prompt_invalid_call_info));
        }
    }

    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    private void onScaleAnimationBySpringWayOne(View view) {
        SpringAnimation animationX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.0f);
        SpringAnimation animationY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.0f);
        animationX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationX.setStartValue(0.8f);
        animationY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationY.setStartValue(0.8f);
        animationX.start();
        animationY.start();
    }

    public void popupOnekeySecurity() {
        if (getSecurityDefence() == 0 || isLoginOk()) {
            OneKeySecurityDialogFragment oneKeySecurityDialogFragment = new OneKeySecurityDialogFragment();
            oneKeySecurityDialogFragment.setCallback(this);
            oneKeySecurityDialogFragment.show(getSupportFragmentManager(), "");
        } else {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_system_login, (ViewGroup) findViewById(R.id.dialog_main));
            EditText etPwd = (EditText) layout.findViewById(R.id.et_pwd);
            AlertDialog.Builder builder = new AlertDialog.Builder(DesktopActivity.this);
            builder.setView(layout);
            builder.setTitle(R.string.dialog_title_user);
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String passwd = etPwd.getText().toString();
                    if (isLoginPwd(passwd)) {
                        OneKeySecurityDialogFragment oneKeySecurityDialogFragment = new OneKeySecurityDialogFragment();
                        oneKeySecurityDialogFragment.setCallback(DesktopActivity.this);
                        oneKeySecurityDialogFragment.show(getSupportFragmentManager(), "");
                    } else {
                        dmsg req = new dmsg();
                        req.to("/apps/sound_err", null);
                    }
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DesktopActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            });
            AlertDialog ad = builder.create();
            ad.setCanceledOnTouchOutside(false);
//            ad.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ad.show();
            NavigationBarUtil.hideNavigationBar(ad.getWindow());
            NavigationBarUtil.clearFocusNotAle(ad.getWindow());
        }
    }

    public void popupQRCode() {
        QrCodeDialogFragment qrCodeDialogFragment = new QrCodeDialogFragment();
        qrCodeDialogFragment.setCallback(this);
        qrCodeDialogFragment.show(getSupportFragmentManager(), "");
    }

    public void popupConcierge() {
        Intent it = new Intent();
        ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallQuickActivity");
        it.setComponent(comp);
        it.setAction("android.intent.action.VIEW");
        it.putExtra("type", 1);
        startActivity(it);
    }

    public void popupLiftCtrl() {
        LiftCtrlDialogFragment liftCtrlDialogFragment = new LiftCtrlDialogFragment();
        liftCtrlDialogFragment.setCallback(this);
        liftCtrlDialogFragment.show(getSupportFragmentManager(), "");
    }

    public void popupSOS() {
        SosDialogFragment sosDialogFragment = new SosDialogFragment();
        sosDialogFragment.setCallback(this);
        sosDialogFragment.show(getSupportFragmentManager(), "");
    }

    public static long dTs = System.currentTimeMillis();
    public static long web_dTs = System.currentTimeMillis();

    private void refreshStatus() {

        if (Math.abs(System.currentTimeMillis() - web_dTs) >= 15 * 1000) {
            web_dTs = System.currentTimeMillis();
            getNetStatus();//网络状态
            getSipStatus();//sip状态
            initSecurityStatus();

            if (utils.getLocalIp() != null && !SysReceiver.loadTuyaQr) {
                dmsg req = new dmsg();
                Log.i("aaa", "______________/apps/tuya/do_load_tuya_qr_________!!!!!___________" + SysReceiver.loadTuyaQr);
                req.to("/apps/tuya/do_load_tuya_qr", null);
            }
        }
        if (Math.abs(System.currentTimeMillis() - dTs) >= 30 * 1000 || SysReceiver.need_refresh_time) {
            boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME);
            if (autoTimeEnabled) {
                dmsg req = new dmsg();
                dxml p = new dxml();
                p.setText("/params/cmd", "settings put global auto_time 1");
                req.to("/upgrade/root/cmd", p.toString());
            }
            dTs = System.currentTimeMillis();
            refreshDate();
        }
        if (SysReceiver.need_refresh_apps != 0) {
            SysReceiver.need_refresh_apps = 0;
//            SysReceiver.need_refresh_apps = 0;
            if (appsFragments != null) {
                refreshAppsPages();
                ((MainScreenFragment) appsFragments.get(0)).initMainMenu();
                vpMain.setCurrentItem(cur_page);
            }
        }
    }

    private void getNetStatus() {
        dmsg req = new dmsg();
        if (req.to("/apps/net_status", null) != 200) return;
        dxml versionInfo = new dxml();
        versionInfo.parse(req.mBody);
        String status = versionInfo.getText("/params/status", "false");
        Drawable d;
        if (status.equals("true")) {
            d = getResources().getDrawable(R.mipmap.ic_pc);
        } else {
            d = getResources().getDrawable(R.mipmap.ic_pc_off);
        }
        ivComStatus.setImageDrawable(d);
    }

    private void getSipStatus() {
        dmsg req = new dmsg();
        dxml p = new dxml();
        if (req.to("/ui/sip_status/read", null) == 200) {
            p.parse(req.mBody);
            int status = p.getInt("/params/sip_status", 0);//0:disable 1:register success 2:register failed
            if (status == 1) {
                ivMainSipStatus.setVisibility(View.VISIBLE);
            } else {
                ivMainSipStatus.setVisibility(View.GONE);
            }
        } else {
            ivMainSipStatus.setVisibility(View.GONE);
        }
    }

    private void initSecurityStatus() {
        int defenceType = getSecurityDefence();
        switch (defenceType) {
            case 0:
                btnSecurity.setImageResource(R.mipmap.ic_top_security_off);
                break;
            case 1:
                btnSecurity.setImageResource(R.mipmap.ic_top_security_out);
                break;
            case 2:
                btnSecurity.setImageResource(R.mipmap.ic_top_security_home);
                break;
            case 3:
                btnSecurity.setImageResource(R.mipmap.ic_top_security_sleep);
                break;
        }
    }

    public void refreshDate() {
        SysReceiver.need_refresh_time = false;
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        switch (sys.time.format) {
            case 0:
                sDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                break;
            case 1:
                sDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                break;
            case 2:
                sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        SimpleDateFormat sTimeFormat;
        if (sys.time.is24 == 1) {
            sTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            sTimeFormat = new SimpleDateFormat("hh:mm");
        }
        String date = sDateFormat.format(new java.util.Date());
        String week = String.valueOf(now.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(week)) {
            week = getString(R.string.str_week_sun);
        } else if ("2".equals(week)) {
            week = getString(R.string.str_week_mon);
        } else if ("3".equals(week)) {
            week = getString(R.string.str_week_tues);
        } else if ("4".equals(week)) {
            week = getString(R.string.str_week_wed);
        } else if ("5".equals(week)) {
            week = getString(R.string.str_week_thur);
        } else if ("6".equals(week)) {
            week = getString(R.string.str_week_fri);
        } else if ("7".equals(week)) {
            week = getString(R.string.str_week_sat);
        }
        String time = sTimeFormat.format(new java.util.Date());
        if (sys.time.is24 != 1) {
            int apm = now.get(Calendar.AM_PM);
            tvDateTop.setText(date + " " + week + " " + time + ((apm == 0) ? getString(R.string.str_am) : getString(R.string.str_pm)));
        } else {
            tvDateTop.setText(date + " " + week + " " + time);
        }
    }

    private int getSecurityDefence() {
        dmsg req = new dmsg();
        dxml p = new dxml();
        req.to("/security/defence_read", null);
        p.parse(req.mBody);
        int defence = p.getInt("/params/defence", 0);
        return defence;
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME);
        if (autoTimeEnabled) {
            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setText("/params/cmd", "settings put global auto_time 1");
            req.to("/upgrade/root/cmd", p.toString());
        }
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        layoutMain.setBackground(Utils.path2Drawable(this, Utils.getDesktopBgPath()));
        getNetStatus();
        getSipStatus();
        initSecurityStatus();
        refreshDate();
        this.tStart();
    }

    private boolean getAutoState(String name) {
        try {
            return Settings.System.getInt(this.getContentResolver(), name) > 0;
        } catch (Settings.SettingNotFoundException snfe) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDeskFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isDeskFront = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int cur_third_num = 0;
        for (AppModel app : appsList) {
            if (app.getId() >= 10020) {
                cur_third_num++;
            }
        }
        if (cur_third_num != utils.getThirdAppsNum(SysService.mContext)) {
            if (appsFragments != null) {
                refreshAppsPages();
                ((MainScreenFragment) appsFragments.get(0)).initMainMenu();
                vpMain.setCurrentItem(cur_page);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.tStop();
        utils.eHome.idle = 0;
    }

    @Override
    public void onDestroy() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onDestroy();
        this.tStop();
    }

    private boolean isLoginOk() {
        dmsg req = new dmsg();
        dxml p = new dxml();
        req.to("/security/is_login_ok", null);
        p.parse(req.mBody);
        int isOK = p.getInt("/params/is_ok", 0);
        return isOK == 1 ? true : false;
    }

    private boolean isLoginPwd(String passwd) {
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setText("/params/password", passwd);
        req.to("/security/is_password_ok", p.toString());
        p.parse(req.mBody);
        int isOK = p.getInt("/params/is_ok", 0);
        return isOK == 1 ? true : false;
    }

    private AutoTransition autoTransition;

    public void initExpand() {
        btnSecurity.setVisibility(View.VISIBLE);
        btnMore.setVisibility(View.VISIBLE);
        btnMore.setImageResource(R.mipmap.ic_right);
        btnSwitch.setVisibility(View.VISIBLE);
        //开始动画
        beginDelayedTransition(layoutTopMenu);
    }

    private void initClose() {
        btnSecurity.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        btnMore.setImageResource(R.mipmap.ic_left);
        btnSwitch.setVisibility(View.VISIBLE);
        //开始动画
        beginDelayedTransition(layoutTopMenu);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedTransition(ViewGroup view) {
        autoTransition = new AutoTransition();
        autoTransition.setDuration(300);
        TransitionManager.beginDelayedTransition(view, autoTransition);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class tRun implements Runnable {

        public void run() {
            if (e_timer != null) e_timer.sendMessage(e_timer.obtainMessage());
        }
    }
}