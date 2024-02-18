package kiradk.client.tls

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

@Suppress("Deprecation")
internal class TLSSocket(
    private val socket: Socket
) : Socket by socket {

    override fun attachForReading(channel: ByteChannel): WriterJob {
        TODO("Not yet implemented")
    }

    override fun attachForWriting(channel: ByteChannel): ReaderJob {
        TODO("Not yet implemented")
    }
    override fun dispose() {
        socket.dispose()
    }
}
