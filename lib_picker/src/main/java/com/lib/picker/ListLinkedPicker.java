package com.lib.picker;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lib.picker.bean.FifthBean;
import com.lib.picker.bean.FirstBean;
import com.lib.picker.bean.FourthBean;
import com.lib.picker.bean.SecondBean;
import com.lib.picker.bean.ThirdBean;
import com.lib.picker.bean.AddressData;
import com.lib.picker.bean.base.LinkedFirstItem;
import com.lib.picker.bean.base.LinkedFourItem;
import com.lib.picker.bean.base.LinkedSecondItem;
import com.lib.picker.bean.base.LinkedThirdItem;
import com.lib.picker.listpicker.FirstPickerView;
import com.lib.picker.listpicker.PickerView;
import com.lib.picker.listpicker.OnPickerLinkedListener;

import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;

/**
 * 样式2：多级联动效果
 * ***********************************************
 * **                  _oo0oo_                  **
 * **                 o8888888o                 **
 * **                 88" . "88                 **
 * **                 (| ^!^ |)                 **
 * **                 0\  ¥  /0                 **
 * **               ___/'---'\___               **
 * **            .' \\\|     |// '.             **
 * **           / \\\|||  :  |||// \\           **
 * **          / _ ||||| -:- |||||- \\          **
 * **          | |  \\\\  -  /// |   |          **
 * **          | \_|  ''\---/''  |_/ |          **
 * **          \  .-\__  '-'  __/-.  /          **
 * **        ___'. .'  /--.--\  '. .'___        **
 * **     ."" '<  '.___\_<|>_/___.' >'  "".     **
 * **    | | : '-  \'.;'\ _ /';.'/ - ' : | |    **
 * **    \  \ '_.   \_ __\ /__ _/   .-' /  /    **
 * **====='-.____'.___ \_____/___.-'____.-'=====**
 * **                  '=---='                  **
 * ***********************************************
 * **              佛祖保佑  镇类之宝             **
 * ***********************************************
 * <p>
 * 多级联动选择器  布局构建类：最多5个选择器（数据由后台控制）,5级联动选择器。默认只初始化第一级数据，第2 3 4 5级数据由联动获得。
 * <p>
 * 具体模式：
 * （1）最全5个-大门门禁：小区/楼号/单元/楼层/房间号
 * （2）小区门禁2-4个：小区/楼号/单元/楼层/房间号 或 楼号/单元/楼层/房间号（小区数据为空）或 单元可为空（eg:楼号/楼层/房间号）
 * （3）楼门禁2-3个：单元/楼层/房间号 或 楼层/房间号（单元可为空）
 * （4）单元门禁2个：楼层/房间号 （最低标准）
 * (5) 层级门禁1个：房间号筛选 （2019-03-05新要求，可以挂到层节点）
 * <p>
 * 使用规则：
 * xml布局中添加AddressLinkedPicker的完整路径
 * 代码中
 */
public class ListLinkedPicker<Fst extends LinkedFirstItem<Snd>//第一条数据
        , Snd extends LinkedSecondItem<Trd>//第二条数据
        , Trd extends LinkedThirdItem<Fur>//第三条数据
        , Fur extends LinkedFourItem<Fiv>//第四条数据
        , Fiv>//第五条数据
        extends LinearLayout {
    private Context context;
    //================================变量--数据源变量========================================
    private AddressData provider = null;//数据源
    private Fst selectFirstItem;
    private Snd selectSecondItem;
    private Trd selectThirdtem;
    private Fur selectFourthItem;
    private Fiv selectFifthItem;
    private int selectFirstPosition = 0, selectSecondPosition = 0, selectThirdPosition = 0, selectFourthPosition = 0, selectFifthPosition = 0;//索引标记，默认选中第一个item数据
    //================================变量--view变量========================================
    protected float firstColumnWeight = 1.0f;//第1 2 3 4 5级显示的宽度比重

    //================================回调监听========================================

    OnPickerLinkedListener onWheelLinkedListener;

    /**
     * 设置滑动过程数据联动监听
     *
     * @param onWheelLinkedListener
     */
    public void setOnPickerLinkedListener(OnPickerLinkedListener onWheelLinkedListener) {
        this.onWheelLinkedListener = onWheelLinkedListener;
    }
    //================================构造========================================


    public ListLinkedPicker(Context context) {
        super(context);
        this.context = context;
        buildView();
    }

    public ListLinkedPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        buildView();
    }

    public ListLinkedPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        buildView();
    }

    public ListLinkedPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        buildView();
    }

    /**
     * 构造，传递数据，并处理数据
     *
     * @param context
     */
    public ListLinkedPicker(Activity context, AddressData provider) {
        super(context);
        this.context = context;
        this.provider = provider;
        buildView();
    }

    /**
     * 布局构建/支持update
     */
    public void buildView() {
        removeAllViews();
        setOrientation(HORIZONTAL);
        setGravity(CENTER_VERTICAL);
        if (provider == null) {
            //只构建加载动画
            buildProgress();
            return;
        }
        setWidth();
        buildPicker();
    }


    /**
     * 最好这样写
     *
     * @return
     */
