package com.vhxnif.pet.util

/**
 *
 * @author chen
 * @since 2024-06-06
 */

fun <R> Boolean.matchRun(f: () -> R): R? {
    return if (this) {
        f()
    } else {
        null
    }
}
