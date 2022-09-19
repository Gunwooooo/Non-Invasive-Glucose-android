package com.hanait.noninvasiveglucoseapplication.util

import kotlinx.coroutines.*

@DelicateCoroutinesApi
class GlobalCoroutineScope {
    private val externalScope: CoroutineScope = GlobalScope
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    interface OnLaunch{
        fun launch()
    }

    fun launch(onLaunch: OnLaunch) {
        externalScope.launch(defaultDispatcher) {
            onLaunch.launch()
        }
    }
}