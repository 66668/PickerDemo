package com.sjy.roompicker.roompicker;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sjy.roompicker.R;
import com.sjy.roompicker.roompicker.utils.CacheUtils;
import com.sjy.roompicker.roompicker.utils.TouchConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 新版房间筛选器（兼容非触屏设备），统一处理界面
 * <p>
 * 0102设计要求：
 * 触屏
 * 1. 层数大于等于8层，显示层，UI显示占50%屏高度，
 * 2 层数小于8,不显示层，房间数据全部显示。
 * 非触屏
 * 1. 保持原有设计，呼叫为按键触发
 * <p>
 * sjy 2019-12-25
 */
public class RoomPicker extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "RJ_ROOM";
    private static final int USER_LEVEL = 6;
    //==============布局/控件==============
    private Context context;
    private RelativeLayout ly_roomTitle_01, ly_roomTitle_02, ly_roomTitle_03, ly_roomTitle_04, ly_roomTitle_05;
    private TextView room_title_01, room_title_02, room_title_03, room_title_04, room_title_05, tv_pager, tv_uppager, tv_nextpager, tv_errorData, img_uppager, img_nextpager;
    private LinearLayout ly_roomTitle, ly_uppager, ly_nextpager;
    private RecyclerView room_recyclerView;
    private RelativeLayout ly_roomBoot, ly_pager, ly_data, ly_untouch_input;
    private ProgressBar progressbar;
    private StringBuilder builder = new StringBuilder();
    //    private TextInputView textInputView;
    private TextView room_et;
    private GridLayoutManager gridLayoutManager;
    private RoomPickerAdapter adapter;
    private RoomPickerCallback callback;//

    //==============参数==============
    private MyHandler mHandler;

    private boolean isUntouch = false;//默认非触屏设备(>>>固定，外部<<<)
    private boolean isPort = false;//默认横屏设备(>>>固定，外部<<<)
    private boolean isKeyEventListen = false;//是否允许输入监听,true-->则拦截CallFrag的输入事件，false-->无监听事件
    private boolean isInputRoom = false;//输入房间号界面判断(只用于非触屏)
    private boolean isSelectRoom = false;//选择item房间界面判断

    //触屏大九饼模式判断（6层18户）
    private boolean isMinLevel = false;//触屏：当楼层小于等于6层时->true/大于6层->false | 小于6层时，不显示楼层，直接显示房间数据

    private int currentNodeId;//当前设备节点id(>>>固定<<<)
    private int currentRoomLevel = 0;//当前设备节点level(>>>固定<<<)
    private int currentShowLevel;//UI显示的根节点level(>>>固定<<<)
    private JSONArray currentIdData;//当前设备节点数据（json数据）(>>>固定<<<)
    private List<RoomItemBean> baseRoomDatas;//当前设备节点总数据（封装数据）(>>>固定<<<)
    private List<RoomItemBean> itemPagerDatas;//判断页数的总数据/房间总数据

    private int pagerSize = 8;//不处理大于10的item事件，非触屏：横屏8个，竖屏9个 触屏：横屏8个，竖屏12个
    private int currentPager = 1;//当前页数
    private int sumPager = 1;//总页数
    private Map<Integer, List<RoomItemBean>> baseRoomMap;
    private static ExecutorService execute = Executors.newFixedThreadPool(5);//使用线程池

    //实现上页/下页的临时参数

    private enum UISTATE {
        DATA_EMPTY,// 空数据，显示错误
        DATA_ORG_LOADING,//原始数据，正在加载
        UI_SHOW_DATA,//初始化/重置显示UI
    }

    //==============初始化==============

    public RoomPicker(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public RoomPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RoomPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public RoomPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }
    //==============初始设置==============

    private void initView(Context context) {
        this.context = context;
        inflate(context, R.layout.view_roompicker, this);
        //初始化布局
        mHandler = new MyHandler(this);
        if (execute == null) {
            execute = Executors.newFixedThreadPool(5);
        }
        initWidget();

        createData();//TODO 测试数据
    }

    private void initWidget() {
        ly_roomBoot = (RelativeLayout) findViewById(R.id.ly_roomBoot);
        ly_data = (RelativeLayout) findViewById(R.id.ly_data);
        ly_untouch_input = (RelativeLayout) findViewById(R.id.ly_untouch_input);
        ly_roomTitle = (LinearLayout) findViewById(R.id.ly_roomTitle);
//        textInputView = (TextInputView) findViewById(R.id.textInputView);
        room_et = (TextView) findViewById(R.id.room_et);
        room_recyclerView = (RecyclerView) findViewById(R.id.room_recyclerView);
        tv_errorData = (TextView) findViewById(R.id.tv_errorData);

        ly_roomTitle_01 = (RelativeLayout) findViewById(R.id.ly_roomTitle_01);
        ly_roomTitle_02 = (RelativeLayout) findViewById(R.id.ly_roomTitle_02);
        ly_roomTitle_03 = (RelativeLayout) findViewById(R.id.ly_roomTitle_03);
        ly_roomTitle_04 = (RelativeLayout) findViewById(R.id.ly_roomTitle_04);
        ly_roomTitle_05 = (RelativeLayout) findViewById(R.id.ly_roomTitle_05);
        room_title_01 = (TextView) findViewById(R.id.room_title_01);
        room_title_02 = (TextView) findViewById(R.id.room_title_02);
        room_title_03 = (TextView) findViewById(R.id.room_title_03);
        room_title_04 = (TextView) findViewById(R.id.room_title_04);
        room_title_05 = (TextView) findViewById(R.id.room_title_05);

        //页数
        ly_pager = (RelativeLayout) findViewById(R.id.ly_pager);
        tv_pager = (TextView) findViewById(R.id.tv_pager);
        img_nextpager = (TextView) findViewById(R.id.img_nextpager);
        img_uppager = (TextView) findViewById(R.id.img_uppager);
        tv_uppager = (TextView) findViewById(R.id.tv_uppager);
        tv_nextpager = (TextView) findViewById(R.id.tv_nextpager);
        ly_uppager = (LinearLayout) findViewById(R.id.ly_uppager);
        ly_nextpager = (LinearLayout) findViewById(R.id.ly_nextpager);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        tv_errorData.setOnClickListener(this);
        tv_nextpager.setOnClickListener(this);
        tv_uppager.setOnClickListener(this);
    }

    /**
     * adapter
     */
    private void initAdapter(List<RoomItemBean> pagerDatas) {
        adapter = new RoomPickerAdapter(context);
        adapter.setUntouch(isUntouch);
        adapter.setDatas(pagerDatas);
        adapter.setPort(isPort);
        if (isPort) {
            gridLayoutManager = new GridLayoutManager(context, 3);
        } else {
            gridLayoutManager = new GridLayoutManager(context, 4);
        }
        adapter.setOnItemClickListener(recyclerListener);
        room_recyclerView.setLayoutManager(gridLayoutManager);
        room_recyclerView.setAdapter(adapter);
    }

    //==============参数设置==============
    /**
     * recyclerView适配监听
     */
    private RoomPickerItemListener recyclerListener = new RoomPickerItemListener() {
        @Override
        public void onItemClickListener(RoomItemBean data, int pos) {
            selectLevel(pos, data);
        }
    };

    public void setRoomPickerCallback(RoomPickerCallback callback) {
        this.callback = callback;
    }

    public RoomPickerCallback getCallback() {
        return callback;
    }

    public boolean isUntouch() {
        return isUntouch;
    }

    public boolean isKeyEventListen() {
        return isKeyEventListen;
    }

    public void setUntouch(boolean untouch) {
        isUntouch = untouch;
        try {
            if (adapter != null) {
                adapter.setUntouch(untouch);
            }
        } catch (Exception e) {
        }

    }

    public boolean isPort() {
        return isPort;
    }

    /**
     * 重新进入界面，是否需要重新设置高度
     *
     * @return
     */
    public boolean resetPickerView() {
        if (!isUntouch && currentShowLevel <= 3) {
            return true;
        } else {//大部分挂在单元门，currentShowLevel = 5
            return false;
        }
    }

    public void setPort(boolean port) {
        isPort = port;
    }

    /**
     * TODO 测试
     * 设置参数后，重新构建
     */
    public void buildView(boolean isInit) {
        try {
//            currentIdData = (JSONArray) CacheUtils.getInstance().getSerializable(TouchConstants.ROOM_DATAS);
            if (currentIdData == null) {
                setUIState(UISTATE.DATA_ORG_LOADING);
                //异步加载数据
                execute.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 测试
                        String str = obj1.toJSONString();
//                        String str = obj2.toJSONString();//切换测试
                        //

                        JSONObject orgObj = JSONObject.parseObject(str);
                        if (orgObj != null && orgObj.containsKey("tree")) {
                            currentIdData = RoomGsonUtils.getRoomJsonArray(orgObj, currentNodeId);
                            setInitData();
                            setUIData(true);
                            setUIState(UISTATE.UI_SHOW_DATA);
                        } else {
                            setUIState(UISTATE.DATA_EMPTY);
                        }
                    }
                });
            } else {//目前不用
                Log.e(TAG, "从缓存中加载房间数据");
                if (isInit || baseRoomDatas == null) {//初始化
                    setUIState(UISTATE.DATA_ORG_LOADING);
                    execute.execute(new Runnable() {
                        @Override
                        public void run() {
                            setInitData();
                            setUIData(true);
                            setUIState(UISTATE.UI_SHOW_DATA);
                        }
                    });
                } else {
                    reset();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "构建房间筛选器异常：" + e.toString());
        }
    }

    /**
     * 初始化/重置 筛选器参数数据
     */
    private void setInitData() {
//        String level = CacheUtils.getInstance().getString(TouchConstants.ROOM_LEVEL);
        String level = "2";// node_id=113687对应的level=2
        if (!TextUtils.isEmpty(level)) {
            currentRoomLevel = Integer.parseInt(level);
        } else {
            Log.e(TAG, "节点参数错误，无法使用");
            setUIState(UISTATE.DATA_EMPTY);
            return;
        }

        //
        //UI显示level判断:否有level=2(二道门) 否有level=4（单元)
        switch (currentRoomLevel) {
            case 1://小区节点
                boolean hasLevel2 = RoomGsonUtils.hasNodeLevel2(currentIdData);
                if (hasLevel2) {
                    currentShowLevel = currentRoomLevel + 1;//从二道门显示2
                } else {
                    currentShowLevel = currentRoomLevel + 2;//从楼栋显示3
                }
                Log.d(TAG, "当前使用节点" + currentShowLevel);
                baseRoomDatas = getRoomData(currentIdData);
                break;
            case 2://二道门节点
                currentShowLevel = 3;
                Log.d(TAG, "当前使用节点3");
                baseRoomDatas = getRoomData(currentIdData);
                break;
            case 3://楼
                boolean hasLevel4 = RoomGsonUtils.hasNodeLevel4(currentIdData);
                if (hasLevel4) {//有单元数据
                    currentShowLevel = 4;//从单元显示4
                    Log.d(TAG, "当前使用节点4");
                    baseRoomDatas = getRoomData(currentIdData);
                } else {//无单元数据
                    if (isUntouch) {//非触
                        currentShowLevel = 6;//非触屏 显示房间
                        Log.d(TAG, "当前使用节点6");
                        baseRoomDatas = getItemRoomData_4(currentIdData);
                    } else {
                        currentShowLevel = 5;//触屏要从层显示
                        Log.d(TAG, "当前使用节点5");
                        baseRoomDatas = getRoomData(currentIdData);
                    }
                }
                break;
            case 4://单元
                if (isUntouch) {//非触
                    currentShowLevel = 6;//直接显示房间
                    Log.d(TAG, "当前使用节点6");
                    baseRoomDatas = getItemRoomData_4(currentIdData);
                } else {
                    currentShowLevel = 5;//触屏要从层显示
                    Log.d(TAG, "当前使用节点5");
                    baseRoomDatas = getRoomData(currentIdData);
                }
                break;
            default://层节点
                currentShowLevel = 6;
                Log.d(TAG, "当前使用节点6");
                baseRoomDatas = getRoomData(currentIdData);
                break;
        }
        if (isUntouch) {//非触屏
            if (isPort) {
                pagerSize = 9;
            } else {
                pagerSize = 8;
            }
        } else {//触屏
            if (isPort) {
                pagerSize = 12;
            } else {
                pagerSize = 8;
            }
        }
        //特殊楼层判断：6层18户模式判断
        if (!isUntouch && currentShowLevel == 5) {
            List<RoomItemBean> data = getRoomData(currentIdData);
            if (data.size() <= USER_LEVEL) {//重置数据
                currentShowLevel = 6;
                baseRoomDatas = getItemRoomData_4(currentIdData);
                if (isPort) {
                    pagerSize = 18;//需要回归
                } else {
                    pagerSize = 16;//需要回归
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            Log.d(TAG, "初始化：设置筛选器高度");
                            callback.changeToMaxView(true);
                            //修改UI高度
                            if (isPort) {
                                ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                                ly.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_02);
                                ly_roomBoot.setLayoutParams(ly);
                            } else {
                                ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                                ly.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_02);
                                ly_roomBoot.setLayoutParams(ly);
                            }
                        }
                    }
                });
            }
        }
    }

    //UI数据更新
    private void setUIData(final boolean isInit) {
        post(new Runnable() {
            @Override
            public void run() {
                if (isUntouch) {//非触屏处理
                    if (currentShowLevel <= 4) {//小区2/楼3/单元4
                        if (isInit) {
                            List<RoomItemBean> pagerDatas = setPagerData(baseRoomDatas);
                            initAdapter(pagerDatas);
                        } else {
                            //显示更level页数
                            List<RoomItemBean> pagerDatas = setPagerData(baseRoomDatas);
                            if (adapter != null) {
                                adapter.updateDatas(pagerDatas);
                            }
                        }
                    } else {
                        itemPagerDatas = baseRoomDatas;//用于输入框输入比对判断;
                        //排除页数扰乱数据
                        currentPager = 1;
                        sumPager = 1;
                        //输入数据清除
                        builder = new StringBuilder();
                        room_et.setText("");
                    }
                } else {//触屏处理
                    if (isInit) {
                        List<RoomItemBean> pagerDatas = setPagerData(baseRoomDatas);
                        initAdapter(pagerDatas);
                    } else {
                        //显示更level页数
                        List<RoomItemBean> pagerDatas = setPagerData(baseRoomDatas);
                        if (adapter != null) {
                            adapter.updateDatas(pagerDatas);
                        }
                    }
                }
            }
        });
    }

    /**
     * 重置
     */
    private void reset() {
        Log.d(TAG, "重置筛选器");
        //回归高度
        if (resetPickerView()) {
            Log.d(TAG, "回归筛选器高度");
            //回归UI高度
            if (isPort) {
                ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_01);
                ly_roomBoot.setLayoutParams(ly);
            } else {
                ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_01);
                ly_roomBoot.setLayoutParams(ly);
            }
            //回归参数
            if (isUntouch) {//非触屏
                if (isPort) {
                    pagerSize = 9;
                } else {
                    pagerSize = 8;
                }
            } else {//触屏
                if (isPort) {
                    pagerSize = 12;
                } else {
                    pagerSize = 8;
                }
            }
        }
        setUIData(false);
        setUIState(UISTATE.UI_SHOW_DATA);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_uppager) {
            updatePager(currentPager - 1, false);
        } else if (id == R.id.tv_nextpager) {
            updatePager(currentPager + 1, true);
        } else if (id == R.id.tv_errorData) {//点击重新加载数据
            reset();
        }
    }

    /**
     * =======================================输入监听事件=========================================
     */
    public void onKeyEvent(String keyText) {
        if (keyText.length() == 1 && keyText.charAt(0) >= '0' && keyText.charAt(0) <= '9') {
            if (isInputRoom && isUntouch) {
                //--------输入框分配的事件--------

                builder.append(keyText);
                room_et.setText(builder.toString());
            } else if (isSelectRoom && isUntouch) {
                if (keyText.equals("0")) {
                    //需要CallFrag高度页修改
                    if (callback != null) {
                        callback.changeToMaxView(false);//回归高度
                    }
                    //
                    reset();
                } else {
                    //--------recyclerView分配的事件--------
                    int realPos = Integer.parseInt(keyText) - 1;//recyclerView的item计数都从1开始(UI强制要求)
                    RoomItemBean bean = adapter.getItem(realPos);
                    if (bean != null) {
                        selectLevel(realPos, bean);
                    }

                }
            } else {
                Log.d(TAG, "触屏输入事件不理会" + keyText);
            }
        } else if ("#".equals(keyText)) {//下一页
            if (isSelectRoom) {
                //--------recyclerView分配的事件--------
                updatePager(currentPager + 1, true);
            }
        } else if ("*".equals(keyText)) {//上一页
            if (isSelectRoom) {
                //--------recyclerView分配的事件--------

                //无法上翻页数，则认为*为关闭功能
                if (!updatePager(currentPager - 1, false)) {
                    if (callback != null) {
                        callback.hidePickerView();
                    }
                }
            } else if (isInputRoom) {

                //--------输入框分配的事件--------

                //输入框时，*为关闭功能
                if (callback != null) {
                    callback.hidePickerView();
                }
            }
        } else if ("BACK".equals(keyText)) {
            if (isInputRoom) {
                //--------输入框分配的事件--------
                if (builder.toString().length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                    room_et.setText(builder.toString());
                }
            }
        } else if ("CALL".equals(keyText)) {
            if (isUntouch) {
                checkResult();
            }
        }
    }

    /**
     *
     */
    private void checkResult() {
        isInputRoom = false;
        isKeyEventListen = false;
        Log.d(TAG, "匹配数据=" + builder.toString() + "--" + itemPagerDatas.size() + "--" + baseRoomDatas.size());
        //线程处理耗时
        if (execute == null) {
            execute = Executors.newFixedThreadPool(5);
        }
        execute.execute(new Runnable() {
            @Override
            public void run() {
                boolean hasRoom = false;
                for (int i = 0; i < itemPagerDatas.size(); i++) {
                    final RoomItemBean bean = itemPagerDatas.get(i);
                    if (bean.getNode_name().equals(builder.toString())) {
                        hasRoom = true;
                        post(new Runnable() {
                            @Override
                            public void run() {
                                //拿到结果
                                if (callback != null) {
                                    callback.onRoomSuccess(bean);
                                }
                            }
                        });
                    }
                }
                if (!hasRoom) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onRoomFailed("没有匹配房间");
                            }
                        }
                    });
                }
            }
        });
    }


    //==============状态设置==============

    /**
     * 触屏/非触屏共同处理 adapter的item事件
     *
     * @param pos
     * @param bean
     */
    private void selectLevel(int pos, final RoomItemBean bean) {
        switch (bean.level) {
            case "2"://二道门
                isInputRoom = false;
                isSelectRoom = true;
                List<RoomItemBean> datas2 = getRoomData(bean.getChild());
                //显示下一级数据
                room_title_01.setText(bean.getNode_name());
                room_title_01.setSelected(false);

                room_title_02.setText("楼栋");
                ly_roomTitle_02.setVisibility(View.VISIBLE);
                room_title_02.setSelected(true);
                //更新adapter
                if (adapter != null) {
                    List<RoomItemBean> pagerDatas = setPagerData(datas2);
                    adapter.updateDatas(pagerDatas);
                }
                break;
            case "3"://楼
                List<RoomItemBean> datas3 = getRoomData(bean.getChild());
                boolean hasLevel4 = RoomGsonUtils.hasNodeLevel4(bean.getChild());
                room_title_02.setText(bean.getNode_name());
                room_title_02.setSelected(false);
                //显示下一级数据
                if (hasLevel4) {//显示单元
                    isInputRoom = false;
                    isSelectRoom = true;

                    room_title_03.setText("单元");
                    ly_roomTitle_03.setVisibility(View.VISIBLE);
                    room_title_03.setSelected(true);

                    //更新adapter
                    if (adapter != null) {
                        List<RoomItemBean> pagerDatas = setPagerData(datas3);
                        adapter.updateDatas(pagerDatas);
                    }
                } else if (isUntouch) {//无单元，楼层判断-->非触屏,显示房间
                    isSelectRoom = false;
                    isInputRoom = true;
                    builder = new StringBuilder();
                    room_et.setText("");
                    itemPagerDatas = getItemRoomData_4(bean.getChild());//用于输入框输入比对判断;

                    room_title_05.setText("房间");
                    ly_roomTitle_05.setVisibility(View.VISIBLE);
                    room_title_05.setSelected(true);
                    //显示输入框
                    ly_data.setVisibility(VISIBLE);
                    room_recyclerView.setVisibility(View.GONE);
                    ly_untouch_input.setVisibility(VISIBLE);
                    //隐藏
                    tv_errorData.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                    ly_pager.setVisibility(View.GONE);
                    if (callback != null) {
                        callback.hideReset();
                    }
                } else if (datas3.size() <= USER_LEVEL) {//无单元，楼层判断-->触屏,楼层少直接显示显示房间
                    //
                    isInputRoom = false;
                    isSelectRoom = true;
                    room_title_05.setText("房间");
                    ly_roomTitle_05.setVisibility(View.VISIBLE);
                    room_title_05.setSelected(true);

                    //设置房间数据，修改相应参数
                    itemPagerDatas = getItemRoomData_4(bean.getChild());
                    if (isPort) {
                        pagerSize = 18;//需要回归
                    } else {
                        pagerSize = 16;//需要回归
                    }

                    if (callback != null) {
                        callback.changeToMaxView(true);
                        //修改UI高度
                        if (isPort) {
                            ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                            ly.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_02);
                            ly_roomBoot.setLayoutParams(ly);
                        } else {
                            ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                            ly.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_02);
                            ly_roomBoot.setLayoutParams(ly);
                        }
                    }
                    //更新adapter
                    if (adapter != null) {
                        List<RoomItemBean> pagerDatas = setPagerData(itemPagerDatas);
                        adapter.updateDatas(pagerDatas);
                    }

                } else {////无单元，楼层判断-->触屏，显示楼层
                    isInputRoom = false;
                    isSelectRoom = true;

                    room_title_04.setText("楼层");
                    ly_roomTitle_04.setVisibility(View.VISIBLE);
                    room_title_04.setSelected(true);

                    //更新adapter
                    if (adapter != null) {
                        List<RoomItemBean> pagerDatas = setPagerData(datas3);
                        adapter.updateDatas(pagerDatas);
                    }
                }


                break;
            case "4"://单元
                List<RoomItemBean> datas4 = getRoomData(bean.getChild());
                room_title_03.setText(bean.getNode_name());
                room_title_03.setSelected(false);

                //非触屏,显示房间（代码同上）
                if (isUntouch) {
                    isSelectRoom = false;
                    isInputRoom = true;
                    builder = new StringBuilder();
                    room_et.setText("");
                    itemPagerDatas = getItemRoomData_4(bean.getChild());//用于输入框输入比对判断;

                    room_title_05.setText("房间");
                    ly_roomTitle_05.setVisibility(View.VISIBLE);
                    room_title_05.setSelected(true);
                    //显示输入框
                    ly_data.setVisibility(VISIBLE);
                    room_recyclerView.setVisibility(View.GONE);
                    ly_untouch_input.setVisibility(VISIBLE);
                    //隐藏
                    tv_errorData.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                    ly_pager.setVisibility(View.GONE);
                    if (callback != null) {
                        callback.hideReset();
                    }
                } else if (datas4.size() <= USER_LEVEL) {//楼层判断-->触屏,楼层少直接显示显示房间
                    //
                    isInputRoom = false;
                    isSelectRoom = true;
                    room_title_05.setText("房间");
                    ly_roomTitle_05.setVisibility(View.VISIBLE);
                    room_title_05.setSelected(true);

                    //设置房间数据，修改相应参数
                    itemPagerDatas = getItemRoomData_4(bean.getChild());
                    if (isPort) {
                        pagerSize = 18;//需要回归
                    } else {
                        pagerSize = 16;//需要回归
                    }
                    if (callback != null) {
                        callback.changeToMaxView(true);
                        //修改UI高度
                        if (isPort) {
                            ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                            ly.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_02);
                            ly_roomBoot.setLayoutParams(ly);
                        } else {
                            ViewGroup.LayoutParams ly = ly_roomBoot.getLayoutParams();
                            ly.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_02);
                            ly_roomBoot.setLayoutParams(ly);
                        }
                    }
                    //更新adapter
                    if (adapter != null) {
                        List<RoomItemBean> pagerDatas = setPagerData(itemPagerDatas);
                        adapter.updateDatas(pagerDatas);
                    }

                } else {//楼层判断-->触屏，显示楼层
                    isInputRoom = false;
                    isSelectRoom = true;
                    room_title_04.setText("楼层");
                    ly_roomTitle_04.setVisibility(View.VISIBLE);
                    room_title_04.setSelected(true);

                    //更新adapter
                    if (adapter != null) {
                        List<RoomItemBean> pagerDatas = setPagerData(datas4);
                        adapter.updateDatas(pagerDatas);
                    }
                }
                break;
            case "5"://层（只有触屏走这一步）
                if (isUntouch) {
                    Log.d(TAG, "非触屏数据处理错误" + bean.getNode_name());
                    return;
                }
                List<RoomItemBean> datas5 = getRoomData(bean.getChild());
                isInputRoom = false;
                isSelectRoom = true;
                room_title_04.setText(bean.getNode_name());
                room_title_04.setSelected(false);

                room_title_05.setText("房间");
                ly_roomTitle_05.setVisibility(View.VISIBLE);
                room_title_05.setSelected(true);

                //更新adapter
                if (adapter != null) {
                    List<RoomItemBean> pagerDatas = setPagerData(datas5);
                    adapter.updateDatas(pagerDatas);
                }
                break;
            case "6"://房间数据（只有触屏走这一步）
                room_title_05.setText(bean.getNode_name());
                room_title_05.setSelected(false);
                Log.d(TAG, "选中呼叫房间：" + bean.toString());
                post(new Runnable() {
                    @Override
                    public void run() {
                        //拿到结果
                        if (callback != null) {
                            isInputRoom = false;
                            isKeyEventListen = false;
                            callback.onRoomSuccess(bean);
                        }
                    }
                });
                break;
            default:
                Log.e(TAG, "节点错误");
                break;

        }
    }

    /**
     * 修改UI状态
     *
     * @param state
     */
    private void setUIState(final UISTATE state) {
        post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case DATA_EMPTY://
                        isInputRoom = false;
                        isSelectRoom = false;
                        //显示
                        ly_data.setVisibility(View.VISIBLE);
                        ly_roomTitle_01.setVisibility(View.VISIBLE);
                        room_title_01.setText("区域");
                        room_title_01.setSelected(true);
                        if (isUntouch) {
                            tv_errorData.setVisibility(View.VISIBLE);
                            tv_errorData.setText("获取数据失败，请重新进入界面");
                        } else {
                            tv_errorData.setVisibility(View.VISIBLE);
                        }
                        //隐藏
                        progressbar.setVisibility(View.GONE);
                        ly_untouch_input.setVisibility(View.GONE);
                        room_recyclerView.setVisibility(View.GONE);
                        ly_roomTitle_02.setVisibility(View.GONE);
                        ly_roomTitle_03.setVisibility(View.GONE);
                        ly_roomTitle_04.setVisibility(View.GONE);
                        ly_roomTitle_05.setVisibility(View.GONE);
                        ly_pager.setVisibility(View.GONE);
                        break;
                    case DATA_ORG_LOADING:
                        isInputRoom = false;
                        isSelectRoom = false;
                        //显示
                        ly_data.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.VISIBLE);
                        ly_roomTitle_01.setVisibility(View.VISIBLE);
                        room_title_01.setText("区域");
                        room_title_01.setSelected(true);
                        //隐藏
                        tv_errorData.setVisibility(View.GONE);
                        ly_untouch_input.setVisibility(View.GONE);
                        room_recyclerView.setVisibility(View.GONE);
                        ly_roomTitle_02.setVisibility(View.GONE);
                        ly_roomTitle_03.setVisibility(View.GONE);
                        ly_roomTitle_04.setVisibility(View.GONE);
                        ly_roomTitle_05.setVisibility(View.GONE);
                        ly_pager.setVisibility(View.GONE);
                        break;
                    case UI_SHOW_DATA:
                        //初始化参数
                        isKeyEventListen = true;//监听按键
                        //显示
                        ly_data.setVisibility(View.VISIBLE);
                        //隐藏
                        tv_errorData.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);

                        if (currentShowLevel == 2) {//显示 小区 数据，其他隐藏
                            isInputRoom = false;
                            isSelectRoom = true;
                            //显示room title
                            ly_roomTitle_01.setVisibility(View.VISIBLE);
                            room_title_01.setText("区域");
                            room_recyclerView.setVisibility(View.VISIBLE);
                            //其他room title
                            ly_roomTitle_02.setVisibility(View.GONE);
                            ly_roomTitle_03.setVisibility(View.GONE);
                            ly_roomTitle_04.setVisibility(View.GONE);
                            ly_roomTitle_05.setVisibility(View.GONE);
                            ly_untouch_input.setVisibility(View.GONE);

                            //设置颜色
                            room_title_01.setSelected(true);
                            room_title_02.setSelected(false);
                            room_title_03.setSelected(false);
                            room_title_04.setSelected(false);
                            room_title_05.setSelected(false);
                            if (callback != null) {
                                callback.showReset();
                            }

                        } else if (currentShowLevel == 3) {//显示 楼 数据，其他隐藏
                            isInputRoom = false;
                            isSelectRoom = true;
                            //显示room title
                            ly_roomTitle_02.setVisibility(View.VISIBLE);
                            room_title_02.setText("楼栋");
                            room_recyclerView.setVisibility(View.VISIBLE);
                            //其他room title
                            ly_roomTitle_01.setVisibility(View.GONE);
                            ly_roomTitle_03.setVisibility(View.GONE);
                            ly_roomTitle_04.setVisibility(View.GONE);
                            ly_roomTitle_05.setVisibility(View.GONE);
                            ly_untouch_input.setVisibility(View.GONE);

                            //设置颜色
                            room_title_02.setSelected(true);
                            room_title_01.setSelected(false);
                            room_title_03.setSelected(false);
                            room_title_04.setSelected(false);
                            room_title_05.setSelected(false);
                            if (callback != null) {
                                callback.showReset();
                            }
                        } else if (currentShowLevel == 4) {//显示 单元 数据，其他隐藏
                            isInputRoom = false;
                            isSelectRoom = true;
                            //显示room title
                            ly_roomTitle_03.setVisibility(View.VISIBLE);
                            room_title_03.setText("单元");
                            room_recyclerView.setVisibility(View.VISIBLE);
                            //其他room title
                            ly_roomTitle_01.setVisibility(View.GONE);
                            ly_roomTitle_02.setVisibility(View.GONE);
                            ly_roomTitle_04.setVisibility(View.GONE);
                            ly_roomTitle_05.setVisibility(View.GONE);
                            ly_untouch_input.setVisibility(View.GONE);

                            //设置颜色
                            room_title_03.setSelected(true);
                            room_title_01.setSelected(false);
                            room_title_02.setSelected(false);
                            room_title_04.setSelected(false);
                            room_title_05.setSelected(false);
                            if (callback != null) {
                                callback.showReset();
                            }

                        } else if (currentShowLevel == 5 && !isUntouch) {//触屏时，显示 楼层 数据，其他隐藏
                            isInputRoom = false;
                            isSelectRoom = true;
                            //显示room title
                            ly_roomTitle_04.setVisibility(View.VISIBLE);
                            room_title_04.setText("楼层");
                            room_recyclerView.setVisibility(View.VISIBLE);
                            //其他room title
                            ly_roomTitle_01.setVisibility(View.GONE);
                            ly_roomTitle_02.setVisibility(View.GONE);
                            ly_roomTitle_03.setVisibility(View.GONE);
                            ly_roomTitle_05.setVisibility(View.GONE);
                            ly_untouch_input.setVisibility(View.GONE);

                            //设置颜色
                            room_title_04.setSelected(true);
                            room_title_01.setSelected(false);
                            room_title_02.setSelected(false);
                            room_title_03.setSelected(false);
                            room_title_05.setSelected(false);
                            if (callback != null) {
                                callback.showReset();
                            }

                        } else if (currentShowLevel == 6 && !isUntouch) {//触屏时，显示 房间 数据，其他隐藏
                            isInputRoom = false;
                            isSelectRoom = true;
                            //显示room title
                            ly_roomTitle_05.setVisibility(View.VISIBLE);
                            room_title_05.setText("房间");
                            room_recyclerView.setVisibility(View.VISIBLE);
                            //其他room title
                            ly_roomTitle_01.setVisibility(View.GONE);
                            ly_roomTitle_02.setVisibility(View.GONE);
                            ly_roomTitle_03.setVisibility(View.GONE);
                            ly_roomTitle_04.setVisibility(View.GONE);
                            ly_untouch_input.setVisibility(View.GONE);

                            //设置颜色
                            room_title_05.setSelected(true);
                            room_title_01.setSelected(false);
                            room_title_02.setSelected(false);
                            room_title_03.setSelected(false);
                            room_title_04.setSelected(false);
                            if (callback != null) {
                                callback.hideReset();
                            }

                        } else {// 非触屏，显示房间输入框
                            isSelectRoom = false;
                            isInputRoom = true;
                            builder = new StringBuilder();
                            room_et.setText("");
                            ly_untouch_input.setVisibility(View.VISIBLE);

                            room_title_04.setText("房间");

                            room_title_01.setVisibility(View.GONE);
                            room_title_02.setVisibility(View.GONE);
                            room_title_03.setVisibility(View.GONE);
                            room_title_04.setVisibility(View.VISIBLE);
                            //设置颜色
                            room_title_04.setSelected(true);
                            room_title_01.setSelected(false);
                            room_title_02.setSelected(false);
                            room_title_03.setSelected(false);

                            ly_data.setVisibility(VISIBLE);
                            room_recyclerView.setVisibility(View.GONE);
                            //隐藏
                            tv_errorData.setVisibility(View.GONE);
                            progressbar.setVisibility(View.GONE);
                            if (callback != null) {
                                callback.hideReset();
                            }

                        }
                        break;
                    default:
                        break;
                }


            }
        });

    }

    //==============私有==============


    private List<RoomItemBean> getRoomData(JSONArray orgArry) {
        List<RoomItemBean> datas = new ArrayList<RoomItemBean>();
        for (int i = 0; i < orgArry.size(); i++) {
            JSONObject obj = orgArry.getJSONObject(i);
            //过滤 999的节点
            if (obj.getString("level").equals("999")) {
                continue;
            }
            datas.add(new RoomItemBean
                    (obj.getString("node_name"),
                            obj.getString("level"),
                            obj.getIntValue("parent_id"),
                            obj.getIntValue("node_id"),
                            obj.getJSONArray("child")));
        }
        return datas;
    }

    /**
     * 单元节点使用的方法,用于获取所有的房间号(层+房间的总房间数据)
     */
    private List<RoomItemBean> getItemRoomData_4(JSONArray baseArray) {
        List<RoomItemBean> datas = new ArrayList<RoomItemBean>();
        for (int i = 0; i < baseArray.size(); i++) {
            JSONObject obj = baseArray.getJSONObject(i);
            JSONArray obj_array = obj.getJSONArray("child");
            for (int j = 0; j < obj_array.size(); j++) {
                JSONObject obj2 = obj_array.getJSONObject(j);
                datas.add(new RoomItemBean(
                        obj2.getString("node_name"),
                        obj2.getString("level"),
                        obj2.getIntValue("parent_id"),
                        obj2.getIntValue("node_id"),
                        obj2.getJSONArray("child")));
            }
        }
        return datas;
    }

    /**
     * 页数处理
     */
    private List<RoomItemBean> setPagerData(List<RoomItemBean> data) {
        //初始化页数
        currentPager = 1;
        itemPagerDatas = data;
        int size = itemPagerDatas.size();
        if (size % pagerSize != 0) {
            sumPager = size / pagerSize + 1;
        } else {
            sumPager = size / pagerSize;
        }

        //显示第一页的数据
        if (sumPager > 1) {
            //UI显示
            tv_pager.setText("" + currentPager + "/" + sumPager);
            if (isUntouch) {//显示图标
                img_uppager.setVisibility(VISIBLE);
                img_nextpager.setVisibility(VISIBLE);
            } else {//隐藏图标
                img_uppager.setVisibility(GONE);
                img_nextpager.setVisibility(GONE);
            }
            ly_pager.setVisibility(VISIBLE);
            return itemPagerDatas.subList(0, pagerSize);

        } else {//只有一页数据,不显示页数
            ly_pager.setVisibility(GONE);
            return itemPagerDatas;
        }
    }

    /**
     * 上一页，下一页判断
     *
     * @param currPager
     * @param isNext
     */
    private boolean updatePager(int currPager, boolean isNext) {
        if (sumPager == 1 || currPager == 0) {
            return false;
        }
        if (isNext) {//下一页处理
            if (currPager > sumPager || currPager == 1) {
                return false;
            }
            //计算list的下标，截取数据
            int start = 0, end = 0;
            if (currPager < sumPager) {
                start = pagerSize * (currPager - 1);
                end = pagerSize * currPager;
            } else {//最后一页
                start = pagerSize * (currPager - 1);
                end = itemPagerDatas.size();
            }
            if (start > end) {
                return false;//按键过快导致
            }
            List<RoomItemBean> subDatas = itemPagerDatas.subList(start, end);
            //更新数据
            if (adapter != null) {
                adapter.updateDatas(subDatas);
            }
            tv_pager.setText(currPager + "/" + sumPager);
            this.currentPager = currPager;
            return true;
        } else {//上一页处理
            if (currPager <= 0 || currPager == sumPager) {
                return false;
            }

            //计算list的下标，截取数据
            int start = 0, end = 0;
            if (currPager > 1) {
                start = pagerSize * (currPager - 1);
                end = pagerSize * currPager;
            } else {//第一页
                start = 0;
                end = pagerSize;
            }
            if (start > end) {
                return false;//按键过快导致
            }
            List<RoomItemBean> subDatas = itemPagerDatas.subList(start, end);
            //更新数据
            if (adapter != null) {
                adapter.updateDatas(subDatas);
            }
            tv_pager.setText(currPager + "/" + sumPager);
            this.currentPager = currPager;
            return true;
        }
    }

