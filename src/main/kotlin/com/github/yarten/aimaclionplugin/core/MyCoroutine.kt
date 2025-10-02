package com.github.yarten.aimaclionplugin.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * 支持超时处理、错误处理的协程
 */
abstract class MyCoroutine(val timeoutMs: Long, val tryCount: Int = 10, val tryDurationMs: Long = 1000) {
    /**
     * 执行协程的内容
     */
    abstract suspend fun run()

    /**
     * 失败中的处理
     */
    abstract suspend fun failOnce(tryNumber: Int, e: Exception)

    /**
     * 最终失败时的处理
     */
    open suspend fun finalFail() =
        failOnce(tryCount, RuntimeException("try count exceed limits $tryCount"))

    fun launch() {
        val co = this
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..tryCount) {
                try {
                    withTimeout(timeoutMs) {
                        co.run()
                    }
                    return@launch
                }
                catch (e: Exception) {
                    if (i == tryCount)
                        break
                    co.failOnce(i, e)
                    delay(tryDurationMs)
                }
            }

            co.finalFail()
        }
    }
}
