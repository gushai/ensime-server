package org.ensime.client.util

import org.ensime.api.RpcResponseEnvelope
import org.ensime.api.EnsimeServerError

/**
 * Ensime-client using the Jerk protocol.
 * @param context NetworkClientContext
 */
class NetworkClientJerk(implicit context: NetworkClientContext) extends NetworkClientMain {

  /**
   * Translates incoming response from the ensime server into information objects from
   * org.ensime.api.outgoing
   */
  def handleReceivedMessage(responseMessage: String): Unit = {

    if (context.verbose)
      logger.info("[Incoming Message]" + responseMessage.take(150))

    import spray.json._
    import org.ensime.jerk.JerkFormats._
    import org.ensime.jerk.JerkEnvelopeFormats._

    // TODO: Do async via a Future?
    val responseEnvelope = try {
      val jsonAst = responseMessage.parseJson
      Some(jsonAst.convertTo[RpcResponseEnvelope])
    } catch {
      case e: Throwable => {
        logger.error("Message can not be parsed by spray.json. " + e.getMessage)
        None
      }
    }

    // Return to caller
    responseEnvelope match {
      case Some(responseEnv) => {
        returnToCaller(responseEnv)
      }
      case None => {
        logger.error("Incoming message could not be parsed! Exception thrown by spary.json. Message is ignored.")
      }
    }

  }

  /**
   * Returns the incoming server response to the caller (i.e. assigned promise).
   *
   * Messages without message id are print to std out.
   *
   * TODO: Implement here where to send incoming messages without message id.
   *
   * @param   responseEnvelope  RpcResponseEnvelope
   */
  private def returnToCaller(responseEnvelope: RpcResponseEnvelope): Unit = {

    responseEnvelope.callId match {
      case Some(callId) => {
        if (!doesPromiseEntryExist(callId)) {
          logger.error(s"""Oops. Found callId ($callId) w/o promise in map. Message will ne ignored.""")
        } else {
          val p = getPromiseAndRemoveEntry(callId)

          if (responseEnvelope.payload.isInstanceOf[EnsimeServerError]) {
            p.failure(new Exception("EnsimeServerError: " + responseEnvelope.payload.asInstanceOf[EnsimeServerError].description))
          } else {
            p.success(responseEnvelope.payload)
          }
        }
      }
      case _ => {
        logger.info("[EnsimeEvent]\t" + responseEnvelope.payload)
        // TODO: Hook to container + stuff
      }
    }

  }

}