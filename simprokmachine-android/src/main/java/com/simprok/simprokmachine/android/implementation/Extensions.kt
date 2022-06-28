package com.simprok.simprokmachine.android.implementation


internal fun <T> List<T>.dropFirst(): List<T> {
    return if (isEmpty()) {
        toList()
    } else {
        val copy = toMutableList()
        copy.removeFirst()
        copy.toList()
    }
}