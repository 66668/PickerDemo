package com.lib.picker.listpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lib.picker.bean.FifthBean;
import com.lib.picker.bean.FirstBean;
import com.lib.picker.bean.FourthBean;
import com.lib.picker.bean.SecondBean;
import com.lib.picker.bean.ThirdBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 一条筛选器的View视图（基于ListView实现）
 */
public class PickerView extends LinearLayout {

    /**
     * 参数
     */
    private Context context;
    private int selectedIndex;//选中项的索引
    private int initPosition = -1;//初始化默认选中项
    private OnItemSelectListener onItemSelectListener;
    //布局
    ListView lv_first, lv_second, lv_third, lv_fourth, lv_fifth;
    //适配器
    PickerListAdapter<List<FirstBean>> firstAdapter;
    PickerListAdapter<List<SecondBean>> secondAdapter;
    PickerListAdapter<List<ThirdBean>> thirdAdapter;
    PickerListAdapter<List<FourthBean>> fourthAdpater;
    PickerListAdapter<List<FifthBean>> fifthAdpater;

    public PickerView(Context context,) {
        this(context, null);
        createView();
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    /**
     * 创建数据
     */
    private void createView() {

    }

    public final void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public final void setItems(List<?> items) {
        this.items.clear();
        for (Object item : items) {
            if (item instanceof PickerItem) {
                this.items.add((PickerItem) item);
            } else if (item instanceof CharSequence || item instanceof Number) {
                this.items.add(new StringItem(item.toString()));
            } else {
                throw new IllegalArgumentException("please implements " + PickerItem.class.getName());
            }
        }
    }

    public final void setItems(List<?> items, int index) {
        setItems(items);
        setSelectedIndex(index);
    }

    public final void setSelectedIndex(int index) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int size = items.size();
        if (index == 0 || (index > 0 && index < size && index != selectedIndex)) {
            initPosition = index;
        }
    }

    public final void setItems(String[] list) {
        setItems(Arrays.asList(list));
    }

    public final void setItems(List<String> list, String item) {
        int index = list.indexOf(item);
        if (index == -1) {
            index = 0;
        }
        setItems(list, index);
    }

    public final void setItems(String[] list, int index) {
        setItems(Arrays.asList(list), index);
    }

    public final void setItems(String[] items, String item) {
        setItems(Arrays.asList(items), item);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (items == null || items.size() == 0) {
            return;
        }

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    }


    /**
     * 获取选项个数
     */
    protected int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * 用于兼容旧版本的纯字符串条目
     */
    private static class StringItem implements PickerItem {
        private String name;

        private StringItem(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    public interface OnItemSelectListener {
        /**
         * 滑动选择回调
         *
         * @param index 当前选择项的索引
         */
        void onSelected(int index);

    }


    /**
     * dp转换为px
     */
    private int toPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pxValue = (int) (dpValue * scale + 0.5f);
        return pxValue;
    }

    /**
     * px转换为dp
     */
    private int toDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dpValue = (int) (pxValue / scale + 0.5f);
        return dpValue;
    }

}
