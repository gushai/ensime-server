package org.ensime.client.util

import org.ensime.api.RpcResponseEnvelope
import org.ensime.api.EnsimeServerError

/**
 * @author gus
 */

class NetworkClientJerk(implicit context: NetworkClientContext) extends NetworkClientMain {

  def handleReceivedMessage(responseMessage: String): Unit = {
    // println("[handleReceivedMessage]" + responseMessage + "\n")

    import spray.json._
    import org.ensime.jerk.JerkFormats._
    import org.ensime.jerk.JerkEnvelopeFormats._

    // TODO: Do async via a Future? 
    val jsonAst = responseMessage.parseJson
    val responseEnvelope = jsonAst.convertTo[RpcResponseEnvelope]

    // Return to caller
    returnToCaller(responseEnvelope)

  }

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