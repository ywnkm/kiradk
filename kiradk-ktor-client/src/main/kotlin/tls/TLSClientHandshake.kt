package kiradk.client.tls

import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kiradk.client.utils.LazyTODO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.*
import kotlin.coroutines.CoroutineContext

// io.ktor.network.tls.TLSClientHandshake
internal class TLSClientHandshake(
    socketReadChannel: ByteReadChannel,
    socketWriteChannel: ByteWriteChannel,
    val clientHelloID: ClientHelloID,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    private val helloSpec = clientHelloID.specFactory()

    private val digest = Digest()
    private val clientSeed: ByteArray = helloSpec.clientRandFactory(helloSpec.secureRandom)

    @Volatile
    private lateinit var serverHello: TLSServerHello

    private val cipher: TLSCipher by LazyTODO

    @OptIn(ExperimentalCoroutinesApi::class)
    val input: ReceiveChannel<TLSRecord> = produce {
        var useCipher = false
        try {
            loop@ while (true) {
                val rawRecord = socketReadChannel.readTLSRecord()
                val record = if (useCipher) cipher.decrypt(rawRecord) else rawRecord
                val packet = record.packet

                when (record.type) {
                    TLSRecordType.Alert -> {
                        val level = TLSAlertLevel.byCode(packet.readByte().toInt())
                        val code = TLSAlertType.byCode(packet.readByte().toInt())

                        if (code == TLSAlertType.CloseNotify) return@produce
                        val cause = TLSException("Received alert during handshake. Level: $level, code: $code")

                        channel.close(cause)
                        return@produce
                    }

                    TLSRecordType.ChangeCipherSpec -> {
                        check(!useCipher)
                        val flag = packet.readByte()
                        if (flag != 1.toByte()) {
                            throw TLSException("Expected flag: 1, received $flag in ChangeCipherSpec")
                        }
                        useCipher = true
                        continue@loop
                    }
                    TLSRecordType.Handshake -> {}
                    TLSRecordType.ApplicationData -> {}
                }

                channel.send(TLSRecord(record.type, packet = packet))
            }
        } catch (cause: ClosedReceiveChannelException) {
            channel.close()
        } catch (cause: Throwable) {
            channel.close()
        } finally {
            output.close()
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    val output: SendChannel<TLSRecord> = actor {
        var useCipher = false

        try {
            for (rawRecord in channel) {
                try {
                    val record = if (useCipher) cipher.encrypt(rawRecord) else rawRecord
                    if (rawRecord.type == TLSRecordType.ChangeCipherSpec) useCipher = true

                    socketWriteChannel.writeTlsRecord(record)
                } catch (cause: Throwable) {
                    channel.close(cause)
                }
            }
        } finally {
            socketWriteChannel.writeTlsRecord(
                TLSRecord(
                    TLSRecordType.Alert,
                    packet = buildPacket {
                        writeByte(TLSAlertLevel.WARNING.code.toByte())
                        writeByte(TLSAlertType.CloseNotify.code.toByte())
                    }
                )
            )

            socketWriteChannel.close()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val handshakes: ReceiveChannel<TLSHandShake> = produce {
        while (true) {
            val record = input.receive()
            if (record.type != TLSRecordType.Handshake) {
                record.packet.release()
                throw TLSException("TLS handshake expected, got ${record.type}")
            }

            val packet = record.packet

            while (packet.isNotEmpty) {
                val handshake = packet.readTLSHandshake()
                if (handshake.type == TLSHandshakeType.HelloRequest) continue
                if (handshake.type != TLSHandshakeType.Finished) {
                    digest += handshake
                }

                channel.send(handshake)

                if (handshake.type == TLSHandshakeType.Finished) {
                    packet.release()
                    return@produce
                }
            }
        }
    }

    suspend fun negotiate() {
        digest.use {
            sendClientHello()
            serverHello = receiveServerHello()
            verifyHello(serverHello)

        }
    }

    private suspend fun sendClientHello() {
        sendHandshakeRecord(TLSHandshakeType.ClientHello) {
            writeTLSClientHello(
                TLSVersion.TLS12,
                helloSpec.cipherSuites,
                helloSpec.extensions,
                0,
                clientSeed,
                ByteArray(32)
            )
        }
    }

    private suspend fun sendHandshakeRecord(handshakeType: TLSHandshakeType, block: BytePacketBuilder.() -> Unit) {
        val handshakeBody = buildPacket(block)

        val recordBody = buildPacket {
            writeTLSHandshakeType(handshakeType, handshakeBody.remaining.toInt())
            writePacket(handshakeBody)
        }

        digest.update(recordBody)
        val element = TLSRecord(TLSRecordType.Handshake, packet = recordBody)
        try {
            output.send(element)
        } catch (cause: Throwable) {
            element.packet.release()
            throw cause
        }
    }

    private suspend fun receiveServerHello(): TLSServerHello {
        val handshake = handshakes.receive()

        check(handshake.type == TLSHandshakeType.ServerHello) {
            ("Expected TLS handshake ServerHello but got ${handshake.type}")
        }
        return handshake.packet.readTLSServerHello()
    }

    private suspend fun verifyHello(serverHello: TLSServerHello) {
        val suit = serverHello.cipherSuite
        // TODO check suit

    }
}