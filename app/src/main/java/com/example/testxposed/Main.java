package com.example.testxposed;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.showstartfans.activity")) {
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
                                    super.afterHookedMethod(param);
                                    param.setResult(true);
                                }
                            });
                            XposedHelpers.findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getSaleStatus", new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult(1);
                                }
                            });
                            XposedHelpers.findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getRemainTicket", new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult(100);
                                }
                            });
                        }
                    });
        }
    }
}
