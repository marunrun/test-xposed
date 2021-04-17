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
                    @SuppressLint("PrivateApi")
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

//antServer
/*                        @RestController
                        class MyController {

                            @PostMapping("/sign")
                            public String sign() {
                                Class<?> clazzJDUtils = null;
                                try {
                                    clazzJDUtils = classLoader.loadClass("android.widget.Security");
                                    XposedBridge.log("load class:" + clazzJDUtils);
                                } catch (Exception e) {
                                    XposedBridge.log("load class err:" + Log.getStackTraceString(e));
                                }

                                String rc = (String) XposedHelpers.callStaticMethod(clazzJDUtils, "restoreString", "123");
                                XposedBridge.log("getSignFromJni = " + rc);
                                return rc;
                            }

                        }

                        Server server = AndServer.webServer(context)
                                .port(8080)
                                .timeout(10, TimeUnit.SECONDS)
                                .build();

// startup the server.
                        server.startup();*/

                        // NanoHttp server
/*                        class myHttpServer extends NanoHTTPD {
                            private static final String REQUEST_ROOT = "/";

                            public myHttpServer() throws IOException {
                                // 端口是8088，也就是说要通过http://127.0.0.1:8088来访当问
                                super(1111);
                                start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
                                XposedBridge.log("---xposed Server---");
                            }

                            @Override
                            public Response serve(IHTTPSession session) {
                                // log("serve");
                                //这个就是之前分析，重写父类的一个参数的方法，
                                //这里边已经把所有的解析操作已经在这里执行了
                                return super.serve(session);
                            }

                            @Override
                            public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {

                                XposedBridge.log(uri);

                                Class<?> clazzZy;
                                try {
                                    clazzZy = classLoader.loadClass("android.widget.Security");
                                    XposedBridge.log("load class:" + clazzZy);
                                } catch (Exception e) {
                                    XposedBridge.log("load class err:" + Log.getStackTraceString(e));
                                    return newFixedLengthResponse("load class is null");
                                }
                                if (uri.contains("sign")) {
                                    XposedBridge.log(parms.get("data"));
                                    return sign(clazzZy, parms.get("data"));
                                }


                                return super.serve(uri, method, headers, parms, files);
                            }

                            public Response sign(Class<?> clazzUse, String strData) {
                                String rc = (String) XposedHelpers.callStaticMethod(clazzUse, "restoreString", strData);
                                XposedBridge.log("sign = " + rc);
                                return newFixedLengthResponse(rc);
                            }
                        }

                        new myHttpServer();*/


                        // Sekiro
                        String strANDROID_ID =  "mi5";//Settings.System.getString(AndroidAppHelper.currentApplication().getApplicationContext().getContentResolver(), Settings.System.ANDROID_ID); // UUID.randomUUID().toString();  // Settings.System.getString(AndroidAppHelper.currentApplication().getApplicationContext().getContentResolver(), Settings.System.ANDROID_ID);

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
                                            Class<?> aClass = classLoader.loadClass("android.widget.Security");
                                            String res = (String) XposedHelpers.callStaticMethod(aClass, "restoreString", sekiroRequest.getString("data"));
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
