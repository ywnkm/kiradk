package kiradk.client

import io.ktor.client.engine.*
import io.ktor.http.*

public class KiraDKClientConfig : HttpClientEngineConfig() {

    public var proxyUrl: Url? = null

    public var mode: KiraClientMode = KiraClientMode.Default

}

public enum class KiraClientMode {
    Default,
    SingleConnection
}
