package kiradk.client

import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal fun newKiraDKEngine(config: KiraDKClientConfig): KiraDKClientEngine {
    return when(config.mode) {
        KiraClientMode.Default -> KiraDKClientEngine(config)
        KiraClientMode.SingleConnection -> SingleConnectionEngine(config)
    }
}

public open class KiraDKClientEngine(
    override val config: KiraDKClientConfig
) : HttpClientEngineBase("KiraDK") {


    @OptIn(InternalAPI::class)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        // TODO: WS, ...
        return executeHttp(data)
    }

    private suspend fun executeHttp(data: HttpRequestData): HttpResponseData {
        val socket = createSocket(data)
        TODO()
    }

    public open suspend fun createSocket(request: HttpRequestData): Socket {
        return when (request.url.protocol.name) {
            "http" -> aSocket(SelectorManager(this.dispatcher)).tcp().connect(request.url.host, request.url.port)
            "https" -> TODO()
            else -> throw UnsupportedOperationException("Unsupported protocol ${request.url.protocol}")
        }
    }

}

internal class SingleConnectionEngine(
    config: KiraDKClientConfig
) : KiraDKClientEngine(config) {

    @Volatile
    private var socket: Socket? = null

    private val lock = Mutex()

    override suspend fun createSocket(request: HttpRequestData): Socket {
        socket?.let { return it }
        return lock.withLock {
            socket?.let { return it }
            val new = super.createSocket(request)
            this.socket = new
            new
        }
    }
}

