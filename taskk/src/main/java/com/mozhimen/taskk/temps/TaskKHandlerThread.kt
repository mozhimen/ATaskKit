package com.mozhimen.taskk.temps

import android.os.Handler
import android.os.HandlerThread
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.os.UtilKHandler
import com.mozhimen.kotlin.utilk.android.os.UtilKHandlerThread
import com.mozhimen.taskk.bases.BaseTaskKWakeBefDestroy

/**
 * @ClassName TaskKHandlerThread
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2025/2/5 16:22
 * @Version 1.0
 */
@OApiInit_ByLazy
@OApiCall_BindLifecycle
@OApiCall_BindViewLifecycle
class TaskKHandlerThread : /*BaseWakeBefDestroyTaskK*/BaseTaskKWakeBefDestroy() {
    private var _handlerThread: HandlerThread? = null
    private var _handler: Handler? = null

    fun getHandlerThread(): HandlerThread? =
        _handlerThread

    fun getHandler(): Handler? =
        _handler

    fun start(threadName: String) {
        _handlerThread = UtilKHandlerThread.get(threadName)
        _handlerThread!!.start()
        _handler = UtilKHandler.get(_handlerThread!!.looper)
    }

    override fun isActive(): Boolean {
        return _handlerThread?.isAlive ?: false
    }

    override fun cancel() {
        try {
            _handlerThread?.join()
            _handlerThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}