package kiradk.client.http

import io.ktor.http.*

public abstract class HttpMessage internal constructor(
    public val headers: Headers,
    public val body: ByteArray,
)

public class Response internal constructor(
    public val version: HttpProtocolVersion,
    public val statusCode: HttpStatusCode,
    headers: Headers,
    body: ByteArray,
) : HttpMessage(headers, body)
