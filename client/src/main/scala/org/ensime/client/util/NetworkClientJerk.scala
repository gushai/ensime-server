package org.ensime.client.util

/**
 * @author gus
 */

class NetworkClientJerk(implicit context: NetworkClientContext) extends NetworkClientMain {

  def getMessageId(msg: String): Option[Int] = {
    return Some(1)
  }

  def handleReceivedMessage(msg: String): Unit = {
    println("[handleReceivedMessage]" + msg + "\n")
  }

}