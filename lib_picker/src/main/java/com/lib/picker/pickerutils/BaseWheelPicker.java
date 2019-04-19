package com.lib.picker.pickerutils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 滑轮选择器  参考：https://github.com/gzu-liyujiang/AndroidPicker
 * 该处只用于构建 选择器部分(dialog的centerView)
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseWheelPicker extends LinearLayout {
    protected Context context;
    protected float lineSpaceMultiplier = WheelView.LINE_SPACE_MULTIPLIER;
    protected int textPadding = WheelView.TEXT_PADDING;
    protected int textSize = WheelView.TEXT_SIZE;
    protected Typeface typeface = Typeface.DEFAULT;
    protected int textColorNormal = WheelView.TEXT_COLOR_NORMAL;
    protected int textColorFocus = WheelView.TEXT_COLOR_FOCUS;
    protected int labelTextColor = WheelView.TEXT_COLOR_FOCUS;
    protected int offset = WheelView.ITEM_OFF_SET;
    protected boolean cycleDisable = true;
    protected boolean useWeight = true;
    protected boolean textSizeAutoFit = true;
    protected WheelView.DividerConfig dividerConfig = new WheelView.DividerConfig();


    /**
     * ======================================构造==========================================
     */
    public BaseWheelPicker(Context context) {
        super(context);
        this.context =context;
        buildView();
    }

    public BaseWheelPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        buildView();
    }

    public BaseWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
        buildView();
    }

    public BaseWheelPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context =context;
        buildView();
    }

    /**
     * ======================================WheelView相关设置==========================================
     */

    public abstract void buildView();


    /**
     * 可用于设置每项的高度，范围为2-4
     */
    public final void setLineSpaceMultiplier(@FloatRange(from = 2, to = 4) float multiplier) {
        lineSpaceMultiplier = multiplier;
    }

    /**
     * Use {@link #setTextPadding(int)} instead
     */
    @Deprecated
    public void setPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    /**
     * 可用于设置每项的宽度，单位为dp
     */
    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(@ColorInt int textColorFocus, @ColorInt int textColorNormal) {
        this.textColorFocus = textColorFocus;
        this.textColorNormal = textColorNormal;
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        this.textColorFocus = textColor;
    }

    public void setLabelTextColor(int labelTextColor) {
        this.labelTextColor = labelTextColor;
    }

    /**
     * 设置分隔阴影是否可见
     */
    public void setShadowVisible(boolean shadowVisible) {
        if (null == dividerConfig) {
            dividerConfig = new WheelView.DividerConfig();
        }
        dividerConfig.setShadowVisible(shadowVisible);
    }

    /**
     * 设置分隔阴影颜色及透明度
     */
    public void setShadowColor(@ColorInt int color) {
        setShadowColor(color, 100);
    }

    /**
     * 设置分隔阴影颜色及透明度
     */
    public void setShadowColor(@ColorInt int color, @IntRange(from = 1, to = 255) int alpha) {
        if (null == dividerConfig) {
            dividerConfig = new WheelView.DividerConfig();
        }
        dividerConfig.setShadowColor(color);
        dividerConfig.setShadowAlpha(alpha);
    }

    /**
     * 设置分隔线是否可见
     */
    public void setDividerVisible(boolean visible) {
        if (null == dividerConfig) {
            dividerConfig = new WheelView.DividerConfig();
        }
        dividerConfig.setVisible(visible);
    }

    /**
     * @deprecated use {@link #setDividerVisible(boolean)} instead
     */
    @Deprecated
    public void setLineVisible(boolean visible) {
        setDividerVisible(visible);
    }

    /**
     * @deprecated use {@link #setDividerColor(int)} instead
     */
    @Deprecated
    public void setLineColor(@ColorInt int color) {
        setDividerColor(color);
    }

    /**
     * 设置分隔线颜色
     */
    public void setDividerColor(@ColorInt int lineColor) {
        if (null == dividerConfig) {
            dividerConfig = new WheelView.DividerConfig();
        }
        dividerConfig.setVisible(true);
        dividerConfig.setColor(lineColor);
    }

    /**
     * 设置分隔线长度比例
     */
    public void setDividerRatio(float ratio) {
        if (null == dividerConfig) {
            dividerConfig = new WheelView.DividerConfig();
        }
        dividerConfig.setRatio(ratio);
    }

    /**
     * 设置分隔线配置项，设置null将隐藏分割线及阴影
     */
    public void setDividerConfig(@Nullable WheelView.DividerConfig config) {
        if (null == config) {
            dividerConfig = new WheelView.DividerConfig();
            dividerConfig.setVisible(false);
            dividerConfig.setShadowVisible(false);
        } else {
            dividerConfig = config;
        }
    }

    /**
     * @deprecated use {@link #setDividerConfig(WheelView.DividerConfig)} instead
     */
    @Deprecated
    public void setLineConfig(WheelView.DividerConfig config) {
        setDividerConfig(config);
    }

    /**
     * 设置选项偏移量，可用来要设置显示的条目数，范围为1-5。
     * 1显示3条、2显示5条、3显示7条……
     */
    public void setOffset(@IntRange(from = 1, to = 5) int offset) {
        this.offset = offset;
    }

    /**
     * 设置是否禁用循环
     */
    public void setCycleDisable(boolean cycleDisable) {
        this.cycleDisable = cycleDisable;
    }

    /**
     * 是否使用比重来平分布局
     */
    public void setUseWeight(boolean useWeight) {
        this.useWeight = useWeight;
    }

    /**
     * 条目内容过长时是否自动减少字号来适配
     */
    public void setTextSizeAutoFit(boolean textSizeAutoFit) {
        this.textSizeAutoFit = textSizeAutoFit;
    }



    /**
     * 动态创建一个选择器
     *
     * @return
     */
    protected WheelView createWheelView() {
        WheelView wheelView = new WheelView(context);
        wheelView.setLineSpaceMultiplier(lineSpaceMultiplier);
        wheelView.setTextPadding(textPadding);
        wheelView.setTextSize(textSize);
        wheelView.setTypeface(typeface);
        wheelView.setTextColor(textColorNormal, textColorFocus);
        wheelView.setDividerConfig(dividerConfig);
        wheelView.setOffset(offset);
        wheelView.setCycleDisable(cycleDisable);
        wheelView.setUseWeight(useWeight);
        wheelView.setTextSizeAutoFit(textSizeAutoFit);
        return wheelView;
    }

    /**
     * 动态创建一个label标签
     *
     * @return
     */
    protected TextView createLabelView() {
        TextView labelView = new TextView(context);
        labelView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        labelView.setTextColor(labelTextColor);
        labelView.setTextSize(textSize);
        return labelView;
    }

}
