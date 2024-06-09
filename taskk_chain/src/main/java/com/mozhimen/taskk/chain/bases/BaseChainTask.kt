package com.mozhimen.taskk.chain.bases

import androidx.core.os.TraceCompat
import com.mozhimen.taskk.chain.temps.ChainTaskGroup
import com.mozhimen.taskk.chain.annors.AChainState
import com.mozhimen.taskk.chain.commons.IChainListener
import com.mozhimen.taskk.chain.commons.IChainTask
import com.mozhimen.taskk.chain.helpers.ChainRuntime
import com.mozhimen.taskk.chain.helpers.ChainImpl
import com.mozhimen.taskk.chain.helpers.ChainTaskComparator
import com.mozhimen.basick.utilk.java.lang.UtilKThread
import java.util.*
import kotlin.collections.ArrayList

/**
 * @ClassName TaskK
 * @Description 启动阶段需要初始化的任务，在taskk中对应着一个Task
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/3/29 15:15
 * @Version 1.0
 */
abstract class BaseChainTask @JvmOverloads constructor(
    /**任务名称**/
    val id: String,
    /**是否是异步任务**/
    val isAsyncTask: Boolean = false,
    /**延迟执行的时间**/
    val delayMills: Long = 0,
    /**任务的优先级**/
    var priority: Int = 0
) : Runnable, Comparable<BaseChainTask>, IChainTask {

    //任务执行时间
    var executeTime: Long = 0
        protected set

    //任务的状态
    var state: Int = AChainState.IDLE
        protected set

    val dependTasks: MutableList<BaseChainTask> = ArrayList()//当前task依赖了那些前置任务，只有当dependTasks集合中的所有任务执行完，当前才可以被执行
    val behindTasks: MutableList<BaseChainTask> = ArrayList()//当前task被那些后置任务依赖，只有当当前这个task执行完，behindTasks集合中的后置任务才可以执行
    val dependTasksName: MutableList<String> = ArrayList()//用于运行时log统计输出，输出当前task依赖了那些前置任务， 这些前置任务的名称我们将它存储在这里

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private val _chainkListeners: MutableList<IChainListener> = ArrayList()//任务运行状态监听器集
    private val _chainkTaskComparator by lazy { ChainTaskComparator() }
    private var _chainkImpl: ChainImpl? = ChainImpl()//用于输出task运行时的日志

    //////////////////////////////////////////////////////////////////////////////////////////////////

    override fun compareTo(other: BaseChainTask): Int {
        return ChainTaskComparator().compare(this, other)
    }

    override fun run() {
        //改变任务的状态--onStart onRunning onFinished -- 通知后置任务去开始执行
        TraceCompat.beginSection(id)
        toRunning()
        run(id)//真正的执行初始化任务的代码的方法
        toFinish()
        //通知它的后置任务去执行
        notifyBehindTasks()
        recycle()
        TraceCompat.endSection()
    }

    override fun start() {
        if (state != AChainState.IDLE) {
            throw RuntimeException("cannot run task $id again")
        }
        toStart()
        executeTime = System.currentTimeMillis()
        //执行当前任务
        ChainRuntime.executeTask(this)
    }

    override fun addTaskKListener(listener: IChainListener) {
        if (!_chainkListeners.contains(listener)) {
            _chainkListeners.add(listener)
        }
    }

    override fun dependOn(node: BaseChainTask) {
        var taskK = node
        if (taskK != this) {
            if (node is ChainTaskGroup) {
                taskK = node.endTask
                dependTasks.add(taskK)
                dependTasksName.add(taskK.id)
                //当前task依赖了dependTask， 那么我们还需要吧dependTask-里面的behindTask添加进去当前的task
                if (!taskK.behindTasks.contains(this)) {
                    taskK.behindTasks.add(this)
                }
            }
        }
    }

    override fun removeDependence(dependTask: BaseChainTask) {
        var taskK = dependTask
        if (dependTask != this) {
            if (dependTask is ChainTaskGroup) {
                taskK = dependTask.endTask
            }
            dependTasks.remove(taskK)
            dependTasksName.remove(taskK.id)
            //把当前task从dependTask的 后置依赖任务集合behindTasks中移除
            //达到接触两个任务依赖关系的目的
            if (taskK.behindTasks.contains(this)) {
                taskK.behindTasks.remove(this)
            }
        }
    }

    override fun behind(behindTask: BaseChainTask) {
        var taskK = behindTask
        if (behindTask != this) {
            if (behindTask is ChainTaskGroup) {
                taskK = behindTask.startTask
            }
            //这个是把behindTask添加到当前task的后面
            behindTasks.add(taskK)
            //把当前task添加到behindTask 的前面
            behindTask.dependOn(this)
        }
    }

    override fun removeBehind(behindTask: BaseChainTask) {
        var taskK = behindTask
        if (behindTask != this) {
            if (behindTask is ChainTaskGroup) {
                taskK = behindTask.startTask
            }
            behindTasks.remove(taskK)
            behindTask.removeDependence(this)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private fun recycle() {
        dependTasks.clear()
        behindTasks.clear()
        _chainkListeners.clear()
        _chainkImpl = null
    }

    private fun notifyBehindTasks() {
        //通知后置任务去尝试执行
        if (behindTasks.isNotEmpty()) {
            if (behindTasks.size > 1) {
                Collections.sort(behindTasks, _chainkTaskComparator)
            }

            //遍历behindTask后置任务，通知他们，告诉他们你的一个前置依赖任务已经执行完成了
            for (behindTask in behindTasks) {
                // A behindTask ->(B,C) A执行完成之后， B,C才可以执行。
                behindTask.dependTaskFinished(this)
            }

        }
    }

    private fun dependTaskFinished(node: BaseChainTask) {
        // A behindTasks ->(B,C) A执行完成之后， B,C7可以执行。
        // task= B,C , dependTask=A
        if (dependTasks.isEmpty()) {
            return
        }
        //把A从B, C的前置依赖任务集合中移除
        dependTasks.remove(node)
        //B, C的所有前置任务是否都执行完了
        if (dependTasks.isEmpty()) {
            start()
        }
    }

    private fun toStart() {
        state = AChainState.START
        ChainRuntime.setTaskStateInfo(this)
        for (listener in _chainkListeners) {
            listener.onStart(this)
        }
        _chainkImpl?.onStart(this)
    }

    private fun toFinish() {
        state = AChainState.FINISHED
        ChainRuntime.setTaskStateInfo(this)
        ChainRuntime.removeBlockTask(this.id)
        for (listener in _chainkListeners) {
            listener.onFinished(this)
        }
        _chainkImpl?.onFinished(this)
    }

    private fun toRunning() {
        state = AChainState.RUNNING
        ChainRuntime.setTaskStateInfo(this)
        ChainRuntime.setThreadName(this, UtilKThread.getName_ofCur())
        for (listener in _chainkListeners) {
            listener.onRunning(this)
        }
        _chainkImpl?.onRunning(this)
    }
}

