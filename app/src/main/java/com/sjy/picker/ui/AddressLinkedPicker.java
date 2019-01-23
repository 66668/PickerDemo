package com.sjy.picker.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.sjy.picker.R;
import com.sjy.picker.ui.pickerutils.AddressData;
import com.sjy.picker.ui.pickerutils.BaseWheelPicker;
import com.sjy.picker.ui.pickerutils.FifthBean;
import com.sjy.picker.ui.pickerutils.FourthBean;
import com.sjy.picker.ui.pickerutils.LinkedFirstItem;
import com.sjy.picker.ui.pickerutils.LinkedFourItem;
import com.sjy.picker.ui.pickerutils.LinkedSecondItem;
import com.sjy.picker.ui.pickerutils.LinkedThirdItem;
import com.sjy.picker.ui.pickerutils.OnWheelLinkedListener;
import com.sjy.picker.ui.pickerutils.SecondBean;
import com.sjy.picker.ui.pickerutils.ThirdBean;
import com.sjy.picker.ui.pickerutils.WheelView;

import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;

/**
 * ***********************************************
 * **                  _oo0oo_                  **
 * **                 o8888888o                 **
 * **                 88" . "88                 **
 * **                 (| ^!^ |)                 **
 * **                 0\  =  /0                 **
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
 * 动态住址联动选择器：最多5个选择器，最少2个选择器（数据可由后台获取）,5级联动选择器。默认只初始化第一级数据，第2 3 4 5级数据由联动获得。
 * <p>
 * 具体模式：
 * （1）最全5个-大门门禁：小区/楼号/单元/楼层/房间号
 * （2）小区门禁2-4个：小区/楼号/单元/楼层/房间号 或 楼号/单元/楼层/房间号（小区数据为空）或 单元可为空（eg:楼号/楼层/房间号）
 * （3）楼门禁2-3个：单元/楼层/房间号 或 楼层/房间号（单元可为空）
 * （4）单元门禁2个：楼层/房间号 （最低标准）
 * <p>
 * 使用规则：
 * xml布局中添加AddressLinkedPicker的完整路径
 * 代码中
 */
