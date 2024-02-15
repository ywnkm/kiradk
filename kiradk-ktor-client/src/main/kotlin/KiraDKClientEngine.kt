package kiradk.client

import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.util.*

public class KiraDKClientEngine(
    override val config: KiraDKClientConfig
) : HttpClientEngineBase("") {

    @OptIn(InternalAPI::class)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        TODO("Not yet implemented")
    }
}
