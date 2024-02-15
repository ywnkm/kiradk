package kiradk.client.tls

import io.ktor.network.sockets.*

internal class TLSSocket(
    private val socket: Socket
) : Socket by socket {


    override fun dispose() {
        socket.dispose()
    }
}
