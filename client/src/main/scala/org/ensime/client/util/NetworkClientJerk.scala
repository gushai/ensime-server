package org.ensime.client.util

/**
 * @author gus
 */

class NetworkClientJerk(implicit context: NetworkClientContext) extends NetworkClientMain {

  def getMessageId(msg: String): Option[Int] = {
    return Some(1)
  }

  def handleReceivedMessage(msg: String): Unit = {
    logger.info("[JERK incoming message] \n" + msg + "\n")
  }

}