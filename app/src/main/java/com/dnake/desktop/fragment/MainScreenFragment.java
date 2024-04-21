package com.dnake.desktop.fragment;

import static com.dnake.desktop.DesktopActivity.btnMore;
import static com.dnake.desktop.DesktopActivity.btnSecurity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.BaseFragment;
import com.dnake.desktop.DesktopActivity;
import com.dnake.desktop.R;
import com.dnake.desktop.SysReceiver;
import com.dnake.desktop.SysService;
import com.dnake.desktop.adapter.MainAppMenuAdapter;
import com.dnake.desktop.adapter.VarColumnGridLayoutManager;
import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.model.DesktopMainLogger;
import com.dnake.misc.AccuWeather;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainScreenFragment extends BaseFragment implements MainAppMenuAdapter.Callback, BaseDialogFragment.Callback {

    private TextView tvDate, tvTime, tvTimeQuantum, tvWeek;
    private TextView msg, sms, miss;
    private long dTs = 0;
    private Handler e_timer = null;
    private ScheduledThreadPoolExecutor timer = null;
    public int mDnd = 0;

    private RecyclerView mainMenuRv;
    private MainAppMenuAdapter mAdapter;
    private List<AppModel> mainAppList = new ArrayList<>();
    private VarColumnGridLayoutManager gridLayoutManager;

    private ConstraintLayout layoutMain;
    private LinearLayout layoutMainApps;
    private LinearLayout layoutEdit;
    private ImageView btnEditOk;
    private ImageView btnEditCancel;

    public static MainScreenFragment newInstance() {
        return new MainScreenFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {
        dTs = System.currentTimeMillis();
        e_timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (timer != null) doProcess();
            }
        };
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleWithFixedDelay(new tRun(), 1000, 1000, TimeUnit.MILLISECONDS);
        tvDate = (TextView) rootView.findViewById(R.id.tv_date);
        tvWeek = (TextView) rootView.findViewById(R.id.tv_week);
        tvTime = (TextView) rootView.findViewById(R.id.tv_time);
        tvTimeQuantum = (TextView) rootView.findViewById(R.id.tv_time_quantum);

        mainMenuRv = (RecyclerView) rootView.findViewById(R.id.rv_apps);
        layoutMain = (ConstraintLayout) rootView.findViewById(R.id.layout_main);
        layoutMainApps = (LinearLayout) rootView.findViewById(R.id.layout_main_apps);
        layoutEdit = (LinearLayout) rootView.findViewById(R.id.layout_edit);
        btnEditOk = (ImageView) rootView.findViewById(R.id.btn_edit_ok);
        btnEditCancel = (ImageView) rootView.findViewById(R.id.btn_edit_cancel);
        mainMenuRv.setOnTouchListener(new View.OnTouchListener() {//主页主菜单为空时，长按事件
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mAdapter.getIs_edit() != 1 && mAdapter.getItemCount() == 0) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            handler.postDelayed(runnable, 1000);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                        default:
                            handler.removeCallbacks(runnable);
                            break;
                    }
                    return false;
                } else {
                    return false;
                }
            }
        });
        btnEditOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.saveUpdate();
                mAdapter.setIs_edit(0);
                for (int i = 0; i < mAdapter.getmDatas().size(); i++) {
                    mainMenuRv.getChildAt(i).setScaleX(1.0f);
                    mainMenuRv.getChildAt(i).setScaleY(1.0f);
                }
                layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
                gridLayoutManager.setSpancount(mAdapter.getmDatas().size());
                btnMore.setEnabled(true);
                btnSecurity.setEnabled(true);
                (((DesktopActivity) getActivity()).vpMain).setScrollble(true);
                ((DesktopActivity) getActivity()).miss_nread = 0;
                ((DesktopActivity) getActivity()).sms_nread = 0;
                ((DesktopActivity) getActivity()).msg_nread = 0;
                mDnd = (SysReceiver.dnd == 0 ? 1 : 0);
            }
        });
        btnEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.cancelUpdate();
                mAdapter.setIs_edit(0);
                layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
                gridLayoutManager.setSpancount(mAdapter.getmDatas().size());
                btnMore.setEnabled(true);
                btnSecurity.setEnabled(true);
                (((DesktopActivity) getActivity()).vpMain).setScrollble(true);
                ((DesktopActivity) getActivity()).miss_nread = -1;
                ((DesktopActivity) getActivity()).sms_nread = -1;
                ((DesktopActivity) getActivity()).msg_nread = -1;
                mDnd = (SysReceiver.dnd == 0 ? 1 : 0);
            }
        });
        initMainMenu();
    }

    private ItemTouchHelper mItemTouchHelper;

    public void initMainMenu() {
        mainAppList = DesktopMainLogger.loadApps(getActivity());
        gridLayoutManager = new VarColumnGridLayoutManager(mContext, mainAppList.size());
        mainMenuRv.setLayoutManager(gridLayoutManager);
        mAdapter = new MainAppMenuAdapter(this.getActivity(), mainAppList);
        mAdapter.setCallback(this);
        mainMenuRv.setAdapter(mAdapter);

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (!(mAdapter.getIs_edit() == 1 && (mAdapter.getmDatas().size() != viewHolder.getAdapterPosition()))) {
//                    viewHolder.itemView.setScaleX(0.8f);
//                    viewHolder.itemView.setScaleY(0.8f);
//                } else {
                    viewHolder.itemView.setScaleX(1.0f);
                    viewHolder.itemView.setScaleY(1.0f);
                }
                final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition() >= (mAdapter.getmDatas().size() - 1) ? (mAdapter.getmDatas().size() - 1) : target.getAdapterPosition();
                Log.i("aaa", "_______target.getAdapterPosition()_____________" + target.getAdapterPosition());
                Log.i("aaa", "_______mAdapter.getmDatas().size()_____________" + mAdapter.getmDatas().size());
                Log.i("aaa", "_______toPosition_____________" + toPosition);
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mAdapter.getmDatas(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mAdapter.getmDatas(), i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * 长按选中Item的时候开始调用
             *
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder != null) {
                    if (mAdapter.getIs_edit() == 1 && (mAdapter.getmDatas().size() != viewHolder.getAdapterPosition())) {
                        viewHolder.itemView.setScaleX(0.8f);
                        viewHolder.itemView.setScaleY(0.8f);
                    }
                }
            }

            /**
             * 手指松开的时候还原
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
                mAdapter.setListData(mAdapter.getmDatas());
            }
        });
        mItemTouchHelper.attachToRecyclerView(mainMenuRv);
        if (mAdapter.getIs_edit() != 1) {
            layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
            gridLayoutManager.setSpancount(mAdapter.getmDatas().size());
            btnMore.setEnabled(true);
            btnSecurity.setEnabled(true);
            (((DesktopActivity) getActivity()).vpMain).setScrollble(true);
            if (appsSelectDialogFragment != null) {
                appsSelectDialogFragment.dismiss();
            }
        } else {
            layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
            gridLayoutManager.setSpancount(4);
            btnMore.setEnabled(false);
            btnSecurity.setEnabled(false);
            (((DesktopActivity) getActivity()).vpMain).setScrollble(false);
        }
    }

    @Override
    protected void initData() {
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
                if (timer != null) doProcess();
            }
        };
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleWithFixedDelay(new tRun(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DesktopActivity) getActivity()).miss_nread = -1;
        ((DesktopActivity) getActivity()).sms_nread = -1;
        ((DesktopActivity) getActivity()).msg_nread = -1;
        mDnd = (SysReceiver.dnd == 0 ? 1 : 0);
        initData();
    }

    @Override
    protected void stopView() {
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

    private AccuWeather aWeather = null;
    private Boolean wOK = false;
    private long wTs = 0;

    public void loadWeather(long cur_time) {
        if (wOK) {
            wOK = false;
            TextView tv = (TextView) rootView.findViewById(R.id.tv_temperature);
            String s = String.valueOf(AccuWeather.temp) + "℃";
            tv.setText(s);
            ImageView v = (ImageView) rootView.findViewById(R.id.iv_weather);
            if (AccuWeather.icon < 2) v.setImageResource(R.mipmap.main_weather_w0);
            else if (AccuWeather.icon < 5) v.setImageResource(R.mipmap.main_weather_w1);
            else if (AccuWeather.icon < 8) v.setImageResource(R.mipmap.main_weather_w2);
            else if (AccuWeather.icon < 11) v.setImageResource(R.mipmap.main_weather_w3);
            else if (AccuWeather.icon < 12) v.setImageResource(R.mipmap.main_weather_w2);
            else if (AccuWeather.icon < 13) v.setImageResource(R.mipmap.main_weather_w4);
            else if (AccuWeather.icon < 18) v.setImageResource(R.mipmap.main_weather_w5);
            else if (AccuWeather.icon < 19) v.setImageResource(R.mipmap.main_weather_w4);
            else if (AccuWeather.icon < 20) v.setImageResource(R.mipmap.main_weather_w6);
            else if (AccuWeather.icon < 22) v.setImageResource(R.mipmap.main_weather_w7);
            else if (AccuWeather.icon < 23) v.setImageResource(R.mipmap.main_weather_w6);
            else if (AccuWeather.icon < 24) v.setImageResource(R.mipmap.main_weather_w7);
            else if (AccuWeather.icon < 30) v.setImageResource(R.mipmap.main_weather_w6);
            else if (AccuWeather.icon < 33) v.setImageResource(R.mipmap.main_weather_w0);
            else if (AccuWeather.icon < 35) v.setImageResource(R.mipmap.main_weather_w8);
            else if (AccuWeather.icon < 39) v.setImageResource(R.mipmap.main_weather_w9);
            else if (AccuWeather.icon < 43) v.setImageResource(R.mipmap.main_weather_w10);
            else if (AccuWeather.icon < 45) v.setImageResource(R.mipmap.main_weather_w11);
            else v.setImageResource(R.mipmap.main_weather_w0);
        }
        if (((TextView) rootView.findViewById(R.id.tv_temperature)).getText().toString().contains("--") || Math.abs(cur_time - wTs) > 60 * 60 * 1000 || (AccuWeather.haved == false && Math.abs(cur_time - wTs) > 5 * 60 * 1000)) {
            wTs = cur_time;
            if (aWeather == null) {
                aWeather = new AccuWeather() {
                    @Override
                    public void onFinished() {
                        wOK = true;
                    }
                };
            }
            aWeather.start();
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (timerThread != null) {
//            timerThread.interrupt();
//            timerThread = null;
//        }
//    }

    private boolean isBootDone = false;

    private void doProcess() {
        if (!isAdded()) {
            return;
        }
        if (isVisible || !isBootDone) {
//            if (!isBootDone && utils.getVersionNameAll().contains("902")) {
//                Log.i("aaa", "_________902!!!!!!!???????????_______1080000___");
//                utils.setCpuMaxScale(1080000);
//            }
            long current_time = System.currentTimeMillis();
//            if (Math.abs(current_time - dTs) >= 15 * 1000 || dTs == 0) {
            if (Math.abs(current_time - dTs) >= 15 * 1000 || dTs == 0 || TextUtils.isEmpty(tvDate.getText().toString())) {
                dTs = current_time;
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
                tvDate.setText(date);
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
                tvWeek.setText(week);
                String time = sTimeFormat.format(new java.util.Date());
                tvTime.setText(time);
                if (sys.time.is24 != 1) {
                    tvTimeQuantum.setVisibility(View.VISIBLE);
                    int apm = now.get(Calendar.AM_PM);
                    if (apm == 0) {
                        tvTimeQuantum.setText(getString(R.string.str_am));
                    } else {
                        tvTimeQuantum.setText(getString(R.string.str_pm));
                    }
                } else {
                    tvTimeQuantum.setVisibility(View.GONE);
                }
//                if (utils.getVersionNameAll().contains("902")) {
////                    utils.setCpuMaxScale(1536000);
//                    Log.i("aaa", "_________902!!!!!!!_______1080000___");
//                    utils.setCpuMaxScale(1080000);
//                }
                isBootDone = true;
            }
            this.loadWeather(current_time);
            this.loadPrompt();
        }
    }

    @Override
    public void onItemLongPress(RecyclerView.ViewHolder vh) {
        if (mAdapter.getIs_edit() != 1) {
            mAdapter.tempData();
            mAdapter.setIs_edit(1);
            layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
//            gridLayoutManager.setSpancount(mAdapter.getItemCount());
            gridLayoutManager.setSpancount(4);
            btnMore.setEnabled(false);
            btnSecurity.setEnabled(false);
            (((DesktopActivity) getActivity()).vpMain).setScrollble(false);
        } else {
            mItemTouchHelper.startDrag(vh);
        }
    }

    @Override
    public void onItemClick(View view, AppModel model) {
        if (mAdapter.getIs_edit() != 1) {
//            onScaleAnimationBySpringWayOne(view);
            if (model.getId() == 10013) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.apps", "com.dnake.setting.activity.SettingsActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                getActivity().startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (model.getId() == 10001) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallMainActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                getActivity().startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (model.getId() == 10009) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.security", "com.dnake.security.MainActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                getActivity().startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (model.getId() == 10006) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.apps", "com.dnake.message.activity.MessageActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (model.getId() == 10008) {
                ((DesktopActivity) mContext).popupQRCode();
            } else if (model.getId() == 10010) {
                ((DesktopActivity) mContext).popupConcierge();
            } else if (model.getId() == 10011) {
//            ((DesktopActivity) mContext).popupLiftCtrlConfirm();
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.smart", "com.dnake.smart.ElevatorLabel");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
            } else if (model.getId() == 10002) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.MonitorActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
//            else if (model.getName().equals(getString(R.string.app_camera))) {
//                Intent it = new Intent();
//                ComponentName comp = new ComponentName("com.dnake.security", "com.dnake.security.activity.IpcActivity");
//                it.setComponent(comp);
//                it.setAction("android.intent.action.VIEW");
//                startActivity(it);
//                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            }
            else if (model.getId() == 10005) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallMainActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                it.putExtra("tab_index", 2);
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (model.getId() == 10007) {
                Intent it = new Intent("com.dnake.broadcast");
                it.putExtra("event", "com.dnake.talk.dnd");
                it.putExtra("data", mDnd == 0 ? 1 : 0);
                mContext.sendBroadcast(it);
            } else if (model.getId() == 10004) {
                Intent it = new Intent();
                ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.MonitorQuadSplitterActivity");
                it.setComponent(comp);
                it.setAction("android.intent.action.VIEW");
                startActivity(it);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                if (!TextUtils.isEmpty(model.getPackageName()) && !TextUtils.isEmpty(model.getAction())) {
                    utils.openThirdApp(model.getPackageName(), model.getAction());
                } else if (model.getIntent() != null) {
                    startActivity(model.getIntent());
                }
            }
        }
    }

    private AppsSelectDialogFragment appsSelectDialogFragment;

    @Override
    public void onAddClick(View view) {
        appsSelectDialogFragment = new AppsSelectDialogFragment(0, mAdapter.getmDatas());//0:主界面底部菜单; 1:右上角菜单
        appsSelectDialogFragment.setCallback(this);
        appsSelectDialogFragment.show(getActivity().getSupportFragmentManager(), "");
    }

    public void loadPrompt() {
        if (((DesktopActivity) getActivity()).sms_nread != SysReceiver.sms_nread) {
            ((DesktopActivity) getActivity()).sms_nread = SysReceiver.sms_nread;
            if (mAdapter.getAppIndexById(10006) != -1) {//message
                if (SysReceiver.sms_nread > 0) {
                    if (mAdapter.getIs_edit() != 1) {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10006))).getChildAt(3)).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10006))).getChildAt(3)).setVisibility(View.GONE);
                    }
                    if (SysReceiver.sms_nread > 99) {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10006))).getChildAt(3)).setText("99+");
                    } else {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10006))).getChildAt(3)).setText(SysReceiver.sms_nread + "");
                    }
                } else {
                    ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10006))).getChildAt(3)).setVisibility(View.GONE);
                }
            }
        }
        if (((DesktopActivity) getActivity()).msg_nread != SysReceiver.msg_nread) {
            ((DesktopActivity) getActivity()).msg_nread = SysReceiver.msg_nread;
        }
        if (((DesktopActivity) getActivity()).miss_nread != SysReceiver.miss_nread) {
            ((DesktopActivity) getActivity()).miss_nread = SysReceiver.miss_nread;
            if (mAdapter.getAppIndexById(10001) != -1) {//call
                if (SysReceiver.miss_nread > 0) {
                    if (mAdapter.getIs_edit() != 1) {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10001))).getChildAt(3)).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10001))).getChildAt(3)).setVisibility(View.GONE);
                    }
                    if (SysReceiver.miss_nread > 99) {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10001))).getChildAt(3)).setText("99+");
                    } else {
                        ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10001))).getChildAt(3)).setText(SysReceiver.miss_nread + "");
                    }
                } else {
                    ((TextView) ((FrameLayout) mainMenuRv.getChildAt(mAdapter.getAppIndexById(10001))).getChildAt(3)).setVisibility(View.GONE);
                }
            }
        }
        if (mDnd != SysReceiver.dnd) {
            mDnd = SysReceiver.dnd;
            int dndPosition = mAdapter.getIndexByItemID(10007);
            if (dndPosition >= 0) {
                View dndView = mainMenuRv.getLayoutManager().findViewByPosition(mAdapter.getIndexByItemID(10007));
                if (mDnd == 0) {
                    ((ImageView) dndView.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_dnd_off);
                } else {
                    ((ImageView) dndView.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_dnd_on);
                }
            }
        }
    }

    @Override
    public void doCancel() {
        if (appsSelectDialogFragment != null && appsSelectDialogFragment.getSelectedItem() != null) {
            mAdapter.addItem(appsSelectDialogFragment.getSelectedItem());
            mAdapter.notifyDataSetChanged();
            gridLayoutManager.setSpancount(4);
        }
        this.getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (mAdapter.getIs_edit() != 1) {
                mAdapter.tempData();
                mAdapter.setIs_edit(1);
                layoutEdit.setVisibility((mAdapter.getIs_edit() == 1) ? View.VISIBLE : View.GONE);
//            gridLayoutManager.setSpancount(mAdapter.getItemCount());
                gridLayoutManager.setSpancount(4);
                btnMore.setEnabled(false);
                btnSecurity.setEnabled(false);
                (((DesktopActivity) getActivity()).vpMain).setScrollble(false);
            }
        }
    };

    private class tRun implements Runnable {

        public void run() {
            if (e_timer != null) e_timer.sendMessage(e_timer.obtainMessage());
        }
    }
}