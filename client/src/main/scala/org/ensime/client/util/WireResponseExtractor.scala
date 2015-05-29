package org.ensime.client.util

case object ResponseError
case object ResponseMessage

trait WireResponseExtractor {

  def isSuccessfullResponse(msg: String): Boolean

  def dropResponseEnvelope(msg: String): String

  //def getResponseObject[T](msg: String) : T

}