package com.mozhimen.taskk.temps

import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.elemk.commons.ISuspendA_Listener
import com.mozhimen.kotlin.elemk.commons.ISuspend_Listener
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.taskk.bases.BaseTaskKWakeBefDestroy
import com.mozhimen.kotlin.utilk.android.util.e
import kotlinx.coroutines.*

@OApiCall_BindViewLifecycle
@OApiCall_BindLifecycle
@OApiInit_ByLazy
class TaskKPoll : BaseTaskKWakeBefDestroy() {
    private var _pollingScope: CoroutineScope? = null
    @Volatile
    private var _time = 0

    override fun isActive(): Boolean =
        _pollingScope != null && _pollingScope!!.isActive

    /**
     *
     * @param interval Long 循环间隔时长
     * @param times Int 循环次数
     * @param task SuspendFunction1<Int, Unit>
     */
    fun start(intervalMillis: Long, times: Int, task: ISuspendA_Listener<Int>/*suspend (Int) -> Unit*/, onFinish: ISuspend_Listener? = null) {
        if (isActive()) return
        _time = times
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            while (isActive && _time > 0) {
                try {
                    task.invoke(_time)
                } catch (e: Exception) {
                    if (e is CancellationException) return@launch
                    e.printStackTrace()
                    e.message?.e(TAG)
                }
                _time--
                delay(intervalMillis)
            }
            onFinish?.invoke()
            this@TaskKPoll.cancel()
        }
        _pollingScope = scope
    }

    override fun cancel() {
        if (!isActive()) return
        _pollingScope?.cancel()
        _pollingScope = null
        _time = 0
    }
}