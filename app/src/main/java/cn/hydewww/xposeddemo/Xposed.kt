package cn.hydewww.xposeddemo

import android.app.AndroidAppHelper
import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllMethods
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Xposed : IXposedHookLoadPackage {

    private val switch_URI = Uri.parse("content://cn.hydewww.xposeddemo.provider/switch")

    // 获取当前Switch状态
    fun getState(context: Context): Boolean {
        var state = true
        val cursor = context.contentResolver.query(switch_URI, null, null, null, null)
        if (cursor == null) // app未启动
            return false
        if (cursor.moveToFirst())
            state = cursor.getInt(cursor.getColumnIndex("state")) == 1
        cursor.close()
        return state
    }

    // 统一的hook操作
    fun addHook(lpparam: XC_LoadPackage.LoadPackageParam, className: String, methodName: String){
        hookAllMethods(Class.forName(className), methodName, object: XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = AndroidAppHelper.currentApplication() // 获取当前应用上下文
                if (!getState(context))
                    return
                log("${lpparam.packageName} 调用 $className.$methodName")
                Toast.makeText(context, "${lpparam.packageName} 调用 $className.$methodName", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 获取ContentProvider访问的模块
    fun getModuleName(uri: String): String {
        var moduleName = ""
        if ("content://com.android.contacts/contacts"  in uri){
            moduleName = "通讯录"
        } else if("content://com.android.calendar/calendars" in uri){
            moduleName = "日历"
        } else if("content://call_log/calls" in uri){
            moduleName = "通话记录"
        } else if("content://sms" in uri){
            moduleName = "短信"
        }
        return moduleName
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 不监控系统应用
        if ("com.android" in lpparam.packageName || "android" == lpparam.packageName)
            return

        addHook(lpparam, "android.hardware.Camera", "open")
        addHook(lpparam, "android.location.LocationManager", "requestLocationUpdates")
        addHook(lpparam, "android.media.AudioRecord", "startRecording")

        // 单独操作ContentResolver.query()
        hookAllMethods(Class.forName("android.content.ContentResolver"), "query", object: XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val moduleName = getModuleName(uri = param.args[0].toString())
                    if (moduleName == "")
                        return  // 防止getState死循环
                    val context =  AndroidAppHelper.currentApplication()    // 获取当前应用上下文
                    if (!getState(context))
                        return
                    log("${lpparam.packageName} 调用 $moduleName")
                    Toast.makeText(context, "${lpparam.packageName} 调用 $moduleName", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

}