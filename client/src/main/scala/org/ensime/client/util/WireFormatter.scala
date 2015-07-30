package org.ensime.client.util

import org.ensime.api.RpcRequest

trait WireFormatter {

  def toWireFormat(request: RpcRequest, callId: Int): String

}