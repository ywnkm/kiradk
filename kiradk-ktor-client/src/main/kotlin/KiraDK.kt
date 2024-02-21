package kiradk.client

import io.ktor.client.engine.*

public object KiraDK : HttpClientEngineFactory<KiraDKClientConfig> {

    override fun create(block: KiraDKClientConfig.() -> Unit): HttpClientEngine {
        return newKiraDKEngine(KiraDKClientConfig().apply(block))
    }
}
