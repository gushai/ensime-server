package org.ensime.client.util

import org.ensime.api.RpcRequest

/**
 * Transforms requests to wire messages.
 *
 * Use to implement different protocols.
 */
trait WireFormatter {

  /**
   * Transforms a RpcRequest into a wire message incl. call id.
   * @param request RpcRequest to be send to ensime-server.
   * @param callIDd Id assigned to the request.
   * @return        Wire format of the request.
   */
  def toWireFormat(request: RpcRequest, callId: Int): String

}