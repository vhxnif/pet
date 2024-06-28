package com.vhxnif.pet.transformer

import org.springframework.ai.transformer.splitter.TextSplitter

/**
 *
 * @author vhxnif
 * @since 2024-06-03
 */

class TranslateTextSplitter : TextSplitter() {
    override fun splitText(text: String?): MutableList<String> {
        // line split
        return if (text.isNullOrBlank()) {
            mutableListOf()
        } else {
           text.split("\n").filter { it.isNotBlank() }.toMutableList()
        }
    }
}