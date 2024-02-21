package kiradk.client.tls

import io.ktor.network.tls.*
import io.ktor.network.util.*
import io.ktor.utils.io.core.*
import java.security.MessageDigest

internal fun Digest(): Digest = Digest(BytePacketBuilder())

@JvmInline
internal value class Digest(val state: BytePacketBuilder) : Closeable {

    fun update(packet: ByteReadPacket) = synchronized(state) {
        if (packet.isEmpty) return
        state.writePacket(packet.copy())
    }

    fun doHash(hashName: String): ByteArray = synchronized(state) {
        state.preview { handshakes: ByteReadPacket ->
            val digest = MessageDigest.getInstance(hashName)!!

            val buffer = DefaultByteBufferPool.borrow()
            try {
                while (!handshakes.isEmpty) {
                    val rc = handshakes.readAvailable(buffer)
                    if (rc == -1) break
                    buffer.flip()
                    digest.update(buffer)
                    buffer.clear()
                }

                return@preview digest.digest()
            } finally {
                DefaultByteBufferPool.recycle(buffer)
            }
        }
    }

    override fun close() {
        state.release()
    }
}

internal operator fun Digest.plusAssign(record: TLSHandShake) {
    check(record.type != TLSHandshakeType.HelloRequest)

    update(
        buildPacket {
            writeTLSHandshakeType(record.type, record.packet.remaining.toInt())
            if (record.packet.remaining > 0) writePacket(record.packet.copy())
        }
    )
}
