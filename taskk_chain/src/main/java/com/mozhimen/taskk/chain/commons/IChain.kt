package com.mozhimen.taskk.chain.commons

import com.mozhimen.taskk.chain.TaskKChain
import com.mozhimen.taskk.chain.bases.BaseChainTask

/**
 * @ClassName IChainK
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
interface IChain {
    fun addBlockTask(taskId: String): IChain
    fun addBlockTasks(vararg taskIds: String): TaskKChain

    /**
     * project任务组，也有可能是独立的一个task
     * @param task ChainKTask
     */
    fun start(task: BaseChainTask)
}