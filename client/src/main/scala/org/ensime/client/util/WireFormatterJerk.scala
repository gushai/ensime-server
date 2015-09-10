package org.ensime.client.util

import org.ensime.api.RpcRequest
import org.ensime.api.RpcRequestEnvelope
import spray.json._

/**
 * Transforms requests into Jerk wire messages.
 *
 * Relies on org.ensime.jerk json formats:
 * -  org.ensime.jerk.JerkFormats._
 * -  org.ensime.jerk.JerkEnvelopeFormats._
 *
 */
class WireFormatterJerk extends WireFormatter {

  /**
   * Transforms RpcRequests in to Jerk wire messages incl call id.
   * @param request RpcRequest to be sent to ensime-server.
   * @param callId  Id assigned to the request.
   * @return         Wire format of the request.
   */
  def toWireFormat(request: RpcRequest, callId: Int): String = {
    import org.ensime.jerk.JerkFormats._
    import org.ensime.jerk.JerkEnvelopeFormats._

    val reqEnv = new RpcRequestEnvelope(request, callId)
    transform(reqEnv)
  }

  /**
   * Transform request to json string.
   * @param T     JsonFormat
   * @param value Object to transform.
   * @return      Json message string.
   */
  private def transform[T: JsonFormat](value: T): String = {
    val json = value.toJson
    json.toString()
  }
}

object WireFormatterJerk extends WireFormatterJerk