//==============参数设置==============

    /**
     * 设置静态hanlder，避免内存泄漏
     */
    private static class MyHandler extends Handler {
        private WeakReference<RoomPicker> myApplicationWeakReference;

        public MyHandler(RoomPicker application) {
            this.myApplicationWeakReference = new WeakReference<RoomPicker>(application);
        }

        @Override
        public void handleMessage(Message msg) {
            if (myApplicationWeakReference.get() == null) {
                return;
            }
        }
    }

    /**
     * 自定义回调事件
     */
    public interface RoomPickerCallback {

        void onRoomSuccess(RoomItemBean bean);

        void onRoomFailed(String e);

        void hideReset();//隐藏上一步按钮：进入输入框时，隐藏

        void showReset();//显示 上一步按钮：有选择布局时，显示

        void hidePickerView();//*按键监听

        void changeToMaxView(boolean toMax);//触屏+竖屏+isMinLevel：不显示层，直接显示所有房间数据，展示布局边最大（6层18户）
    }

    //TODO 测试数据

    //测试数据1
    private JSONObject obj1;
    //该值来自obj1
    private int curInt1 = 113688;//切换值：111056 （挂在level=1小区上）/113687 (挂在level=2二道门小区上) / 113688 (挂在level=3栋上) / 113694 (挂在level=4单元上) / 113736 (挂在level=4层上)

    //测试数据2
    private JSONObject obj2;
    //该值来自obj2
    private int curInt2 = 136786;//切换值：136779（挂在level=1小区上）/ level=2无/  136780 (挂在level=3栋上)/136782 (挂在level=4单元上) /136786 (挂在level=4层上)

    private void createData() {
        currentNodeId = 113687;//获取当前节点，请从如下假数据中选择一个node_id即可//TODO 测试
        obj1 = JSONObject.parseObject("{\"tree\":{\"node_name\":\"测试\",\"parent_id\":-1,\"node_id\":111056,\"child\":[{\"node_name\":\"A区\",\"parent_id\":111056,\"node_id\":113687,\"child\":[{\"node_name\":\"1栋\",\"parent_id\":113687,\"node_id\":113688,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":113688,\"node_id\":113694,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113694,\"node_id\":113736,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113736,\"node_id\":113904,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113736,\"node_id\":113905,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113736,\"node_id\":113906,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113736,\"node_id\":113907,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113736,\"node_id\":113908,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113736,\"node_id\":113909,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113694,\"node_id\":113737,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113737,\"node_id\":113910,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113737,\"node_id\":113911,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113737,\"node_id\":113912,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113737,\"node_id\":113913,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113737,\"node_id\":113914,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113737,\"node_id\":113915,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113694,\"node_id\":113738,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113738,\"node_id\":113916,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113738,\"node_id\":113917,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113738,\"node_id\":113918,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113738,\"node_id\":113919,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113738,\"node_id\":113920,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113738,\"node_id\":113921,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113694,\"node_id\":113739,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113739,\"node_id\":113922,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113739,\"node_id\":113923,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113739,\"node_id\":113924,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113739,\"node_id\":113925,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113739,\"node_id\":113926,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113739,\"node_id\":113927,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"2单元\",\"parent_id\":113688,\"node_id\":113695,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113695,\"node_id\":113740,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113740,\"node_id\":113928,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113740,\"node_id\":113929,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113740,\"node_id\":113930,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113740,\"node_id\":113931,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113740,\"node_id\":113932,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113740,\"node_id\":113933,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113695,\"node_id\":113741,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113741,\"node_id\":113934,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113741,\"node_id\":113935,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113741,\"node_id\":113936,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113741,\"node_id\":113937,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113741,\"node_id\":113938,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113741,\"node_id\":113939,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113695,\"node_id\":113742,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113742,\"node_id\":113940,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113742,\"node_id\":113941,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113742,\"node_id\":113942,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113742,\"node_id\":113943,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113742,\"node_id\":113944,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113742,\"node_id\":113945,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113695,\"node_id\":113743,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113743,\"node_id\":113946,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113743,\"node_id\":113947,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113743,\"node_id\":113948,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113743,\"node_id\":113949,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113743,\"node_id\":113950,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113743,\"node_id\":113951,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"3单元\",\"parent_id\":113688,\"node_id\":113696,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113696,\"node_id\":113744,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113744,\"node_id\":113952,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113744,\"node_id\":113953,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113744,\"node_id\":113954,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113744,\"node_id\":113955,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113744,\"node_id\":113956,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113744,\"node_id\":113957,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113696,\"node_id\":113745,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113745,\"node_id\":113958,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113745,\"node_id\":113959,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113745,\"node_id\":113960,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113745,\"node_id\":113961,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113745,\"node_id\":113962,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113745,\"node_id\":113963,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113696,\"node_id\":113746,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113746,\"node_id\":113964,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113746,\"node_id\":113965,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113746,\"node_id\":113966,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113746,\"node_id\":113967,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113746,\"node_id\":113968,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113746,\"node_id\":113969,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113696,\"node_id\":113747,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113747,\"node_id\":113970,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113747,\"node_id\":113971,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113747,\"node_id\":113972,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113747,\"node_id\":113973,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113747,\"node_id\":113974,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113747,\"node_id\":113975,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"},{\"node_name\":\"2栋\",\"parent_id\":113687,\"node_id\":113689,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":113689,\"node_id\":113701,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113701,\"node_id\":113764,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113764,\"node_id\":114072,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113764,\"node_id\":114073,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113764,\"node_id\":114074,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113764,\"node_id\":114075,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113764,\"node_id\":114076,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113764,\"node_id\":114077,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113701,\"node_id\":113765,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113765,\"node_id\":114078,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113765,\"node_id\":114079,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113765,\"node_id\":114080,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113765,\"node_id\":114081,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113765,\"node_id\":114082,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113765,\"node_id\":114083,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113701,\"node_id\":113766,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113766,\"node_id\":114084,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113766,\"node_id\":114085,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113766,\"node_id\":114086,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113766,\"node_id\":114087,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113766,\"node_id\":114088,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113766,\"node_id\":114089,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113701,\"node_id\":113767,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113767,\"node_id\":114090,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113767,\"node_id\":114091,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113767,\"node_id\":114092,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113767,\"node_id\":114093,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113767,\"node_id\":114094,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113767,\"node_id\":114095,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"2单元\",\"parent_id\":113689,\"node_id\":113702,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113702,\"node_id\":113768,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113768,\"node_id\":114096,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113768,\"node_id\":114097,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113768,\"node_id\":114098,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113768,\"node_id\":114099,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113768,\"node_id\":114100,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113768,\"node_id\":114101,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113702,\"node_id\":113769,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113769,\"node_id\":114102,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113769,\"node_id\":114103,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113769,\"node_id\":114104,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113769,\"node_id\":114105,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113769,\"node_id\":114106,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113769,\"node_id\":114107,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113702,\"node_id\":113770,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113770,\"node_id\":114108,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113770,\"node_id\":114109,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113770,\"node_id\":114110,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113770,\"node_id\":114111,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113770,\"node_id\":114112,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113770,\"node_id\":114113,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113702,\"node_id\":113771,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113771,\"node_id\":114114,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113771,\"node_id\":114115,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113771,\"node_id\":114116,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113771,\"node_id\":114117,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113771,\"node_id\":114118,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113771,\"node_id\":114119,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"3单元\",\"parent_id\":113689,\"node_id\":113703,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113703,\"node_id\":113772,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113772,\"node_id\":114120,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113772,\"node_id\":114121,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113772,\"node_id\":114122,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113772,\"node_id\":114123,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113772,\"node_id\":114124,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113772,\"node_id\":114125,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113703,\"node_id\":113773,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113773,\"node_id\":114126,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113773,\"node_id\":114127,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113773,\"node_id\":114128,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113773,\"node_id\":114129,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113773,\"node_id\":114130,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113773,\"node_id\":114131,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113703,\"node_id\":113774,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113774,\"node_id\":114132,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113774,\"node_id\":114133,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113774,\"node_id\":114134,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113774,\"node_id\":114135,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113774,\"node_id\":114136,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113774,\"node_id\":114137,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113703,\"node_id\":113775,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113775,\"node_id\":114138,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113775,\"node_id\":114139,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113775,\"node_id\":114140,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113775,\"node_id\":114141,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113775,\"node_id\":114142,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113775,\"node_id\":114143,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"4单元\",\"parent_id\":113689,\"node_id\":113704,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113704,\"node_id\":113776,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113776,\"node_id\":114144,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113776,\"node_id\":114145,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113776,\"node_id\":114146,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113776,\"node_id\":114147,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113776,\"node_id\":114148,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113776,\"node_id\":114149,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113704,\"node_id\":113777,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113777,\"node_id\":114150,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113777,\"node_id\":114151,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113777,\"node_id\":114152,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113777,\"node_id\":114153,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113777,\"node_id\":114154,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113777,\"node_id\":114155,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113704,\"node_id\":113778,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113778,\"node_id\":114156,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113778,\"node_id\":114157,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113778,\"node_id\":114158,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113778,\"node_id\":114159,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113778,\"node_id\":114160,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113778,\"node_id\":114161,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113704,\"node_id\":113779,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113779,\"node_id\":114162,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113779,\"node_id\":114163,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113779,\"node_id\":114164,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113779,\"node_id\":114165,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113779,\"node_id\":114166,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113779,\"node_id\":114167,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"5单元\",\"parent_id\":113689,\"node_id\":113705,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113705,\"node_id\":113780,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113780,\"node_id\":114168,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113780,\"node_id\":114169,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113780,\"node_id\":114170,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113780,\"node_id\":114171,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113780,\"node_id\":114172,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113780,\"node_id\":114173,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113705,\"node_id\":113781,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113781,\"node_id\":114174,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113781,\"node_id\":114175,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113781,\"node_id\":114176,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113781,\"node_id\":114177,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113781,\"node_id\":114178,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113781,\"node_id\":114179,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113705,\"node_id\":113782,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113782,\"node_id\":114180,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113782,\"node_id\":114181,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113782,\"node_id\":114182,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113782,\"node_id\":114183,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113782,\"node_id\":114184,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113782,\"node_id\":114185,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113705,\"node_id\":113783,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113783,\"node_id\":114186,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113783,\"node_id\":114187,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113783,\"node_id\":114188,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113783,\"node_id\":114189,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113783,\"node_id\":114190,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113783,\"node_id\":114191,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"6单元\",\"parent_id\":113689,\"node_id\":113706,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113706,\"node_id\":113784,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113784,\"node_id\":114192,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113784,\"node_id\":114193,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113784,\"node_id\":114194,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113784,\"node_id\":114195,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113784,\"node_id\":114196,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113784,\"node_id\":114197,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113706,\"node_id\":113785,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113785,\"node_id\":114198,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113785,\"node_id\":114199,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113785,\"node_id\":114200,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113785,\"node_id\":114201,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113785,\"node_id\":114202,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113785,\"node_id\":114203,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113706,\"node_id\":113786,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113786,\"node_id\":114204,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113786,\"node_id\":114205,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113786,\"node_id\":114206,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113786,\"node_id\":114207,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113786,\"node_id\":114208,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113786,\"node_id\":114209,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113706,\"node_id\":113787,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113787,\"node_id\":114210,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113787,\"node_id\":114211,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113787,\"node_id\":114212,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113787,\"node_id\":114213,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113787,\"node_id\":114214,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113787,\"node_id\":114215,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"7单元\",\"parent_id\":113689,\"node_id\":113707,\"child\":[{\"node_name\":\"1层\",\"parent_id\":113707,\"node_id\":113788,\"child\":[{\"node_name\":\"0101\",\"parent_id\":113788,\"node_id\":114216,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":113788,\"node_id\":114217,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0103\",\"parent_id\":113788,\"node_id\":114218,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0104\",\"parent_id\":113788,\"node_id\":114219,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0105\",\"parent_id\":113788,\"node_id\":114220,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0106\",\"parent_id\":113788,\"node_id\":114221,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":113707,\"node_id\":113789,\"child\":[{\"node_name\":\"0201\",\"parent_id\":113789,\"node_id\":114222,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":113789,\"node_id\":114223,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0203\",\"parent_id\":113789,\"node_id\":114224,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0204\",\"parent_id\":113789,\"node_id\":114225,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0205\",\"parent_id\":113789,\"node_id\":114226,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0206\",\"parent_id\":113789,\"node_id\":114227,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"3层\",\"parent_id\":113707,\"node_id\":113790,\"child\":[{\"node_name\":\"0301\",\"parent_id\":113790,\"node_id\":114228,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0302\",\"parent_id\":113790,\"node_id\":114229,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0303\",\"parent_id\":113790,\"node_id\":114230,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0304\",\"parent_id\":113790,\"node_id\":114231,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0305\",\"parent_id\":113790,\"node_id\":114232,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0306\",\"parent_id\":113790,\"node_id\":114233,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"4层\",\"parent_id\":113707,\"node_id\":113791,\"child\":[{\"node_name\":\"0401\",\"parent_id\":113791,\"node_id\":114234,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0402\",\"parent_id\":113791,\"node_id\":114235,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0403\",\"parent_id\":113791,\"node_id\":114236,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0404\",\"parent_id\":113791,\"node_id\":114237,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0405\",\"parent_id\":113791,\"node_id\":114238,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0406\",\"parent_id\":113791,\"node_id\":114239,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"}],\"level\":\"2\"},{\"node_name\":\"B区\",\"parent_id\":111056,\"node_id\":136812,\"child\":[{\"node_name\":\"1栋\",\"parent_id\":136812,\"node_id\":136813,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":136813,\"node_id\":136814,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136814,\"node_id\":136815,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136815,\"node_id\":136816,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"},{\"node_name\":\"2栋\",\"parent_id\":136812,\"node_id\":136817,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":136817,\"node_id\":136818,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136818,\"node_id\":136820,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136820,\"node_id\":136824,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136820,\"node_id\":136825,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136818,\"node_id\":136821,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136821,\"node_id\":136826,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136821,\"node_id\":136827,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"2单元\",\"parent_id\":136817,\"node_id\":136819,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136819,\"node_id\":136822,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136822,\"node_id\":136828,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136822,\"node_id\":136829,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136819,\"node_id\":136823,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136823,\"node_id\":136830,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136823,\"node_id\":136831,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"}],\"level\":\"2\"},{\"node_name\":\"管理处\",\"parent_id\":111056,\"node_id\":111060,\"child\":[],\"level\":\"999\"}],\"level\":\"1\"},\"current_node_id\":113694}");

        //测试数据2：二道门不存在（level=2不存在的json）
        obj2 = JSONObject.parseObject("{\"tree\":{\"node_name\":\"测试无二道门\",\"parent_id\":-1,\"node_id\":136779,\"child\":[{\"node_name\":\"管理处\",\"parent_id\":136779,\"node_id\":136810,\"child\":[],\"level\":\"999\"},{\"node_name\":\"1栋\",\"parent_id\":136779,\"node_id\":136780,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":136780,\"node_id\":136782,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136782,\"node_id\":136786,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136786,\"node_id\":136794,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136786,\"node_id\":136795,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136782,\"node_id\":136787,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136787,\"node_id\":136796,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136787,\"node_id\":136797,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"2单元\",\"parent_id\":136780,\"node_id\":136783,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136783,\"node_id\":136788,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136788,\"node_id\":136798,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136788,\"node_id\":136799,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136783,\"node_id\":136789,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136789,\"node_id\":136800,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136789,\"node_id\":136801,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"},{\"node_name\":\"2栋\",\"parent_id\":136779,\"node_id\":136781,\"child\":[{\"node_name\":\"1单元\",\"parent_id\":136781,\"node_id\":136784,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136784,\"node_id\":136790,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136790,\"node_id\":136802,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136790,\"node_id\":136803,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136784,\"node_id\":136791,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136791,\"node_id\":136804,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136791,\"node_id\":136805,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"},{\"node_name\":\"2单元\",\"parent_id\":136781,\"node_id\":136785,\"child\":[{\"node_name\":\"1层\",\"parent_id\":136785,\"node_id\":136792,\"child\":[{\"node_name\":\"0101\",\"parent_id\":136792,\"node_id\":136806,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0102\",\"parent_id\":136792,\"node_id\":136807,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"},{\"node_name\":\"2层\",\"parent_id\":136785,\"node_id\":136793,\"child\":[{\"node_name\":\"0201\",\"parent_id\":136793,\"node_id\":136808,\"child\":[],\"level\":\"6\"},{\"node_name\":\"0202\",\"parent_id\":136793,\"node_id\":136809,\"child\":[],\"level\":\"6\"}],\"level\":\"5\"}],\"level\":\"4\"}],\"level\":\"3\"}],\"level\":\"1\"},\"current_node_id\":136779}\n");


    }

}
