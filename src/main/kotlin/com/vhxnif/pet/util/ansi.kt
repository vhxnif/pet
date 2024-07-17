package com.vhxnif.pet.util

import picocli.CommandLine.Help.Ansi

/**
 *
 * @author vhxnif
 * @since 2024-07-12
 */
enum class PreDefinedStyle(val style: String) {
    BOLD("bold"),
    FAINT("faint"),
    UNDERLINE("underline"),
    ITALIC("italic"),
    BLINK("blink"),
    REVERSE("reverse"),
    RESET("reset"),
}

enum class PreDefinedColor(val color: String) {
    BLACK("black"),
    RED("red"),
    GREEN("green"),
    YELLOW("yellow"),
    BLUE("blue"),
    MAGENTA("magenta"),
    CYAN("cyan"),
    WHITE("white"),
}

class AnsiDef {

    private val styles: MutableList<PreDefinedStyle> = mutableListOf()
    private var color: PreDefinedColor? = null
    private var string: String? = null

    infix fun String.use(c: PreDefinedColor) {
        color = c
        string = this
    }

    infix fun String.use(s: PreDefinedStyle): String {
        styles.add(s)
        string = this
        return this
    }

    fun build(): String {
        return when {
            string == null -> ""
            color == null && styles.isEmpty() -> string!!
            color == null  -> Ansi.AUTO.string("@|${styles.joinToString(",")} $string|@")
            styles.isEmpty() -> Ansi.AUTO.string("@|${color?.color} $string|@")
            else -> Ansi.AUTO.string("@|${styles.joinToString(",")},${color?.color} $string|@")
        }
    }
}

fun ansi(f: AnsiDef.() -> Unit): String {
    return AnsiDef().apply { f() }.build()
}
