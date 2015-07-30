package org.ensime.client.util

import org.ensime.api.RpcRequest
import org.ensime.api.RpcRequestEnvelope
import spray.json._
/**
 * @author gus
 */

class WireFormatterJerk extends WireFormatter {

  def toWireFormat(request: RpcRequest, callId: Int): String = {
    import org.ensime.jerk.JerkFormats._
    import org.ensime.jerk.JerkEnvelopeFormats._

    //import org.ensime.api.EscapingStringInterpolation._

    val reqEnv = new RpcRequestEnvelope(request, callId)
    transform(reqEnv)

  }

  private def transform[T: JsonFormat](value: T): String = {
    val json = value.toJson
    json.toString()
  }
}

object WireFormatterJerk extends WireFormatterJerk