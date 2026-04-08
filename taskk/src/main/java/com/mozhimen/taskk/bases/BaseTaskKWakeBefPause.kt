package com.mozhimen.taskk.bases

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.bases.BaseWakeBefPauseLifecycleObserver
import com.mozhimen.kotlin.lintk.optins.api.OApiInit_ByLazy
import com.mozhimen.kotlin.lintk.optins.api.OApiCall_BindLifecycle
import com.mozhimen.kotlin.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.kotlin.utilk.kotlin.UtilKLazyJVM.lazy_ofNone

@OApiCall_BindLifecycle
@OApiInit_ByLazy
abstract class BaseTaskKWakeBefPause : BaseWakeBefPauseLifecycleObserver() {
    protected val _context by lazy_ofNone { UtilKApplicationWrapper.instance.applicationContext }

    abstract fun isActive(): Boolean

    @CallSuper
    override fun onPause(owner: LifecycleOwner) {
        cancel()
        super.onDestroy(owner)
    }

    abstract fun cancel()
}