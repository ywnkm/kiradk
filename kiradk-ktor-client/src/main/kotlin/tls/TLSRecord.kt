package kiradk.client.tls

import io.ktor.network.tls.*
import io.ktor.network.tls.extensions.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

public class TLSRecord(
    public val type: TLSRecordType = TLSRecordType.Handshake,
    public val version: TLSVersion = TLSVersion.TLS12,
    public val packet: ByteReadPacket = ByteReadPacket.Empty,
)

public suspend fun ByteWriteChannel.writeTlsRecord(record: TLSRecord) {
    writeByte(record.type.code.toByte())
    writeByte((record.version.code shr 8).toByte())
    writeByte(record.version.code.toByte())
    writeShort(record.packet.remaining.toShort())
    writePacket(record.packet)
    flush()
}

public class TLSHandShake {
    public var type: TLSHandshakeType = TLSHandshakeType.HelloRequest
    public var packet: ByteReadPacket = ByteReadPacket.Empty
}

public class TLSExtensionRecord(
    public val type: TLSExtensionType,
    public val length: Int,
    public val packet: ByteReadPacket,
)

internal class TLSServerHello(
    val version: TLSVersion,
    val serverSeed: ByteArray,
    val sessionId: ByteArray,
    suite: Short,
    val compressionMethod: Short,
    val extensions: List<TLSExtensionRecord> = emptyList()
) {
    val cipherSuite: CipherSuite = TODO()

    val hashAndSignAlgorithms: List<HashAndSign>

    init {
        val algorithms = mutableListOf<HashAndSign>()
        extensions.forEach {
            when (it.type) {
                TLSExtensionType.SignatureAlgorithms -> {
                    // algorithms += it.packet.parseSignatureAlgorithms()
                }
                else -> {
                }
            }
        }

        hashAndSignAlgorithms = algorithms
    }
}
