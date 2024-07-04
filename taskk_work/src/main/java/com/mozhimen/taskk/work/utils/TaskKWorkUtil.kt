package com.mozhimen.taskk.work.utils

import androidx.work.ListenableWorker.Result

/**
 * @ClassName TaskKWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/7/4
 * @Version 1.0
 */
fun Boolean.boolean2result(): Result =
    TaskKWorkUtil.boolean2result(this)

//////////////////////////////////////////////////////////////////////

object TaskKWorkUtil {
    @JvmStatic
    fun boolean2result(boolean: Boolean): Result =
        if (boolean)
            Result.success()
        else
            Result.failure()
}