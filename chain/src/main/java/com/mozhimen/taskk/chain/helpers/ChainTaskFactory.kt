package com.mozhimen.taskk.chain.helpers

import com.mozhimen.taskk.chain.bases.BaseChainTask
import com.mozhimen.taskk.chain.commons.IChainCreator

/**
 * @ClassName ChainKTaskFactory
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Version 1.0
 */
internal class ChainTaskFactory(private val _chainCreator: IChainCreator) {
    //利用iTaskCreator创建task 实例，并管理
    private val _chainKTasksCache: MutableMap<String, BaseChainTask> = HashMap()

    fun getTaskK(id: String): BaseChainTask {
        var task = _chainKTasksCache[id]
        if (task != null) return task
        task = _chainCreator.createChain(id)
        _chainKTasksCache[id] = task
        return task
    }
}