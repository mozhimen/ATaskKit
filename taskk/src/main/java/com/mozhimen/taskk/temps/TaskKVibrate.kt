package com.mozhimen.taskk.temps

import android.os.Vibrator
import androidx.annotation.RequiresPermission
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy
import com.mozhimen.kotlin.lintk.optins.manifest.uses_permission.OUsesPermission_VIBRATE
import com.mozhimen.kotlin.elemk.android.cons.CPermission
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.taskk.bases.BaseTaskKWakeBefDestroy
import com.mozhimen.kotlin.utilk.android.os.UtilKVibrator
import com.mozhimen.kotlin.utilk.kotlin.UtilKLazyJVM.lazy_ofNone

/**
 * @ClassName UtilKVibrate
 * @Description <uses-permission android:name="android.permission.VIBRATE" />
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/2/27 18:28
 * @Version 1.0
 */
@OApiCall_BindViewLifecycle
@OApiCall_BindLifecycle
@OApiInit_ByLazy
@OUsesPermission_VIBRATE
open class TaskKVibrate : BaseTaskKWakeBefDestroy() {
    protected var _vibrator: Vibrator? = null

    /**
     * 震动
     * @param duration Long
     */
    @RequiresPermission(CPermission.VIBRATE)
    @OUsesPermission_VIBRATE
    open fun start(duration: Long = 200L) {
        if (isActive()) return
        if (_vibrator == null) {
            _vibrator = UtilKVibrator.get(_context)
        }
        _vibrator!!.vibrate(duration)
    }

    override fun isActive(): Boolean {
        return _vibrator != null
    }

    /**
     * 停止
     */
    @RequiresPermission(CPermission.VIBRATE)
    @OUsesPermission_VIBRATE
    override fun cancel() {
        if (!isActive()) return
        _vibrator?.cancel()
        _vibrator = null
    }
}