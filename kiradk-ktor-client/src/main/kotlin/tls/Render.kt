package kiradk.client.tls

import io.ktor.network.tls.*
import io.ktor.utils.io.core.*


internal fun BytePacketBuilder.writeTLSHandshakeType(type: TLSHandshakeType, length: Int) {
    if (length > 0xffffff) throw TLSException("TLS handshake size limit exceeded: $length")
    val v = (type.code shl 24) or length
    writeInt(v)
}

internal fun BytePacketBuilder.writeTLSClientHello(
    version: TLSVersion,
    suites: List<CipherSuite>,
    extensions: List<TLSExtensionRecord>,
    compression: Int,
    random: ByteArray,
    sessionId: ByteArray,
) {
    writeShort(version.code.toShort())
    writeFully(random)

    val sessionIdLength = sessionId.size
    if (sessionIdLength < 0 || sessionIdLength > 0xff || sessionIdLength > sessionId.size) {
        throw TLSException("Illegal sessionIdLength")
    }

    writeByte(sessionIdLength.toByte())
    writeFully(sessionId, 0, sessionIdLength)

    writeShort((suites.size * 2).toShort())
    for (suite in suites) {
        writeShort(suite.code)
    }

    // compression is always null, TODO
    writeByte(1)
    writeByte(0)

    writeShort(extensions.sumOf { it.length }.toShort())

    for (extension in extensions) {
        writePacket(extension.packet)
    }
}
