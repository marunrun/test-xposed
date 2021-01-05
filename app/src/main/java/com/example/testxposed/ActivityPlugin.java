package com.example.testxposed;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.example.testxposed.dialog.PluginDialog;
import com.sky.xposed.common.ui.view.SimpleItemView;
import com.sky.xposed.common.util.ResourceUtil;
import com.sky.xposed.javax.MethodHook;
import com.sky.xposed.javax.XposedUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ActivityPlugin {
    private final Context context;
    private XC_LoadPackage.LoadPackageParam mLoadPackageParam;
    private boolean timerEnable;

    private ActivityPlugin(Build build) {
        context = build.mContext;
        mLoadPackageParam = build.mLoadPackageParam;
        timerEnable = getDefaultSharedPreferences().getBoolean(Integer.toString(Constant.XFlag.ENABLE_TIMER), false);
    }

    public void setEnable(boolean enable) {
        timerEnable = enable;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return context.getSharedPreferences(Constant.Name.TAG, Context.MODE_PRIVATE);
    }

    public void handleLoadPackage() {
        XposedHelpers.findAndHookMethod("com.taihebase.activity.base.BaseNewActivity", context.getClassLoader(), "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log(Constant.Name.TITLE + "BaseNewActivity");
                Activity activity = (Activity) param.thisObject;
                XposedBridge.log(Constant.Name.TITLE + activity.getClass().getName());

                if (TextUtils.equals(
                        "com.showstartfans.activity.activitys.usercenter.UserSettingActivity",
                        activity.getClass().getName())) {
                    onHandleSettings(activity);
                }
            }
        });


        Class<?> builderClass = XposedHelpers.findClass("okhttp3.FormBody.Builder", context.getClassLoader());
        Class<?> httpCallBack = XposedHelpers.findClass("com.taihebase.activity.network.HttpCallBack", context.getClassLoader());
        XposedHelpers.findAndHookMethod("com.taihebase.activity.network.HttpUtil", context.getClassLoader(), "post", Context.class, String.class, builderClass, httpCallBack, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("" + param.args[1]);
                if (timerEnable) {
                    String hour = getDefaultSharedPreferences().getString(Integer.toString(Constant.XFlag.TIMER_HOUR), "");
                    String minute = getDefaultSharedPreferences().getString(Integer.toString(Constant.XFlag.TIMER_MINUTE), "");
                    XposedBridge.log("hour: " + hour);
                    XposedBridge.log("minute: " + minute);


                    Object arg = param.args[1];
//                    if (arg.toString().contains("order/order")) {
//                        param.args[1] = "http://10.20.0.20:9504/get";
//                    }
                }

                super.beforeHookedMethod(param);
            }
        });


    }

    private void onHandleSettings(Activity activity) {

        View view = activity.findViewById(ResourceUtil.getId(activity, "about_we"));
        ViewGroup viewGroup = (ViewGroup) view.getParent();

        final int index = viewGroup.indexOfChild(view);

        SimpleItemView viewDing = new SimpleItemView(activity);
        viewDing.getNameView().setTextSize(14);
        viewDing.setName(Constant.Name.TITLE);
        viewDing.setOnClickListener(v -> {
            // 打开设置
            openSettings(activity);
        });
        viewGroup.addView(viewDing, index);
    }


    public void openSettings(Activity activity) {
        PluginDialog pluginDialog = new PluginDialog(this);
        pluginDialog.show(activity.getFragmentManager(), "plugin");
    }

    public MethodHook findMethod(String className, String methodName, Object... parameterTypes) {
        return XposedUtil.findMethod(className, methodName, parameterTypes);
    }


    public static class Build {

        private final Context mContext;
        private XC_LoadPackage.LoadPackageParam mLoadPackageParam;

        public Build(Context context) {
            mContext = context;
        }

        public Build setLoadPackageParam(XC_LoadPackage.LoadPackageParam loadPackageParam) {
            mLoadPackageParam = loadPackageParam;
            return this;
        }

        public ActivityPlugin build() {
            return new ActivityPlugin(this);
        }
    }
}
