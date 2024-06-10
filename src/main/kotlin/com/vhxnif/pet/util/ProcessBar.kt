package com.vhxnif.pet.util

import reactor.core.publisher.Flux

/**
 *
 * @author chen
 * @since 2024-06-10
 */
class ProcessBar(
    private val message: String,
    private val waitProcessLength: Long,
) {
    val incomplete = '░'
    val complete = '█'
    val strBuilder = StringBuilder()
    var completeLength = 0

    init {
        println("$message: ")
        Flux.generate { it.next(incomplete) }.take(waitProcessLength).doOnNext { strBuilder.append(it) }.blockLast()
        print("$strBuilder $completeLength/$waitProcessLength")
    }

    fun partComplete() {
        strBuilder.replace(completeLength,completeLength +1,complete.toString())
        completeLength++
        val pb = "\r $strBuilder $completeLength/$waitProcessLength"
        print(pb)
    }

}