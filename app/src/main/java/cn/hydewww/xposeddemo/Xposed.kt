package cn.hydewww.xposeddemo

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Xposed : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
//        val c = Class.forName("android.hardware.Camera")
//        val m = c.getDeclaredMethod("open", Int::class.java)
//        m.isAccessible = true
//        XposedBridge.hookMethod(m, object : XC_MethodHook() {})
        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader,
                "open", Int::class.java, object : XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("相机: " + lpparam.packageName)
                val exception = java.lang.RuntimeException("")
                param.result = exception
            }
        })
    }
}