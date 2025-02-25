package com.stardust.util

import java.util.Arrays

inline fun <reified T> sortedArrayOf(vararg elements: T): Array<T> {
    val a = arrayOf(*elements)
    Arrays.sort(a)
    return a
}