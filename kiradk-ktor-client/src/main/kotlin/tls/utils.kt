package kiradk.client.tls

import java.security.SecureRandom

internal fun SecureRandom.generateClientSeed(): ByteArray {
    val seed = ByteArray(32)
    nextBytes(seed)
    return seed.also {
        val unixTime = (System.currentTimeMillis() / 1000L)
        it[0] = (unixTime shr 24).toByte()
        it[1] = (unixTime shr 16).toByte()
        it[2] = (unixTime shr 8).toByte()
        it[3] = (unixTime shr 0).toByte()
    }
}
