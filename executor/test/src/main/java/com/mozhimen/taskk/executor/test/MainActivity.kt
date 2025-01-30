package com.mozhimen.taskk.executor.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.kotlin.utilk.android.content.startContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goTaskKExecutor(view: View) {
        startContext<TaskKExecutorActivity>()
    }
}