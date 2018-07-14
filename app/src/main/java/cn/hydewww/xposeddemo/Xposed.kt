package cn.hydewww.xposeddemo

import android.app.AndroidAppHelper
import android.net.Uri
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Xposed : IXposedHookLoadPackage {

    val uri = Uri.parse("content://cn.hydewww.xposeddemo.provider/switch")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
//        val c = Class.forName("android.hardware.Camera")
//        val m = c.getDeclaredMethod("open", Int::class.java)
//        m.isAccessible = true
//        XposedBridge.hookMethod(m, object : XC_MethodHook() {})
        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader,
                "open", Int::class.java, object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                val context =  AndroidAppHelper.currentApplication()
                var cursor = context.contentResolver.query(uri, null, null, null, null)
                var state = true
                if (cursor != null){
                    cursor.moveToFirst()
                    state = cursor.getInt(cursor.getColumnIndex("state")) == 1
                    log("State: " + state.toString())
                    cursor.close()
                }
                if (state){
                    Toast.makeText(context, "调用相机", Toast.LENGTH_LONG).show()
                }
//                val exception = java.lang.RuntimeException("")
//                param.result = exception
                log("相机: " + lpparam.packageName)
                super.afterHookedMethod(param)
            }
        })
    }
}