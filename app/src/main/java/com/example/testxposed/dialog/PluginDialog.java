package com.example.testxposed.dialog;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.testxposed.ActivityPlugin;
import com.example.testxposed.Constant;
import com.sky.xposed.common.ui.base.BaseDialogFragment;
import com.sky.xposed.common.ui.util.ViewUtil;
import com.sky.xposed.common.ui.view.CommonFrameLayout;
import com.sky.xposed.common.ui.view.EditTextItemView;
import com.sky.xposed.common.ui.view.SwitchItemView;
import com.sky.xposed.common.ui.view.TitleView;
import com.sky.xposed.common.util.DisplayUtil;

public class PluginDialog extends BaseDialogFragment {

    private CommonFrameLayout mCommonFrameLayout;
    private TitleView mToolbar;
    private TextView tvPrompt;
    private SwitchItemView timerEnable;
    private EditTextItemView timerHour;
    private EditTextItemView timerMinute;
    private ActivityPlugin plugin;

    @SuppressLint("ValidFragment")
    public PluginDialog(ActivityPlugin activityPlugin) {
        plugin = activityPlugin;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        // 初始化View
        mCommonFrameLayout = new CommonFrameLayout(getContext());
        mToolbar = mCommonFrameLayout.getTitleView();
        mToolbar.setBackgroundColor(Constant.Color.TOOLBAR);
        mToolbar.setTitle("辅助下单");
        // 创建相关的View
        createView(mCommonFrameLayout);


        return mCommonFrameLayout;
    }

    public void createView(CommonFrameLayout frameView) {
        int left = DisplayUtil.dip2px(getContext(), 15);
        int top = DisplayUtil.dip2px(getContext(), 12);

//        tvPrompt = new TextView(getContext());
//        tvPrompt.setTextSize(14);
//        tvPrompt.setBackgroundColor(Color.GRAY);
//        tvPrompt.setTextColor(Color.WHITE);
//        tvPrompt.setPadding(left, top, left, top);
        timerEnable = ViewUtil.newSwitchItemView(getContext(), "定时下单");
        timerEnable.setDesc("开启后，每次下单都会保存下来，定时再发送，为了抢票");


        timerHour = new EditTextItemView(getContext());
        timerHour.setInputType(com.sky.xposed.common.Constant.InputType.NUMBER_SIGNED);
        timerHour.setMaxLength(2);
        timerHour.setUnit("时");
        timerHour.setName("定时下单");
        timerHour.setExtendHint("单位(时)");

        timerMinute = new EditTextItemView(getContext());
        timerMinute.setInputType(com.sky.xposed.common.Constant.InputType.NUMBER_SIGNED);
        timerMinute.setMaxLength(2);
        timerMinute.setUnit("分");
        timerMinute.setName("定时下单");
        timerMinute.setExtendHint("单位(分)");
//        frameView.addContent(tvPrompt);
        frameView.addContent(timerEnable);
        frameView.addContent(timerHour);
        frameView.addContent(timerMinute);
    }


    @Override
    protected void initView(View view, Bundle args) {


        timerEnable.bind(getDefaultSharedPreferences(), Integer.toString(Constant.XFlag.ENABLE_TIMER), false, (view1, key, value) -> {
            plugin.setEnable(value);
            return true;
        });

        timerHour.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.TIMER_HOUR), "",
                (view12, key, value) -> true);

        timerMinute.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.TIMER_MINUTE), "",
                (view12, key, value) -> true);


    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return getSharedPreferences(Constant.Name.TAG);
    }

}
