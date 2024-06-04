package com.vhxnif.pet.util

import reactor.core.publisher.Flux

/**
 *
 * @author xiaochen.zhang
 * @since 2024-05-30
 */

fun Flux<String>.print() {
    this.doOnNext { print(it) }.blockLast()
}

fun Flux<String>.println() {
    this.doOnNext { println(it) }.blockLast()
}

fun Sequence<Flux<String>>.print() {
    this.reduce { acc, flux -> acc.concatWith(flux.doFirst { println() })  }.doOnNext { print(it) }.blockLast()
}