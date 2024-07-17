package com.vhxnif.pet.util

import java.io.File

/**
 *
 * @author vhxnif
 * @since 2024-06-20
 */


fun osConfig () : String {
    val os = System.getProperty("os.name")
    return  if (os.startsWith("Windows")) {
        System.getenv("APPDATA")
    } else {
        System.getenv("HOME") + File.separator + ".config"
    }
}

fun petConfigDir() : String = osConfig() + File.separator + "pet"

fun String.escape() : String {
    return replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t")
}

fun String.println() {
    println(this)
}