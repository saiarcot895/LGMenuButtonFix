package com.saikrishna.lgmenubuttonfix;

import android.app.Activity;
import android.os.Build;
import android.view.KeyEvent;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Created by saikrishna on 4/18/15.
 */
public class MenuButtonFix implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        try {
            // Check to see if this package uses the class
            lpparam.classLoader.loadClass("android.support.v7.app.ActionBarActivity");
        } catch (ClassNotFoundException e) {
            return;
        }
        try {
            findAndHookMethod("android.support.v7.app.ActionBarActivity", lpparam.classLoader, "onKeyDown", int.class, KeyEvent.class, new XC_MethodReplacement() {

                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (methodHookParam.args[0].equals(KeyEvent.KEYCODE_MENU) && "LGE".equalsIgnoreCase(Build.BRAND)) {
                        return true;
                    }
                    return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("Unable to add onKeyDown hook for " + lpparam.packageName);
            XposedBridge.log(e);
        }
        try {
            findAndHookMethod("android.app.Activity", lpparam.classLoader, "onKeyUp", int.class, KeyEvent.class, new XC_MethodReplacement() {

                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam)  throws Throwable {
                    Activity scheme = (Activity) methodHookParam.thisObject;
                    if (methodHookParam.args[0].equals(KeyEvent.KEYCODE_MENU) && "LGE".equalsIgnoreCase(Build.BRAND)) {
                        scheme.openOptionsMenu();
                        return true;
                    }
                    return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("Unable to add onKeyUp hook for " + lpparam.packageName);
            XposedBridge.log(e);
        }
    }
}
