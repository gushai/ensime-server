package org.ensime.client.util

import org.ensime.server.protocol.RpcRequest

trait WireFormatter {

  def toWireFormat(request: RpcRequest, callId: Int): String

}