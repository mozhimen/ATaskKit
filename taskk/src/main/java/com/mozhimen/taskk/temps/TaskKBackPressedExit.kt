package com.mozhimen.taskk.temps

import com.mozhimen.kotlin.elemk.commons.I_Listener
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy
import com.mozhimen.taskk.bases.BaseTaskKWakeBefDestroy
import com.mozhimen.kotlin.utilk.android.widget.showToast

/**
 * @ClassName TaskKExitApp
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/26 16:57
 * @Version 1.0
 */
@OApiCall_BindViewLifecycle
@OApiCall_BindLifecycle
@OApiInit_ByLazy
open class TaskKBackPressedExit : BaseTaskKWakeBefDestroy() {
    protected var _exitWaitTime = 2000L//退出App判断时间
    protected var _firstClickTime = 0L//用来记录第一次点击的时间
    protected var _strTip = ""
    protected var _onExit: I_Listener? = null

    open fun setStrTip(strTip: String): TaskKBackPressedExit {
        _strTip = strTip
        return this
    }

    open fun setExitWaitTime(time: Long): TaskKBackPressedExit {
        _exitWaitTime = time
        return this
    }

    open fun setOnExitListener(onExit: I_Listener) {
        _onExit = onExit
    }

    open fun onBackPressed(): Boolean {
        val secondClickTime = System.currentTimeMillis()
        if (secondClickTime - _firstClickTime > _exitWaitTime) {
            _firstClickTime = secondClickTime
            if (_strTip.isNotEmpty())
                _strTip.showToast()
            return false
        }
        _onExit?.invoke()
//        UtilKApp.exitApp()
        return true
    }

    override fun isActive(): Boolean = _firstClickTime != 0L

    override fun cancel() {
        _firstClickTime = 0L
    }
}