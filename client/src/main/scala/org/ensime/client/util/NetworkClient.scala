package org.ensime.client.util

import java.net.Socket
import scala.io.BufferedSource
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.io.PrintStream
import java.io.IOException
import java.io.DataInputStream
import java.io.DataOutputStream
import scala.concurrent._
import java.util.concurrent.ConcurrentHashMap
import com.typesafe.scalalogging.LazyLogging
import org.ensime.api.EnsimeServerMessage

trait NetworkClientBase {

  /*
   * Networking stuff
   */
  protected val clientSocket: Socket

  protected val out: DataOutputStream
  protected val in: DataInputStream

  def start(): Unit
  def close(): Unit

  def sendMessage(p: Promise[EnsimeServerMessage], msgId: Int, msg: String): Unit
  def sendMessage(msg: String): Unit

}

trait MappingPromiseToId {
  /**
   * Map to keep track of the relation message id to promise to which to return the
   * incoming result.
   */
  protected val mapMessageIdToPromise = new java.util.concurrent.ConcurrentHashMap[Int, Promise[EnsimeServerMessage]]()

  /**
   * Add entry id -> promise to the mapMessageIdToPromise.
   */
  protected def IdToPromiseAdd(msgId: Int, p: Promise[EnsimeServerMessage]): Unit = {
    mapMessageIdToPromise.put(msgId, p)
  }

  /**
   * Remove id entry from
   */
  protected def IdToPromiseDrop(msgId: Int): Unit = {
    mapMessageIdToPromise.remove(msgId)
  }

  /**
   * Get Promise and remove from map based on Id
   * Note: Does not check whether entry exists!
   */
  protected def getPromiseAndRemoveEntry(msgId: Int): Promise[EnsimeServerMessage] = {
    val p = mapMessageIdToPromise.get(msgId)
    IdToPromiseDrop(msgId)
    p
  }

  protected def doesPromiseEntryExist(msgId: Int): Boolean = {
    return mapMessageIdToPromise.containsKey(msgId)
  }
}

class NetworkClientContext(
    val host: String,
    val port: Int,
    val verbose: Boolean
) {
}

abstract class NetworkClientMain(implicit context: NetworkClientContext)
    extends NetworkClientBase with MappingPromiseToId with LazyLogging {

  val clientSocket = new Socket(context.host, context.port)

  val out = new DataOutputStream(clientSocket.getOutputStream());
  val in = new DataInputStream(clientSocket.getInputStream());

  /**
   * Starts the socket listening thread.
   */
  def start(): Unit = {
    startMonitoringSocket(clientSocket.getInputStream)
  }

  /**
   * Close the socket listening thread.
   */
  def close(): Unit = {
    hasShutdownFlagInputStream.set(true)
  }

  /**
   * Starts the thread listening on the socket.
   */
  private val hasShutdownFlagInputStream = new AtomicBoolean(false)
  private def startMonitoringSocket(inputStream: InputStream): Unit = {
    logger.info("Starting monitoring thread!")
    val t = new Thread(new Runnable() {
      def run(): Unit = {
        try {
          while (!hasShutdownFlagInputStream.get()) {
            try {
              if (in.available() != 0) {
                val msg = readIncomingMessageFromInputStream(in);
                //println("[NetworkClient Listener Thread] " + msg)
                handleReceivedMessage(msg)
              }
            } catch {
              case e: IOException =>
                if (!hasShutdownFlagInputStream.get())
                  logger.error("Server socket listener error: ", e)
            }
          }
        } finally {
          clientSocket.close()
        }
      }
    })
    t.start()
  }

  // ---------------------------------------------------------------------------
  // Incoming messages
  // ---------------------------------------------------------------------------

  /**
   * Reads incoming String message from DataInputStream.
   */
  private def readIncomingMessageFromInputStream(in: DataInputStream): String = {

    val headerSize = 6
    val headerData = Array.fill[Byte](headerSize)(0)
    // read header
    val headerDataInd = in.read(headerData, 0, headerSize)
    val msgLength = Integer.parseInt(new String(headerData, "UTF-8"), 16)
    // read message
    val msgData = Array.fill[Byte](msgLength)(0)
    val msgDataInd = in.read(msgData, 0, msgLength)
    val msg = new String(msgData, "UTF-8")
    msg
  }

  /**
   * Handles incoming messages.
   */
  protected def handleReceivedMessage(responseMessage: String): Unit

  /**
   * Returns the id of the incoming message.
   */
  //  def getMessageId(msg: String): Option[Int]

  // ---------------------------------------------------------------------------
  // Outgoing messages
  // ---------------------------------------------------------------------------

  /**
   * Sends message without entry in the id->promise map.
   */
  def sendMessage(msg: String): Unit = {
    val msgBytes = (addPaddingToOutgoingMessage(msg) + msg).getBytes("UTF-8")

    if (context.verbose)
      logger.info("Bytes Send: " + msgBytes.toString())

    out.write(msgBytes, 0, msgBytes.length);
    out.flush()
  }

  /**
   * Adds 6 bytes of message length length information in 0x to the front of the message.
   */
  private def addPaddingToOutgoingMessage(msg: String): String = {
    "%06x".format(msg.length)
  }

  /**
   * Send message and create entry in promise to message id map.
   */
  def sendMessage(p: Promise[EnsimeServerMessage], msgId: Int, msg: String): Unit = {
    if (context.verbose)
      logger.info("Sending message: " + msg)

    IdToPromiseAdd(msgId, p)
    sendMessage(msg)
  }

}
