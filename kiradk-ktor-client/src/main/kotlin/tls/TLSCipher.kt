package kiradk.client.tls

internal interface TLSCipher {

    fun encrypt(record: TLSRecord): TLSRecord

    fun decrypt(record: TLSRecord): TLSRecord
}
