package kiradk.client.tls

import java.io.IOException

public class TLSException(message: String, cause: Throwable? = null) : IOException(message, cause)
