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

/**
 * Base for a network client to connect to an ensime-server.
 *
 */
trait NetworkClientBase {

  protected val clientSocket: Socket

  protected val out: DataOutputStream
  protected val in: DataInputStream

  /**
   * Use to setup up connection with ensime-server.
   */
  def start(): Unit

  /**
   * Use to close connection with ensime-server.
   */
  def close(): Unit

  /**
   * Use to send a message to the ensime-server with direct response.
   * @param p     Promise to return the server response (EnsimeServerMessage) to.
   * @param msgId Unique id assigned to the message.
   * @param msg   Message to be sent.
   */
  def sendMessage(p: Promise[EnsimeServerMessage], msgId: Int, msg: String): Unit

  /**
   * Use to send a message to the ensime-server without direct response.
   * @param msg   Message to be sent.
   */
  def sendMessage(msg: String): Unit

}

/**
 * Contains the mechanism to map message ids to their return Promise.
 */
trait MappingPromiseToId {

  /**
   * Map to keep track of the relation message id to promise to which to return the
   * incoming result.
   */
  protected val mapMessageIdToPromise = new java.util.concurrent.ConcurrentHashMap[Int, Promise[EnsimeServerMessage]]()

  /**
   * Add entry id -> promise to the mapMessageIdToPromise.
   * @param   msgId Message id.
   * @param   p     Promise to EnsimeServerMessage.
   */
  protected def IdToPromiseAdd(msgId: Int, p: Promise[EnsimeServerMessage]): Unit = {
    mapMessageIdToPromise.put(msgId, p)
  }

  /**
   * Remove id entry from map.
   * @param msgId Message id.
   */
  protected def IdToPromiseDrop(msgId: Int): Unit = {
    mapMessageIdToPromise.remove(msgId)
  }

  /**
   * Get Promise and remove from map based on Id
   * Note: Does not check whether entry exists!
   * @param msgId Message id.
   */
  protected def getPromiseAndRemoveEntry(msgId: Int): Promise[EnsimeServerMessage] = {
    val p = mapMessageIdToPromise.get(msgId)
    IdToPromiseDrop(msgId)
    p
  }

  /**
   * Checks of entry with certain id exists.
   * @param msgId Message id.
   * @return      Boolean indicator.
   */
  protected def doesPromiseEntryExist(msgId: Int): Boolean = {
    return mapMessageIdToPromise.containsKey(msgId)
  }

}

/**
 * NetworkClient helper. Contains server information.
 * @param host  Ip of the ensime-server.
 * @parm  port  Port of the ensime-server.
 */
class NetworkClientContext(
  val host: String,
  val port: Int,
  val verbose: Boolean
)

/**
 * Network client to connect to an ensime-server.
 * It is missing the protocol specific functionality.
 * @param context NetworkClientContext
 */
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
   * Closes the socket listening thread.
   */
  def close(): Unit = {
    logger.info("Closing network client ...")
    hasShutdownFlagInputStream.set(true)
    timeoutUnansweredRequests()
    logger.info("Done.")
  }

  /**
   * Starts the thread listening on the socket.
   *
   * Incoming messages are read by readIncomingMessageFromInputStream()
   * and then handles by handleReceivedMessage()
   *
   * @param inputStream InputStream of clientSocket.
   *
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
   *
   * The message is read in 2 steps.
   *
   *  1.  Read first 6 characters of message to determine message length (hex encoded).
   *  2.  Read actual message.
   *
   * @param in DataInputStream
   * @return   Message read from DataInputStream.
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
   *
   * Implement protocol specific handling here.
   *
   * @param responseMessage Incomding message.
   */
  protected def handleReceivedMessage(responseMessage: String): Unit

  // ---------------------------------------------------------------------------
  // Outgoing messages
  // ---------------------------------------------------------------------------

  /**
   * Sends message without entry in the id->promise map.
   *
   * Prepends the message with its length.
   *
   * @param msg Message to be send.
   */
  def sendMessage(msg: String): Unit = {
    val msgBytes = (addPaddingToOutgoingMessage(msg) + msg).getBytes("UTF-8")
    out.write(msgBytes, 0, msgBytes.length);
    out.flush()
  }

  /**
   * Adds 6 bytes of message length information in 0x to the front of the message.
   * @param   msg Message
   * @return      Message length + message.
   */
  private def addPaddingToOutgoingMessage(msg: String): String = {
    "%06x".format(msg.length)
  }

  /**
   * Send message and create entry in promise to message id map.
   * @param p     Promise to return server response to.
   * @param msgId Message id.
   * @param msg   Message to be sent.
   */
  def sendMessage(p: Promise[EnsimeServerMessage], msgId: Int, msg: String): Unit = {
    if (context.verbose)
      logger.info("Sending message: " + msg.take(100))

    IdToPromiseAdd(msgId, p)
    sendMessage(msg)
  }

  // ---------------------------------------------------------------------------
  // Debugging tools
  // ---------------------------------------------------------------------------

  /**
   * Returns the number of unanswered requests.
   * @return  Number of unanswered requests.
   */
  def getNumberOfOpenRequests(): Int = { mapMessageIdToPromise.size }

  /**
   * Sends time out to all unanswered response promises.
   */
  protected def timeoutUnansweredRequests(): Unit = {
    logger.info("[Unanswered requests] There are " + getNumberOfOpenRequests() + " open requests.")

    if (getNumberOfOpenRequests() > 0) {
      logger.info("[Unanswered requests] There are ")
      logger.info("[Unanswered requests] Sending timeouts ... ")
      val values = mapMessageIdToPromise.values()

      val valueIter = values.iterator()
      while (valueIter.hasNext()) {
        val p = valueIter.next()
        p.failure(new Exception("TIME OUT NO ANSWER"))
      }
    }
  }

}
