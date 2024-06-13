package com.vhxnif.pet.util

import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-06-10
 */
class ProcessBar(
    private val message: String,
    private val waitTaskSize: Int
) {
    private val incomplete = '░'
    private val complete = '█'
    private val maxLength = 40
    var str = StringBuilder()
    var length: Int = 0
    var step: Int = 1
    var lengthComplete = 0
    var completedTaskSize = 0

    init {
        step = calStep()
        length = if (waitTaskSize <= maxLength) waitTaskSize else calCount(waitTaskSize, step)
        Flux.generate { it.next(incomplete) }.take(length.toLong()).doOnNext { str.append(it) }.blockLast()
        printProgress()
    }

    fun partCompleted() {
        completedTaskSize++
        if(completedTaskSize % step == 0 || completedTaskSize == waitTaskSize) {
            str[lengthComplete++] = complete
            printProgress()
        }
    }

    private fun printProgress() = print("\r $message $str $completedTaskSize/$waitTaskSize")
    private fun calStep(): Int = if (waitTaskSize <= maxLength) 1 else calCount(waitTaskSize, maxLength)
    private fun calCount(a: Int, b: Int) = (a + b -1) / b

}