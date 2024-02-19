package kiradk.client.tls

import io.ktor.network.tls.*

public class ClientHelloSpec(
    public val cipherSuites: Array<CipherSuite>,
    public val compressionMethods: ByteArray,
    public val extensions: Array<TLSExtensionRecord>,
)
