package org.ensime.client.util

/**
 * @author gus
 */

class NetworkClientSwank(implicit context: NetworkClientContext) extends NetworkClientMain {

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

  def handleReceivedMessage(msg: String): Unit = {
    // Check whether its and error or an answer
    // TODO: Add identifier for errors!
    if (isErrorMessage(msg)) {

      val errorMessageId = getErrorMessageId(msg)
      logger.error("ErrorId: " + errorMessageId + "\t" + msg)

      getMessageId(msg) match {
        case Some(id) =>
          val p = mapMessageIdToPromise.get(id)
          IdtoPromiseDrop(id)
        /// Return failure to 
        // p.success(new Exception("""Error im request with id: $id. Returned errorId: $errorMessageId"""))
        case None =>
          logger.info("Incoming message w/o id:" + "\n" + msg)
      }

    } else {
      val msgId = getMessageId(msg)

      msgId match {
        case Some(id) =>
          val msgPrint = msg
          if (msg.length > 150) {
            logger.info("[Incoming Message]\t" + "Id: " + id + "\t" + msgPrint)
          }
          val p = mapMessageIdToPromise.get(id)
          IdtoPromiseDrop(id)
          p.success(msg)
        case None =>
          logger.info("[Incoming Message Error]\t" + "No id in msg" + "\n" + msg)
      }
    }
  }

  private def isErrorMessage(msg: String): Boolean = {
    if (msg.startsWith("(:reader-error ")) return true
    return false

  }

  private def getErrorMessageId(msg: String): Int = {
    msg.split(" ")(1).toInt
  }

}