package kiradk.client.tls

import kiradk.client.utils.LazyTODO

public class ClientHelloID(
    public val name: String,
    public val version: String,
    public val specFactory: () -> ClientHelloSpec
) {

    public companion object {

        public val Default: ClientHelloID by LazyTODO
    }
}