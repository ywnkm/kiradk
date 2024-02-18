package kiradk.client.tls

public class ClientHelloID(
    public val name: String,
    public val version: String,
    public val specFactory: () -> ClientHelloSpec
) {

    public companion object {

    }
}