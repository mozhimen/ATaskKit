package com.mozhimen.taskk.temps

import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy
import com.mozhimen.kotlin.elemk.commons.ISuspend_Listener
import com.mozhimen.kotlin.elemk.commons.IA_Listener
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindViewLifecycle
import com.mozhimen.taskk.bases.BaseTaskKWakeBefDestroy
import com.mozhimen.kotlin.utilk.android.util.e
import kotlinx.coroutines.*

typealias ITaskKAsyncErrorListener = IA_Listener<Throwable>//(Throwable) -> Unit

@OApiCall_BindViewLifecycle
@OApiCall_BindLifecycle
@OApiInit_ByLazy
open class TaskKAsync : BaseTaskKWakeBefDestroy() {
    protected val _exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        throwable.message?.e(TAG)
        _taskKAsyncErrorListener?.invoke(throwable)        // 发生异常时的捕获
    }
    protected var _taskKAsyncErrorListener: ITaskKAsyncErrorListener? = null
    protected var _asyncScope: CoroutineScope = CoroutineScope(Dispatchers.IO + _exceptionHandler)

    open fun setErrorListener(listener: ITaskKAsyncErrorListener) {
        this._taskKAsyncErrorListener = listener
    }

    open fun execute(task: ISuspend_Listener) {
        if (isActive()) return
        _asyncScope.launch {
            task.invoke()
        }
    }

    override fun isActive(): Boolean = _asyncScope.isActive

    override fun cancel() {
        if (!isActive()) return
        _asyncScope.cancel()
    }
}