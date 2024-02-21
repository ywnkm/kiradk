package kiradk.client.tls

import io.ktor.network.tls.*
import java.security.SecureRandom

public class ClientHelloSpec(
    public val cipherSuites: List<CipherSuite>,
    public val compressionMethods: ByteArray,
    public val extensions: List<TLSExtensionRecord>,
    public val secureRandom: SecureRandom = SecureRandom(),
    public val clientRandFactory: ((SecureRandom) -> ByteArray) = { it.generateClientSeed() }
)
