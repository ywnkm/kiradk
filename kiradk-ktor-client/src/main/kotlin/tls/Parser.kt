package kiradk.client.tls

import io.ktor.network.tls.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlin.experimental.and

private const val MAX_TLS_FRAME_SIZE = 0x4800

internal suspend fun ByteReadChannel.readTLSRecord(): TLSRecord {
    val type = TLSRecordType.byCode(readByte().toInt() and 0xff)
    val version = readTLSVersion()

    val length = readShortCompatible() and 0xffff
    if (length > MAX_TLS_FRAME_SIZE) throw Exception("Illegal TLS frame size: $length")

    val packet = readPacket(length)
    return TLSRecord(type, version, packet)
}

internal fun ByteReadPacket.readTLSHandshake(): TLSHandShake {
    return TLSHandShake().apply {
        val typeAndVersion = readInt()
        type = TLSHandshakeType.byCode(typeAndVersion ushr 24)
        val length = typeAndVersion and 0xffffff
        packet = buildPacket {
            writeFully(readBytes(length))
        }
    }
}

internal fun ByteReadPacket.readTLSServerHello(): TLSServerHello {
    val version = readTLSVersion()

    val random = ByteArray(32)
    readFully(random)
    val sessionIdLength = readByte().toInt() and 0xff

    if (sessionIdLength > 32) {
        throw TLSException("sessionId length limit of 32 bytes exceeded: $sessionIdLength specified")
    }

    val sessionId = ByteArray(32)
    readFully(sessionId, 0, sessionIdLength)

    val suite = readShort()

    val compressionMethod = readByte().toShort() and 0xff

    if (compressionMethod.toInt() != 0) {
        throw TLSException(
            "Unsupported TLS compression method $compressionMethod (only null 0 compression method is supported)"
        )
    }

    if (remaining.toInt() == 0) return TLSServerHello(
        version,
        random,
        sessionId,
        suite,
        compressionMethod
    )

    // handle extensions
    val extensionSize = readShort().toInt() and 0xffff

    if (remaining.toInt() != extensionSize) {
        throw TLSException("Invalid extensions size: requested $extensionSize, available $remaining")
    }

    val extensions = mutableListOf<TLSExtensionRecord>()
    while (remaining > 0) {
        val type = readShort().toInt() and 0xffff
        val length = readShort().toInt() and 0xffff

        extensions += TLSExtensionRecord(
            TLSExtensionType.byCode(type),
            length,
            buildPacket { writeFully(readBytes(length)) }
        )
    }

    return TLSServerHello(version, random, sessionId, suite, compressionMethod, extensions)

}

internal suspend fun ByteReadChannel.readTLSVersion(): TLSVersion =
    TLSVersion.byCode(readShortCompatible() and 0xffff)

private fun ByteReadPacket.readTLSVersion(): TLSVersion =
    TLSVersion.byCode(readShort().toInt() and 0xffff)

internal fun ByteReadPacket.readTripleByteLength(): Int = (readByte().toInt() and 0xff shl 16) or
        (readShort().toInt() and 0xffff)

internal suspend fun ByteReadChannel.readShortCompatible(): Int {
    val first = readByte().toInt() and 0xff
    val second = readByte().toInt() and 0xff

    return (first shl 8) + second
}
