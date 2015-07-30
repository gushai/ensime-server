package org.ensime.client.util

import org.scalatest.FunSpec
import spray.json._
import org.ensime.api.ConnectionInfoReq
import org.ensime.api.RpcRequestEnvelope
import org.ensime.api.ImplicitInfoReq
import org.ensime.api.OffsetRange
import java.io.File
import org.ensime.api.SymbolDesignationsReq
import org.ensime.api.InspectTypeAtPointReq
import org.ensime.api.InspectTypeAtPointReq
import org.ensime.api.TypeAtPointReq
import org.scalatest.BeforeAndAfterAll
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Promise

/**
 * @author gus
 */

trait CommonInputs {

  // Connection
  val netClient = new NetworkClientJerk()(new NetworkClientContext("127.0.0.1", 49349, true))

  // Call id simulation
  val callIdCounter = new AtomicInteger(1)

  // Request information
  val path = "/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Example.scala"
  val file = new File(path)

}

class GetToKnowJerkTest extends FunSpec with BeforeAndAfterAll with CommonInputs {
  import org.ensime.client.util.WireFormatterJerk

  override def beforeAll() = {
    println("Setting up network client")
    netClient.start()
  }
  override def afterAll() = {
    println("Waiting for repsonses ... ")
    Thread.sleep(2000)
    println("Closing network client")

    netClient.close()
  }

  describe("A ensime Jerk client") {

    it("should send a ConnectionInfoReq") {
      val req = ConnectionInfoReq
      val callId = callIdCounter.getAndIncrement()
      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      assert(reqMsg == s"""{"req":{"typehint":"ConnectionInfoReq"},"callId":$callId}""")

      netClient.sendMessage(Promise[String], callId, reqMsg)
    }

    it("should send a InspectTypeAtPointReq") {
      val range = OffsetRange(324, 325)
      val req = InspectTypeAtPointReq(file, range)
      val callId = callIdCounter.getAndIncrement()
      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      assert(reqMsg == s"""{"req":{"typehint":"InspectTypeAtPointReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")

      netClient.sendMessage(Promise[String], callId, reqMsg)
    }

    it("should send a TypeAtPointReq") {
      val range = OffsetRange(324, 325)
      val req = TypeAtPointReq(file, range)
      val callId = callIdCounter.getAndIncrement()
      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      assert(reqMsg == s"""{"req":{"typehint":"TypeAtPointReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")

      netClient.sendMessage(Promise[String], callId, reqMsg)
    }

    it("should send SymbolDesignationsReq") {

      import org.ensime.api.SourceSymbol
      val requestedTypes = SourceSymbol.allSymbols
      val start = 0
      val end = 325
      val req = SymbolDesignationsReq(file, start, end, requestedTypes)
      val callId = callIdCounter.getAndIncrement()
      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      assert(reqMsg == s"""{"req":{"requestedTypes":[{"typehint":"ObjectSymbol"},{"typehint":"ClassSymbol"},{"typehint":"TraitSymbol"},{"typehint":"PackageSymbol"},{"typehint":"ConstructorSymbol"},{"typehint":"ImportedNameSymbol"},{"typehint":"TypeParamSymbol"},{"typehint":"ParamSymbol"},{"typehint":"VarFieldSymbol"},{"typehint":"ValFieldSymbol"},{"typehint":"OperatorFieldSymbol"},{"typehint":"VarSymbol"},{"typehint":"ValSymbol"},{"typehint":"FunctionCallSymbol"},{"typehint":"ImplicitConversionSymbol"},{"typehint":"ImplicitParamsSymbol"},{"typehint":"DeprecatedSymbol"}],"typehint":"SymbolDesignationsReq","end":$end,"file":"$path","start":$start},"callId":$callId}""")

      netClient.sendMessage(Promise[String], callId, reqMsg)
    }

    it("should send a ImplicitInfoReq") {

      val offsetRange = OffsetRange(324, 325)
      val req = ImplicitInfoReq(file, offsetRange)
      val callId = callIdCounter.getAndIncrement()
      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      assert(reqMsg == s"""{"req":{"typehint":"ImplicitInfoReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")

      netClient.sendMessage(Promise[String], callId, reqMsg)

    }

  }
}