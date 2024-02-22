package kiradk.client

import io.ktor.client.engine.*
import io.ktor.http.*
import kiradk.client.utils.defaultSorter

public class KiraDKClientConfig : HttpClientEngineConfig() {

    public var proxyUrl: Url? = null

    public var mode: KiraClientMode = KiraClientMode.Default

    public var headerSorter: (List<String>) -> List<String> = ::defaultSorter

}

public enum class KiraClientMode {
    Default,
    SingleConnection
}
