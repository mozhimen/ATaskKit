package com.mozhimen.taskk.executor.test

import android.os.Bundle
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import androidx.lifecycle.lifecycleScope
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.taskk.executor.TaskKExecutor
import com.mozhimen.kotlin.utilk.android.util.e
import com.mozhimen.kotlin.utilk.java.lang.UtilKThread
import com.mozhimen.taskk.executor.test.databinding.ActivityTaskkExecutorBinding
import kotlinx.coroutines.launch

/**
 * @ClassName ExecutorKActivity
 * @Description TODO
 * @Author mozhimen
 * @Date 2021/9/14 20:05
 * @Version 1.0
 */
class TaskKExecutorActivity : BaseActivityVDB<ActivityTaskkExecutorBinding>() {

    private var _isPaused = false

    override fun initView(savedInstanceState: Bundle?) {
        vdb.taskkExecutorBtnOrder.setOnClickListener {
            for (priority in 0..10) {
                TaskKExecutor.execute(TAG, priority) {
                    try {
                        Thread.sleep((1000 - priority * 100).toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        e.message?.e(TAG)
                    }
                }
            }
        }

        vdb.taskkExecutorBtnAllTask.setOnClickListener {
            if (_isPaused) {
                TaskKExecutor.resume()
            } else {
                TaskKExecutor.pause()
            }
            _isPaused = !_isPaused
        }

        vdb.taskkExecutorBtnAsync.setOnClickListener {
            TaskKExecutor.execute(TAG, runnable = object : TaskKExecutor.ExecutorKCallable<String>() {
                override fun onBackground(): String {
                    UtilKLogWrapper.e(TAG, "onBackground: 当前线程: ${UtilKThread.get_ofCurrent()}")
                    return "我是异步任务的结果"
                }

                override fun onCompleted(t: String?) {
                    UtilKLogWrapper.e(TAG, "onCompleted: 当前线程是: ${UtilKThread.getName_ofCurrent()}")
                    UtilKLogWrapper.e(TAG, "onCompleted: 任务结果是: $t")
                }
            })
        }

        //这里演示ExectorK转化为协程调度器, 使用use是因为我们需要使用完毕主动关闭以免线程泄露
        TaskKExecutor.getTaskKExecutorCoroutineDispatcher().use {
            lifecycleScope.launch(it/*Dispatchers.IO*/) {
                /////////////////////
            }
        }
    }
}