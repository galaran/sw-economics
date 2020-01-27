package me.galaran.swe.util

import java.util.concurrent.TimeUnit

inline fun <T> runWithStopwatch(block: () -> T): T {
    val begin = System.nanoTime()
    val result = block()
    val end = System.nanoTime()

    println("$result @ ${TimeUnit.NANOSECONDS.toMillis(end - begin)}ms")

    return result
}
