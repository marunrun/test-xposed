package com.example.testxposed;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sky.xposed.common.util.Alog;
import com.sky.xposed.javax.MethodHook;
import com.sky.xposed.javax.XposedPlus;
import com.sky.xposed.javax.XposedUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage, MethodHook.ThrowableCallback {

    private static final String TAG = "mrHook";

    @Override
    public void onThrowable(Throwable tr) {
        Alog.e("Throwable", tr);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        if (!Constant.ShowStart.PACKAGE_NAME.equals(lpparam.packageName)) return;
        XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                "ᵢˋ", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = (Context) param.args[0];
                        ClassLoader classLoader = context.getClassLoader();

                        XposedHelpers.findAndHookMethod("com.taihebase.activity.utils.SecurityUtil", classLoader, "getNeedCapturePacket", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                Log.d(TAG, "getNeedCapturePacket");
                                super.afterHookedMethod(param);
                                param.setResult(true);
                            }
                        });

                        XposedHelpers.findAndHookMethod("com.taihebase.activity.base.BaseApplication", classLoader, "onCreate", new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                XposedBridge.log(TAG + "BaseApplication");
                                super.beforeHookedMethod(param);
                                Application application = (Application) param.thisObject;

                                if (TextUtils.equals(
                                        "com.showstartfans.activity.XiudongApplication",
                                        application.getClass().getName())) {

                                    XposedBridge.log(TAG + "XiudongApplication Success");

                                    ActivityPlugin plugin = new ActivityPlugin.Build(context).setLoadPackageParam(lpparam).build();

                                    plugin.handleLoadPackage();
                                }
                            }
                        });

                        XposedHelpers.findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getSaleStatus", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Log.d(TAG, "getSaleStatus");
                                param.setResult(1);
                            }
                        });
                        XposedHelpers.findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getRemainTicket", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Log.d(TAG, "getRemainTicket");
                                param.setResult(100);
                            }
                        });
                        XposedHelpers.findAndHookMethod("com.showstartfans.activity.update.UpdateBean", classLoader, "getVersionCode", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                param.setResult("0.1");
                            }
                        });

                    }
                });
    }


}
