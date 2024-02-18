package kiradk.client

import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*

public class KiraDKClientEngine(
    override val config: KiraDKClientConfig
) : HttpClientEngineBase("KiraDK") {

    @OptIn(InternalAPI::class)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {

        TODO("Not yet implemented")
    }

    public suspend fun createSocket(request: HttpRequestData): Socket {
        return when (request.url.protocol.name) {
            "http" -> aSocket(SelectorManager(this.dispatcher)).tcp().connect(request.url.host, request.url.port)
            else -> throw UnsupportedOperationException("Unsupported protocol ${request.url.protocol}")
        }
    }

}