public class AddressLinkedPicker<Fst extends LinkedFirstItem<Snd>//第一条数据
        , Snd extends LinkedSecondItem<Trd>//第二条数据
        , Trd extends LinkedThirdItem<Fur>//第三条数据
        , Fur extends LinkedFourItem<Fiv>//第四条数据
        , Fiv>//第五条数据
        extends BaseWheelPicker {

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

    OnWheelLinkedListener onWheelLinkedListener;

    /**
     * 设置滑动过程数据联动监听
     *
     * @param onWheelLinkedListener
     */
    public void setOnWheelLinkedListener(OnWheelLinkedListener onWheelLinkedListener) {
        this.onWheelLinkedListener = onWheelLinkedListener;
    }
    //================================构造========================================


    public AddressLinkedPicker(Context context) {
        super(context);
    }

    public AddressLinkedPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AddressLinkedPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AddressLinkedPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 构造，传递数据，并处理数据
     *
     * @param activity
     */
    public AddressLinkedPicker(Activity activity, AddressData provider) {
        super(activity);
        this.provider = provider;
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
        this.selectFifthPosition = selectFifthPosition;
        this.selectSecondPosition = selectSecondPosition;
        this.selectThirdPosition = selectThirdPosition;
        this.selectFourthPosition = selectFourthPosition;
        this.selectFifthPosition = selectFifthPosition;
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
     * 将数据绑定到view中
     * 代码创建布局
     *
     * @return
     */
    private void buildPicker() {

        //--------------------------------------------------------------------
        //-----------------------------根据showNum,动态添加选择器个数---------------------------------------
        //--------------------------------------------------------------------
        //01创建
        WheelView firstView = createWheelView();
        firstView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, firstColumnWeight));
        addView(firstView);
        //01标签
        TextView labelView1 = createLabelView();
        labelView1.setText(provider.lables[0]);
        addView(labelView1);
        //01绑定数据
        firstView.setItems(provider.initFirstData(), selectFirstPosition);

        //02创建
        final WheelView secondView = createWheelView();
        secondView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, firstColumnWeight));
        addView(secondView);
        //02标签
        TextView labelView2 = createLabelView();
        labelView2.setText(provider.lables[1]);
        addView(labelView2);
        //02绑定数据
        secondView.setItems(provider.initSecondData(selectFirstPosition), selectSecondPosition);

        //03创建
        final WheelView thirdView = createWheelView();
        if (provider.showNum >= 3) {
            thirdView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(thirdView);
            //03标签
            TextView labelView3 = createLabelView();
            labelView3.setText(provider.lables[2]);
            addView(labelView3);
            //03绑定数据
            thirdView.setItems(provider.initThirdData(selectFirstPosition, selectSecondPosition), selectThirdPosition);

        }
        //04创建
        final WheelView fourthView = createWheelView();
        if (provider.showNum >= 4) {
            fourthView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(fourthView);
            //04标签
            TextView labelView4 = createLabelView();
            labelView4.setText(provider.lables[3]);
            addView(labelView4);
            //04绑定数据
            fourthView.setItems(provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition), selectFourthPosition);

        }
        //05创建
        final WheelView fifthView = createWheelView();
        if (provider.showNum == 5) {
            fifthView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, firstColumnWeight));
            addView(fifthView);
            //05标签
            TextView labelView5 = createLabelView();
            labelView5.setText(provider.lables[4]);
            addView(labelView5);
            //05绑定数据
            fifthView.setItems(provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition), selectFifthPosition);
        }

        //--------------------------------------------------------------------
        //-----------------------------添加监听---------------------------------------
        //--------------------------------------------------------------------

        //01监听
        firstView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectFirstItem = (Fst) provider.initFirstData().get(index);
                //索引position
                selectFirstPosition = index;
                selectSecondPosition = 0;//重置第二级索引
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
                //根据第一级数据获取第二级数据
                List<SecondBean> secondBeans = provider.initSecondData(selectFirstPosition);
                selectSecondItem = (Snd) secondBeans.get(selectSecondPosition);
                secondView.setItems(secondBeans, selectSecondPosition);

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
                    onWheelLinkedListener.onWheelLinked(selectFirstPosition, 0, 0, 0, 0);
                }
            }

        });

        //02监听
        secondView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
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
                    onWheelLinkedListener.onWheelLinked(selectFirstPosition, selectSecondPosition, 0, 0, 0);
                }
            }

        });

        //03监听
        if (provider.showNum >= 3) {
            thirdView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    if (provider.showNum >= 3) {
                        selectThirdtem = (Trd) provider.initThirdData(selectFirstPosition, selectSecondPosition).get(index);
                        selectThirdPosition = index;
                    }
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
                        selectFourthItem = (Fur) fourthBeans.get(selectFourthPosition);
                        fourthView.setItems(fourthBeans, selectFourthPosition);
                        //根据第4级数据获取第5级数据
                        List<FifthBean> fifthBeans = provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition);
                        selectFifthItem = (Fiv) fifthBeans.get(selectFifthPosition);
                        fifthView.setItems(fifthBeans, selectFifthPosition);
                    }
                    //回调监听
                    if (onWheelLinkedListener != null) {
                        onWheelLinkedListener.onWheelLinked(selectFirstPosition, selectSecondPosition, selectThirdPosition, 0, 0);
                    }

                }
            });
        }

        //04监听
        if (provider.showNum >= 4) {
            fourthView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    if (provider.showNum >= 4) {
                        selectFourthItem = (Fur) provider.initFourthData(selectFirstPosition, selectSecondPosition, selectThirdPosition).get(selectFourthPosition);
                        selectFourthPosition = index;
                    }
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
                        onWheelLinkedListener.onWheelLinked(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, 0);
                    }
                }
            });
        }

        //05监听
        if (provider.showNum == 5) {
            fifthView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    //
                    if (provider.showNum == 5) {
                        selectFifthItem = (Fiv) provider.initFifthData(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition).get(index);
                        selectFifthPosition = index;
                    }
                    //无重置的索引了
                    //无关联的下一级了
                    //回调监听
                    if (onWheelLinkedListener != null) {
                        onWheelLinkedListener.onWheelLinked(selectFirstPosition, selectSecondPosition, selectThirdPosition, selectFourthPosition, selectFifthPosition);
                    }
                }
            });
        }
    }

    /**
     * 布局中先构建布局
     */
    @Override
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
     * 没有加载出数据，使用加载动画表示
     */
    private void buildProgress() {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.progress_rotate));
        addView(progressBar);
    }

    private void setWidth() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
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
