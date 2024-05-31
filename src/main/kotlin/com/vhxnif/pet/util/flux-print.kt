package com.vhxnif.pet.util

import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-30
 */

fun <T> Flux<T>.print() {
    this.subscribe { print(it) }
    this.blockLast()
}