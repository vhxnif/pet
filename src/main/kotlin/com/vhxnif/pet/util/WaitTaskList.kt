package com.vhxnif.pet.util

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author chen
 * @since 2024-06-21
 */
object WaitTaskList {

    private val ctx = AtomicInteger()

    fun startTask() = ctx.andIncrement

    fun downTask() = ctx.andDecrement

    fun allTaskDown() = ctx.get() == 0

}
