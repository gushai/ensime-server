package org.ensime.client

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

/**
 * Handle the communication of the ensime-client with the ensime-server.
 */
class NetworkClient(host: String, port: Int) {

  val serverHost = host
  val serverPort = port

  private val clientSocket = new Socket(host, port)

  val out = new DataOutputStream(clientSocket.getOutputStream());
  val in = new DataInputStream(clientSocket.getInputStream());

  /**
   * Map to keep track of the relation message id to promise to which to return the
   * incoming result.
   */
  private val mapMessageIdToPromise = new java.util.concurrent.ConcurrentHashMap[Int, Promise[String]]()

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
    log("Starting monitoring thread!")
    val t = new Thread(new Runnable() {
      def run(): Unit = {
        try {

          while (!hasShutdownFlagInputStream.get()) {
            try {
              if (in.available() != 0) {
                val msg = readIncomingMessageFromInputStream(in);
                handleReceivedMessage(msg)
              }
              //              log.info("Got connection, creating handler...")
              //              actorSystem.actorOf(Props(classOf[SocketHandler], socket, project, connectionCreator))
            } catch {
              case e: IOException =>
                if (!hasShutdownFlagInputStream.get())
                  // log.error("ENSIME Server socket listener error: ", e)
                  println("ENSIME Client socket listener error: ", e)
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
   * Reads incoming Swank message from DataInputStream.
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
  private def handleReceivedMessage(msg: String): Unit = {

    // Check whether its and error or an answer
    // TODO: Add identifier for errors!
    if (isErrorMessage(msg)) {

      val errorMessageId = getErrorMessageId(msg)
      log("[Error-Message]\t" + "ErrorId: " + errorMessageId + "\t" + msg)

      getMessageId(msg) match {
        case Some(id) =>
          val p = mapMessageIdToPromise.get(id)
          IdtoPromiseDrop(id)
        /// Return failure to 
        // p.success(new Exception("""Error im request with id: $id. Returned errorId: $errorMessageId"""))
        case None =>
          log("Client handleReceivedMessage\t" + "No id in msg" + "\n" + msg)
      }

    } else {
      val msgId = getMessageId(msg)

      msgId match {
        case Some(id) =>
          if (msg.length > 150) {
            log("[Incoming Message]\t" + "Id: " + id + "\t" + msg.substring(0, 150) + "...")
          } else {
            log("[Incoming Message]\t" + "Id: " + id + "\t" + msg)
          }
          val p = mapMessageIdToPromise.get(id)
          IdtoPromiseDrop(id)
          p.success(msg)
        case None =>
          log("[Incoming Message Error]\t" + "No id in msg" + "\n" + msg)
      }
    }
  }

  /**
   * Returns the id of the incoming message.
   */
  def getMessageId(msg: String): Option[Int] = {
    // What about error cases???
    if (msg.startsWith("(:return (:ok ")) {
      val id = msg.substring(msg.lastIndexOf(" ") + 1, msg.length() - 1).toInt
      Some(id)
    } else if (msg.startsWith("(:reader-error ")) {
      val id = msg.substring(msg.lastIndexOf(" ") + 1, msg.length() - 3).toInt
      Some(id)
    } else {
      None
    }
  }

  private def isErrorMessage(msg: String): Boolean = {
    if (msg.startsWith("(:reader-error ")) return true
    return false

  }

  private def getErrorMessageId(msg: String): Int = {
    msg.split(" ")(1).toInt
  }

  // ---------------------------------------------------------------------------
  // Outgoing messages
  // ---------------------------------------------------------------------------

  /**
   * Sends message without entry in the id->promise map.
   */
  def sendMessage(msg: String): Unit = {
    val msgBytes = (addPaddingToOutgoingMessage(msg) + msg).getBytes("UTF-8")
    out.write(msgBytes, 0, msgBytes.length);
    out.flush()
  }
  /// Todo: Delete
  private def sendMessage(out: DataOutputStream, msg: String): Unit = {
    val msgBytes = (addPaddingToOutgoingMessage(msg) + msg).getBytes("UTF-8")
    out.write(msgBytes, 0, msgBytes.length);
    out.flush()
  }

  /// Todo: Delete
  private def msgPadding(msg: Array[Byte]): Array[Byte] = {
    ("0" * (6 - msg.length.toHexString.length) + msg.length.toHexString).getBytes("UTF-8")
  }

  /**
   * Adds 6 bytes of message length length information in 0x to the front of the message.
   */
  private def addPaddingToOutgoingMessage(msg: String): String = {
    //("0"*(6-msg.length().toHexString.length()) + msg.length().toHexString)
    "%06x".format(msg.length)
  }

  /**
   * Add entry id -> promise to the mapMessageIdToPromise.
   */
  def IdtoPromiseAdd(msgId: Int, p: Promise[String]): Unit = {
    mapMessageIdToPromise.put(msgId, p)
  }

  /**
   * Remove id entry from
   */
  def IdtoPromiseDrop(msgId: Int): Unit = {
    mapMessageIdToPromise.remove(msgId)
  }

  /**
   * Send message and create.
   */
  def sendMessage(p: Promise[String], msgId: Int, msg: String): Unit = {
    IdtoPromiseAdd(msgId, p)
    sendMessage(msg)
  }

  private def log(s: String): Unit = {
    println("[ENSIME-Client]" + "\t" + s)
  }

}