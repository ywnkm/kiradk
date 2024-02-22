package kiradk.client.http

import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kiradk.client.utils.MutableIntRange

public suspend fun parseResponse(input: ByteReadChannel): Response? {

    val range = MutableIntRange(0, 0)

    try {
        val line = input.readUTF8Line() ?: return null

        TODO()
    } catch (cause: Throwable) {
        // todo
        throw cause
    }
}

private fun parseVersion(text: String, range: MutableIntRange): HttpProtocolVersion {
    skipSpaces(text, range)
    check(range.start < range.end) { "Failed to parse version: $text" }

    TODO()
}

private fun parseStatus(text: String, range: MutableIntRange): HttpStatusCode {
    TODO()
}


private suspend fun parseHeaders(
    input: ByteReadChannel
): Headers = TODO()

private fun skipSpaces(text: CharSequence, range: MutableIntRange) {
    var idx = range.start
    val end = range.end

    if (idx >= end || !text[idx].isWhitespace()) return
    idx++

    while (idx < end) {
        if (!text[idx].isWhitespace()) break
        idx++
    }

    range.start = idx
}