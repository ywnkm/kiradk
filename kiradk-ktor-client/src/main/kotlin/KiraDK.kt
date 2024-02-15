package kiradk.client

import io.ktor.client.engine.*

public object KiraDK : HttpClientEngineFactory<KiraDKClientConfig> {

    override fun create(block: KiraDKClientConfig.() -> Unit): HttpClientEngine {
        return KiraDKClientEngine(KiraDKClientConfig().apply(block))
    }
}
