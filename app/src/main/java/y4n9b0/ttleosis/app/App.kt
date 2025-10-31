package y4n9b0.ttleosis.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlin.properties.Delegates

/**
 * 另一种 Application 全局单例(调用更简单 app vs App.INSTANCE)：
 *
 * private var instance: App by Delegates.notNull()
 * val app get() = instance
 *
 * class App : Application() {
 *     init { instance = this }
 * }
 */
class App : Application(), DefaultLifecycleObserver {

    init {
        instance = this
    }

    override fun onCreate() {
        super<Application>.onCreate()
        if (isAppMainProcess()) {
            init()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.i("App", "onAppForeground")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.i("App", "onAppBackground")
    }

    private fun init() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun isAppMainProcess(): Boolean {
        val mainAppProcessName = BuildConfig.APPLICATION_ID
        val currentProcessName = getCurrentProcessName()
        return mainAppProcessName.equals(currentProcessName, true)
    }

    private fun getCurrentProcessName(): String? {
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        val currentPid = android.os.Process.myPid()
        runningApps.forEach {
            if (it.pid == currentPid) {
                return it.processName
            }
        }
        return null
    }

    companion object {
        private var instance: App by Delegates.notNull()
        val INSTANCE get() = instance
    }
}