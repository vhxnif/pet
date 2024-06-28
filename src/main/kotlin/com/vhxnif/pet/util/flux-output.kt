package com.vhxnif.pet.util

import reactor.core.publisher.Flux
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * @author vhxnif
 * @since 2024-05-30
 */

fun Flux<String>.print() {
    this.doOnNext { print(it) }.blockLast()
}

fun Flux<String>.println() {
    this.doOnNext { println(it) }.blockLast()
}

fun Sequence<Flux<String>>.print() {
    this.reduce { acc, flux -> acc.concatWith(flux.doFirst { println() }) }.doOnNext { print(it) }.blockLast()
}

fun Sequence<Flux<String>>.toFile(path: String) {
    val pb = ProcessBar("processing", this.count())
    Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8).use { writer ->
        this.reduce { acc, flux ->
            acc.concatWith(
                flux.doFirst { writer.newLine() }
                    .doOnComplete { pb.partCompleted() }
            )
        }.doOnNext {
            writer.write(it)
        }.doFinally {
            pb.partCompleted()
        }.blockLast()
    }
}