//    public AddressLinkedPicker(Activity activity, IProvider<Fst, Snd, Trd, Fur, Fiv> provider) {
//        super(activity);
//        this.provider = provider;
//    }
    public void setData(AddressData provider) {
        this.provider = provider;
    }

    /**
     * 设置默认选中
     *
     * @param selectFirstPosition
     * @param selectSecondPosition
     * @param selectThirdPosition
     * @param selectFourthPosition
     * @param selectFifthPosition
     */

    public void setDefaultPosition(int selectFirstPosition, int selectSecondPosition, int selectThirdPosition, int selectFourthPosition, int selectFifthPosition) {
        this.selectFirstPosition = selectFirstPosition;
        this.selectSecondPosition = selectSecondPosition;
        this.selectThirdPosition = selectThirdPosition;
        this.selectFourthPosition = selectFourthPosition;
        this.selectFifthPosition = selectFifthPosition;

        if (onWheelLinkedListener != null) {
            onWheelLinkedListener.onWheelLinked(
                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, selectFifthPosition),
                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, selectFifthPosition));
        }

    }

    public Fst getSelectFirstItem() {
        if (selectFirstItem == null) {
            selectFirstItem = (Fst) provider.initFirstData().get(selectFirstPosition);
        }
        return selectFirstItem;
    }

    public Snd getSelectSecondItem() {
        if (selectSecondItem == null) {
            selectSecondItem = (Snd) provider.initSecondData(selectFirstPosition).get(selectFirstPosition);
        }
        return selectSecondItem;
    }

    public Trd getSelectThirdtem() {
        if (selectThirdtem == null) {
            selectThirdtem = (Trd) provider.initThirdData(selectFirstPosition, selectSecondPosition).get(selectThirdPosition);
        }
        return selectThirdtem;
    }

    public Fur getSelectFourthItem() {
        if (selectFourthItem == null) {
            selectFourthItem = (Fur) provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition).get(selectFourthPosition);
        }
        return selectFourthItem;
    }

    public Fiv getSelectFifthItem() {
        if (selectFifthItem == null) {
            selectFifthItem = (Fiv) provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition).get(selectFifthPosition);
        }
        return selectFifthItem;
    }

    /**
     * 获取结果
     */
    public String getRoomId(int first, int second, int third, int four, int five) {
        FirstBean firstBean = provider.getFirstData().get(first);
        if (provider.showNum == 1) {
            return "" + firstBean.getNodeId();
        } else if (provider.showNum == 2) {
            SecondBean secondBean = firstBean.getLists().get(second);
            return "" + secondBean.getNodeId();
        } else if (provider.showNum == 3) {
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            return "" + thirdBean.getNodeId();
        } else if (provider.showNum == 4) {
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            FourthBean fourthBean = thirdBean.getLists().get(four);
            return "" + fourthBean.getNodeId();
        } else if (provider.showNum == 5) {
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            FourthBean fourthBean = thirdBean.getLists().get(four);
            FifthBean fifthBean = fourthBean.getLists().get(five);
            return "" + fifthBean.getNodeId();
        }
        return "";
    }

    /**
     * 获取结果
     */
    public String getRoomName(int first, int second, int third, int four, int five) {
        FirstBean firstBean = provider.getFirstData().get(first);

        if (provider.showNum == 1) {//
            return firstBean.getName() + "房间";
        } else if (provider.showNum == 2) {//单元下
            SecondBean secondBean = firstBean.getLists().get(second);
            return firstBean.getName() + "层" + secondBean.getName() + "房间";
        } else if (provider.showNum == 3) {//楼
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            return secondBean.getName() + "层" + thirdBean.getName() + "房间";
        } else if (provider.showNum == 4) {//分区下
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            FourthBean fourthBean = thirdBean.getLists().get(four);

            return secondBean.getName() + "单元" + thirdBean.getName() + "层" + fourthBean.getName() + "房间";
        } else if (provider.showNum == 5) {//小区下
            SecondBean secondBean = firstBean.getLists().get(second);
            ThirdBean thirdBean = secondBean.getLists().get(third);
            FourthBean fourthBean = thirdBean.getLists().get(four);
            FifthBean fifthBean = fourthBean.getLists().get(five);
            return secondBean.getName() + "楼" + thirdBean.getName() + "单元" + fourthBean.getName() + "层" + fifthBean.getName() + "房间";
        }
        return "";
    }

    /**
     * 将数据绑定到view中
     * 代码创建布局
     *
     * @return
     */
    private PickerView secondView = null;
    private PickerView thirdView = null;
    private PickerView fourthView = null;
    private PickerView fifthView = null;

    /**
     * 最终构建
     */
    private void buildPicker() {

        //--------------------------------------------------------------------
        //-----------------------------根据showNum,动态添加选择器个数---------------------------------------
        //--------------------------------------------------------------------
        //01创建
        FirstPickerView firstView = new FirstPickerView(context);
        firstView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, firstColumnWeight));
        addView(firstView);

        //01绑定数据
        firstView.setFirstList(provider.initFirstData(), selectFirstPosition);

        //02创建
        if (provider.showNum >= 2) {
            secondView = null;
            secondView = new PickerView(context);
            secondView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(secondView);

            //02绑定数据
            secondView.setItems(provider.initSecondData(selectFirstPosition), selectSecondPosition);
        }

        //03创建
        if (provider.showNum >= 3) {
            thirdView = null;
            thirdView = new PickerView(context);
            thirdView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(thirdView);//03标签

            //03绑定数据
            thirdView.setItems(provider.initThirdData(selectFirstPosition, selectSecondPosition), selectThirdPosition);
        }

        //04创建
        if (provider.showNum >= 4) {
            fourthView = null;
            fourthView = new PickerView(context);
            fourthView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(fourthView);

            //04绑定数据
            fourthView.setItems(provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition), selectFourthPosition);

        }
        //05创建
        if (provider.showNum == 5) {
            fifthView = null;
            fifthView = new PickerView(context);
            fifthView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(fifthView);

            //05绑定数据
            fifthView.setItems(provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition), selectFifthPosition);
        }

        //--------------------------------------------------------------------
        //-----------------------------添加监听---------------------------------------
        //--------------------------------------------------------------------

        //01监听
        firstView.setOnItemSelectListener(new FirstPickerView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectFirstItem = (Fst) provider.initFirstData().get(index);
                //索引position
                selectFirstPosition = index;
                if (provider.showNum == 2) {
                    selectSecondPosition = 0;//重置第二级索引
                } else if (provider.showNum == 3) {
                    selectSecondPosition = 0;//重置第二级索引
                    selectThirdPosition = 0;//重置第三级索引
                } else if (provider.showNum == 4) {
                    selectSecondPosition = 0;//重置第二级索引
                    selectThirdPosition = 0;//重置第三级索引
                    selectFourthPosition = 0;//重置第4级索引
                } else if (provider.showNum == 5) {
                    selectSecondPosition = 0;//重置第二级索引
                    selectThirdPosition = 0;//重置第三级索引
                    selectFourthPosition = 0;//重置第4级索引
                    selectFifthPosition = 0;//重置第5级索引
                }
                //根据第一级数据获取第二级数据
                if (provider.showNum == 2) {
                    List<SecondBean> secondBeans = provider.initSecondData(selectFirstPosition);
                    selectSecondItem = (Snd) secondBeans.get(selectSecondPosition);
                    secondView.setItems(secondBeans, selectSecondPosition);
                }
                //根据第2级数据获取第3级数据
                if (provider.showNum == 3) {
                    List<SecondBean> secondBeans = provider.initSecondData(selectFirstPosition);
                    selectSecondItem = (Snd) secondBeans.get(selectSecondPosition);
                    secondView.setItems(secondBeans, selectSecondPosition);
                    //
                    List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                    selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                    thirdView.setItems(thirdBeans, selectThirdPosition);
                }
                //根据第3级数据获取第4级数据
                if (provider.showNum == 4) {
                    //
                    List<SecondBean> secondBeans = provider.initSecondData(selectFirstPosition);
                    selectSecondItem = (Snd) secondBeans.get(selectSecondPosition);
                    secondView.setItems(secondBeans, selectSecondPosition);
                    //根据第2级数据获取第3级数据
                    List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                    selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                    thirdView.setItems(thirdBeans, selectThirdPosition);
                    //根据第3级数据获取第4级数据
                    List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                    selectFourthItem = (Fur) fourthBeans.get(selectThirdPosition);
                    fourthView.setItems(fourthBeans, selectFourthPosition);
                }

                //根据第4级数据获取第5级数据
                if (provider.showNum == 5) {
                    //
                    List<SecondBean> secondBeans = provider.initSecondData(selectFirstPosition);
                    selectSecondItem = (Snd) secondBeans.get(selectSecondPosition);
                    secondView.setItems(secondBeans, selectSecondPosition);
                    //根据第2级数据获取第3级数据
                    List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                    selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                    thirdView.setItems(thirdBeans, selectThirdPosition);
                    //根据第3级数据获取第4级数据
                    List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                    selectFourthItem = (Fur) fourthBeans.get(selectThirdPosition);
                    fourthView.setItems(fourthBeans, selectFourthPosition);
                    //根据第4级数据获取第5级数据
                    List<FifthBean> fifthBeans = provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition);
                    selectFifthItem = (Fiv) fifthBeans.get(selectFifthPosition);
                    fifthView.setItems(fifthBeans, selectFifthPosition);
                }

                //回调监听
                if (onWheelLinkedListener != null) {
                    if (provider.showNum == 1) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, -1, -1, -1, -1),
                                getRoomName(selectFirstPosition, -1, -1, -1, -1));
                    } else if (provider.showNum == 2) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, 0, -1, -1, -1),
                                getRoomName(selectFirstPosition, 0, -1, -1, -1));
                    } else if (provider.showNum == 3) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, 0, 0, -1, -1),
                                getRoomName(selectFirstPosition, 0, 0, -1, -1));
                    } else if (provider.showNum == 4) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, 0, 0, 0, -1),
                                getRoomName(selectFirstPosition, 0, 0, 0, -1));
                    } else if (provider.showNum == 5) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, 0, 0, 0, 0),
                                getRoomName(selectFirstPosition, 0, 0, 0, 0));
                    }
                }
            }

        });

        //02监听
        if (provider.showNum >= 2) {
            secondView.setOnItemSelectListener(new PickerView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    selectSecondItem = (Snd) provider.initSecondData(selectFirstPosition).get(index);
                    selectSecondPosition = index;
                    //索引
                    if (provider.showNum == 3) {
                        selectThirdPosition = 0;//重置第三级索引
                    } else if (provider.showNum == 4) {
                        selectThirdPosition = 0;//重置第三级索引
                        selectFourthPosition = 0;//重置第4级索引
                    } else {
                        selectThirdPosition = 0;//重置第三级索引
                        selectFourthPosition = 0;//重置第4级索引
                        selectFifthPosition = 0;//重置第5级索引
                    }

                    //根据第2级数据获取第3级数据
                    if (provider.showNum == 3) {
                        List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                        selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                        thirdView.setItems(thirdBeans, selectThirdPosition);
                    }
                    //根据第3级数据获取第4级数据
                    if (provider.showNum == 4) {
                        //根据第2级数据获取第3级数据
                        List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                        selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                        thirdView.setItems(thirdBeans, selectThirdPosition);
                        //根据第3级数据获取第4级数据
                        List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                        selectFourthItem = (Fur) fourthBeans.get(selectFourthPosition);
                        fourthView.setItems(fourthBeans, selectFourthPosition);
                    }

                    //根据第4级数据获取第5级数据
                    if (provider.showNum == 5) {
                        //根据第2级数据获取第3级数据
                        List<ThirdBean> thirdBeans = provider.initThirdData(selectFirstPosition, selectSecondPosition);
                        selectThirdtem = (Trd) thirdBeans.get(selectThirdPosition);
                        thirdView.setItems(thirdBeans, selectThirdPosition);
                        //根据第3级数据获取第4级数据
                        List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                        selectFourthItem = (Fur) fourthBeans.get(selectFourthPosition);
                        fourthView.setItems(fourthBeans, selectFourthPosition);
                        //根据第4级数据获取第5级数据
                        List<FifthBean> fifthBeans = provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition);
                        selectFifthItem = (Fiv) fifthBeans.get(selectFifthPosition);
                        fifthView.setItems(fifthBeans, selectFifthPosition);
                    }

                    //回调监听
                    if (onWheelLinkedListener != null) {
                        if (provider.showNum == 2) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, -1, -1, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, -1, -1, -1));
                        } else if (provider.showNum == 3) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, 0, -1, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, 0, -1, -1));
                        } else if (provider.showNum == 4) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, 0, 0, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, 0, 0, -1));
                        } else if (provider.showNum == 5) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, 0, 0, 0),
                                    getRoomName(selectFirstPosition, selectSecondPosition, 0, 0, 0));
                        }
                    }
                }

            });
        }

        //03监听
        if (provider.showNum >= 3) {
            thirdView.setOnItemSelectListener(new PickerView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    selectThirdtem = (Trd) provider.initThirdData(selectFirstPosition, selectSecondPosition).get(index);
                    selectThirdPosition = index;
                    //索引
                    if (provider.showNum == 4) {
                        selectFourthPosition = 0;//重置第4级索引
                    } else {
                        selectFourthPosition = 0;//重置第4级索引
                        selectFifthPosition = 0;//重置第5级索引
                    }
                    //根据第3级数据获取第4级数据
                    if (provider.showNum == 4) {
                        //根据第3级数据获取第4级数据
                        List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                        selectFourthItem = (Fur) fourthBeans.get(selectFourthPosition);
                        fourthView.setItems(fourthBeans, selectFourthPosition);
                    }

                    //根据第4级数据获取第5级数据
                    if (provider.showNum == 5) {
                        //根据第3级数据获取第4级数据
                        List<FourthBean> fourthBeans = provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition);
                        Log.d("SJY", "fourthBeans=" + fourthBeans.size() + "--selectThirdPosition=" + selectThirdPosition);
                        selectFourthItem = (Fur) fourthBeans.get(selectFourthPosition);
                        fourthView.setItems(fourthBeans, selectFourthPosition);
                        //根据第4级数据获取第5级数据
                        List<FifthBean> fifthBeans = provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition);
                        selectFifthItem = (Fiv) fifthBeans.get(selectFifthPosition);
                        fifthView.setItems(fifthBeans, selectFifthPosition);
                    }
                    //回调监听
                    if (onWheelLinkedListener != null) {
                        if (provider.showNum == 3) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, -1, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, -1, -1));
                        } else if (provider.showNum == 4) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, 0, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, 0, -1));
                        } else if (provider.showNum == 5) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, 0, 0),
                                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, 0, 0));
                        }
                    }

                }
            });
        }

        //04监听
        if (provider.showNum >= 4) {
            fourthView.setOnItemSelectListener(new PickerView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    selectFourthItem = (Fur) provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition).get(selectFourthPosition);
                    selectFourthPosition = index;
                    //索引
                    if (provider.showNum == 5) {
                        selectFifthPosition = 0;//重置第5级索引
                    }
                    //根据第4级数据获取第5级数据
                    if (provider.showNum == 5) {
                        //根据第4级数据获取第5级数据
                        List<FifthBean> fifthBeans = provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition);
                        selectFifthItem = (Fiv) fifthBeans.get(selectFifthPosition);
                        fifthView.setItems(fifthBeans, selectFifthPosition);
                    }
                    //回调监听
                    if (onWheelLinkedListener != null) {
                        if (provider.showNum == 4) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, -1),
                                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, -1));
                        } else if (provider.showNum == 5) {
                            onWheelLinkedListener.onWheelLinked(
                                    getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, 0),
                                    getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, 0));
                        }
                    }
                }
            });
        }

        //05监听
        if (provider.showNum == 5) {
            fifthView.setOnItemSelectListener(new PickerView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    selectFifthItem = (Fiv) provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition).get(index);
                    selectFifthPosition = index;

                    //无重置的索引了
                    //无关联的下一级了
                    //回调监听
                    if (onWheelLinkedListener != null) {
                        onWheelLinkedListener.onWheelLinked(
                                getRoomId(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, selectFifthPosition),
                                getRoomName(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, selectFifthPosition));
                    }
                }
            });
        }
    }


    /**
     * 没有加载出数据，使用加载动画表示
     */
    private void buildProgress() {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.progress_rotate));
        addView(progressBar);
    }

    private void setWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        int width = 280;
        //根据provider的showNum，设置宽高
        if (provider.showNum == 2) {
            //布局宽度
            width = 560;
        } else if (provider.showNum == 3) {
            width = 700;
        } else if (provider.showNum == 4) {
            width = 860;
        } else if (provider.showNum == 5) {
            width = 980;
        }
        params.width = width;
        setLayoutParams(params);

    }

}
