package com.example.testxposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.sky.xposed.common.util.Alog;
import com.sky.xposed.javax.MethodHook;
import com.virjar.sekiro.api.SekiroClient;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroRequestHandler;
import com.virjar.sekiro.api.SekiroResponse;

import java.net.Proxy;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClassIfExists;


public class Main implements IXposedHookLoadPackage, MethodHook.ThrowableCallback {

    private static final String TAG = "mrHook";

    @Override
    public void onThrowable(Throwable tr) {
        Alog.e("Throwable", tr);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (Constant.ShowStart.PACKAGE_NAME.equals(lpparam.packageName)) {
            findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "a", Context.class, new XC_MethodHook() {
                        @SuppressLint("PrivateApi")
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader = context.getClassLoader();


                            // 取消代理
                            findAndHookMethod("okhttp3.OkHttpClient$Builder", classLoader, "proxy", Proxy.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    param.setResult(this);
                                }
                            });

                            findAndHookMethod("com.taihebase.activity.base.BaseApplication", classLoader, "onCreate", new XC_MethodHook() {
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

                            findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getSaleStatus", new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    Log.d(TAG, "getSaleStatus");
                                    param.setResult(1);
                                }
                            });
                            findAndHookMethod("com.showstartfans.activity.model.pay.TicketBean", classLoader, "getRemainTicket", new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    Log.d(TAG, "getRemainTicket");
                                    param.setResult(100);
                                }
                            });

                            XposedHelpers.findAndHookMethod("g.l.a.k.e", classLoader, "getVersionCode", new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult("0.1");
                                }
                            });

                            // Sekiro
                            String strANDROID_ID = "mi5";//Settings.System.getString(AndroidAppHelper.currentApplication().getApplicationContext().getContentResolver(), Settings.System.ANDROID_ID); // UUID.randomUUID().toString();  // Settings.System.getString(AndroidAppHelper.currentApplication().getApplicationContext().getContentResolver(), Settings.System.ANDROID_ID);

                            String testHost = "marun.run";
                            //接口组名称
                            String groupName = "showstart";
                            //暴露的接口名称
                            String actionName = "getsign";

                            SekiroClient.start(testHost, strANDROID_ID, groupName)
                                    .registerHandler(actionName, new SekiroRequestHandler() {
                                        @Override
                                        public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
                                            try {
                                                Class<?> aClass = classLoader.loadClass("g.p.a.i.k");
                                                String res = (String) XposedHelpers.callStaticMethod(aClass, "d", sekiroRequest.getString("str1"),sekiroRequest.getString("str2"));
                                                sekiroResponse.success(res);
                                            } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    });
        }


    }
}
