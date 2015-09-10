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
import org.ensime.api.RpcResponseEnvelope
import org.ensime.api.ConnectionInfo
import org.ensime.api.TypeInspectInfo
import org.ensime.api.SymbolAtPointReq
import org.ensime.api.SymbolInfo
import org.ensime.api.OffsetSourcePosition
import org.ensime.api.LineSourcePosition
import org.ensime.api.OffsetSourcePosition
import javax.swing.JTree.EmptySelectionModel
import org.ensime.api.EmptySourcePosition

// TODO: Clean up or delete!


/**
 * Common inputs used in the test below.
 * 
 * Fill in:
 * -  port
 * -  path
 * 
 */
trait CommonInputs {

  // Connection
  val host  = "127.0.0.1"
  val port  = ???
  val netClient = new NetworkClientJerk()(new NetworkClientContext(host, port, true))

  // Call id simulation
  val callIdCounter = new AtomicInteger(1)

  // Request information
  val path : String = ??? // Path to a scala file.
  val file = new File(path)

}

/**
 * Test class to understand the jerk protocol.
 */
class GetToKnowJerkTest extends FunSpec with BeforeAndAfterAll with CommonInputs {
  import org.ensime.client.util.WireFormatterJerk

  /*
   * Setup connection to ensime-server.
   */
  override def beforeAll() = {
    println("Setting up network client")
    netClient.start()
  }
  
  /*
   * Closing client after brief wait.
   */
  override def afterAll() = {
    println("Waiting for repsonses ... ")
    Thread.sleep(5000)
    println("Closing network client")

    netClient.close()
  }

  describe("A ensime Jerk client") {

    //    it("should send a ConnectionInfoReq") {
    //      val req = ConnectionInfoReq
    //      val callId = callIdCounter.getAndIncrement()
    //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
    //      assert(reqMsg == s"""{"req":{"typehint":"ConnectionInfoReq"},"callId":$callId}""")
    //
    //      netClient.sendMessage(Promise[String], callId, reqMsg)
    //    }
    //
    //    it("should send a InspectTypeAtPointReq") {
    //      val range = OffsetRange(324, 325)
    //      val req = InspectTypeAtPointReq(file, range)
    //      val callId = callIdCounter.getAndIncrement()
    //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
    //      assert(reqMsg == s"""{"req":{"typehint":"InspectTypeAtPointReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")
    //
    //      netClient.sendMessage(Promise[String], callId, reqMsg)
    //    }
    //
    //    it("should send a TypeAtPointReq") {
    //      val range = OffsetRange(324, 325)
    //      val req = TypeAtPointReq(file, range)
    //      val callId = callIdCounter.getAndIncrement()
    //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
    //      assert(reqMsg == s"""{"req":{"typehint":"TypeAtPointReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")
    //
    //      netClient.sendMessage(Promise[String], callId, reqMsg)
    //    }
    //
    //    it("should send SymbolDesignationsReq") {
    //
    //      import org.ensime.api.SourceSymbol
    //      val requestedTypes = SourceSymbol.allSymbols
    //      val start = 0
    //      val end = 325
    //      val req = SymbolDesignationsReq(file, start, end, requestedTypes)
    //      val callId = callIdCounter.getAndIncrement()
    //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
    //      assert(reqMsg == s"""{"req":{"requestedTypes":[{"typehint":"ObjectSymbol"},{"typehint":"ClassSymbol"},{"typehint":"TraitSymbol"},{"typehint":"PackageSymbol"},{"typehint":"ConstructorSymbol"},{"typehint":"ImportedNameSymbol"},{"typehint":"TypeParamSymbol"},{"typehint":"ParamSymbol"},{"typehint":"VarFieldSymbol"},{"typehint":"ValFieldSymbol"},{"typehint":"OperatorFieldSymbol"},{"typehint":"VarSymbol"},{"typehint":"ValSymbol"},{"typehint":"FunctionCallSymbol"},{"typehint":"ImplicitConversionSymbol"},{"typehint":"ImplicitParamsSymbol"},{"typehint":"DeprecatedSymbol"}],"typehint":"SymbolDesignationsReq","end":$end,"file":"$path","start":$start},"callId":$callId}""")
    //
    //      netClient.sendMessage(Promise[String], callId, reqMsg)
    //    }
    //
    //    it("should send a ImplicitInfoReq") {
    //
    //      val offsetRange = OffsetRange(324, 325)
    //      val req = ImplicitInfoReq(file, offsetRange)
    //      val callId = callIdCounter.getAndIncrement()
    //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
    //      assert(reqMsg == s"""{"req":{"typehint":"ImplicitInfoReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")
    //
    //      netClient.sendMessage(Promise[String], callId, reqMsg)
    //
    //    }

  }

  describe("it should unpack incoming messages") {

    it("should send a ConnectionInfoReq") {
      //      val req = ConnectionInfoReq
      //      val callId = callIdCounter.getAndIncrement()
      //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      //      assert(reqMsg == s"""{"req":{"typehint":"ConnectionInfoReq"},"callId":$callId}""")
      //     netClient.sendMessage(Promise[String], callId, reqMsg)

      val response = """{"callId":1,"payload":{"typehint":"ConnectionInfo","implementation":{"name":"ENSIME"},"version":"0.8.17"}}"""

      import spray.json._
      import org.ensime.jerk.JerkFormats._
      import org.ensime.jerk.JerkEnvelopeFormats._

      val jsonAst = response.parseJson
      println(jsonAst)
      val json = jsonAst.prettyPrint // or .compactPrint
      println(json)
      val myObject = jsonAst.convertTo[RpcResponseEnvelope]
      println(myObject.toString)
      val responseObj = myObject.payload

      println(responseObj)
      //      println(responseObj.pid) elements can not be accessed this way!
      println(responseObj.isInstanceOf[ConnectionInfo])

      responseObj match {
        case ConnectionInfo(pid, implementation, version) => {
          println(pid)
          println(implementation)
          println(version)
        }
        case _ => fail()
      }

    }

    it("should send a InspectTypeAtPointReq") {
      //      val range = OffsetRange(324, 325)
      //      val req = InspectTypeAtPointReq(file, range)
      //      val callId = callIdCounter.getAndIncrement()
      //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      //      assert(reqMsg == s"""{"req":{"typehint":"InspectTypeAtPointReq","file":"$path","range":{"from":324,"to":325}},"callId":$callId}""")
      //
      //      netClient.sendMessage(Promise[String], callId, reqMsg)

      val response = """{"callId":1,"payload":{"typehint":"TypeInspectInfo","type":{"name":"Rational","fullName":"org.codeprose.rational.Rational","pos":{"typehint":"OffsetSourcePosition","file":"/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Rational.scala","offset":627},"typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"interfaces":[{"type":{"name":"StringFormat","fullName":"scala.Predef$$StringFormat","typehint":"BasicTypeInfo","typeId":4,"outerTypeId":3,"typeArgs":[{"name":"A","fullName":"scala.A","typehint":"BasicTypeInfo","typeId":5,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[{"name":"formatted","typehint":"NamedTypeMemberInfo","signatureString":"(fmtstr: String): String","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"String","fullName":"java.lang.String","typehint":"BasicTypeInfo","typeId":1,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(fmtstr: String)String","paramSections":[{"params":[["fmtstr",{"name":"String","fullName":"java.lang.String","typehint":"BasicTypeInfo","typeId":1,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":69}}],"declAs":{"typehint":"Class"}},"viaView":"StringFormat"},{"type":{"name":"Rational","fullName":"org.codeprose.rational.Rational","pos":{"typehint":"OffsetSourcePosition","file":"/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Rational.scala","offset":627},"typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[{"name":"*","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":70}},{"name":"*","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":71}},{"name":"+","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":72}},{"name":"+","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":73}},{"name":"-","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":74}},{"name":"-","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":75}},{"name":"<init>","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(n: Int,d: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(n: Int, d: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["n",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],["d",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":76}},{"name":"<init>","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(n: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(n: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["n",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":77}},{"name":"add","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":78}},{"name":"add","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":79}},{"name":"denominator","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":": Int","declAs":{"typehint":"Method"},"type":{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}},{"name":"multiply","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":80}},{"name":"multiply","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":81}},{"name":"numerator","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":": Int","declAs":{"typehint":"Method"},"type":{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}},{"name":"subtract","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: org.codeprose.rational.Rational): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: org.codeprose.rational.Rational)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":82}},{"name":"subtract","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(that: Int): org.codeprose.rational.Rational","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(that: Int)org.codeprose.rational.Rational","paramSections":[{"params":[["that",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":83}},{"name":"toString","pos":{"typehint":"EmptySourcePosition"},"typehint":"NamedTypeMemberInfo","signatureString":"(): String","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"String","fullName":"java.lang.String","typehint":"BasicTypeInfo","typeId":23,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()String","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":22}}],"declAs":{"typehint":"Class"}}},{"type":{"name":"any2stringadd","fullName":"scala.Predef$$any2stringadd","typehint":"BasicTypeInfo","typeId":27,"outerTypeId":3,"typeArgs":[{"name":"A","fullName":"scala.A","typehint":"BasicTypeInfo","typeId":28,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[{"name":"+","typehint":"NamedTypeMemberInfo","signatureString":"(other: String): String","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"String","fullName":"java.lang.String","typehint":"BasicTypeInfo","typeId":1,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(other: String)String","paramSections":[{"params":[["other",{"name":"String","fullName":"java.lang.String","typehint":"BasicTypeInfo","typeId":1,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":84}},{"name":"<init>","typehint":"NamedTypeMemberInfo","signatureString":"(self: A): any2stringadd[A]","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"any2stringadd","fullName":"scala.Predef$$any2stringadd","typehint":"BasicTypeInfo","typeId":26,"outerTypeId":3,"typeArgs":[{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],"members":[],"declAs":{"typehint":"Class"}},"name":"(self: org.codeprose.rational.Rational)any2stringadd[org.codeprose.rational.Rational]","paramSections":[{"params":[["self",{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":85}}],"declAs":{"typehint":"Class"}},"viaView":"any2stringadd"},{"type":{"name":"ArrowAssoc","fullName":"scala.Predef$$ArrowAssoc","typehint":"BasicTypeInfo","typeId":35,"outerTypeId":3,"typeArgs":[{"name":"A","fullName":"scala.A","typehint":"BasicTypeInfo","typeId":36,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[{"name":"->","typehint":"NamedTypeMemberInfo","signatureString":"[B](y: B): (A, B)","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Tuple2","fullName":"scala.Tuple2","typehint":"BasicTypeInfo","typeId":31,"typeArgs":[{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},{"name":"B","fullName":"scala.B","typehint":"BasicTypeInfo","typeId":29,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[],"declAs":{"typehint":"Class"}},"name":"[B](y: B)(org.codeprose.rational.Rational, B)","paramSections":[{"params":[["y",{"name":"B","fullName":"scala.B","typehint":"BasicTypeInfo","typeId":29,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":30}},{"name":"→","typehint":"NamedTypeMemberInfo","signatureString":"[B](y: B): (A, B)","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Tuple2","fullName":"scala.Tuple2","typehint":"BasicTypeInfo","typeId":34,"typeArgs":[{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},{"name":"B","fullName":"scala.B","typehint":"BasicTypeInfo","typeId":32,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[],"declAs":{"typehint":"Class"}},"name":"[B](y: B)(org.codeprose.rational.Rational, B)","paramSections":[{"params":[["y",{"name":"B","fullName":"scala.B","typehint":"BasicTypeInfo","typeId":32,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":33}}],"declAs":{"typehint":"Class"}},"viaView":"ArrowAssoc"},{"type":{"name":"Object","fullName":"java.lang.Object","typehint":"BasicTypeInfo","typeId":56,"typeArgs":[],"members":[{"name":"!=","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: Any): Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: Any)Boolean","paramSections":[{"params":[["x$1",{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":38}},{"name":"##","typehint":"NamedTypeMemberInfo","signatureString":"(): Int","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()Int","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":40}},{"name":"==","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: Any): Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: Any)Boolean","paramSections":[{"params":[["x$1",{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":41}},{"name":"eq","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: AnyRef): Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: AnyRef)Boolean","paramSections":[{"params":[["x$1",{"name":"Object","fullName":"java.lang.Object","typehint":"BasicTypeInfo","typeId":42,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":43}},{"name":"equals","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: Any): Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: Any)Boolean","paramSections":[{"params":[["x$1",{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":44}},{"name":"getClass","typehint":"NamedTypeMemberInfo","signatureString":"(): Class[_]","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Class","fullName":"java.lang.Class","typehint":"BasicTypeInfo","typeId":46,"typeArgs":[{"name":"?0","fullName":"java.lang.?0","typehint":"BasicTypeInfo","typeId":47,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[],"declAs":{"typehint":"Class"}},"name":"()Class[_]","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":45}},{"name":"hashCode","typehint":"NamedTypeMemberInfo","signatureString":"(): Int","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()Int","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":40}},{"name":"ne","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: AnyRef): Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: AnyRef)Boolean","paramSections":[{"params":[["x$1",{"name":"Object","fullName":"java.lang.Object","typehint":"BasicTypeInfo","typeId":42,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":48}},{"name":"notify","typehint":"NamedTypeMemberInfo","signatureString":"(): Unit","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Unit","fullName":"scala.Unit","typehint":"BasicTypeInfo","typeId":50,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()Unit","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":49}},{"name":"notifyAll","typehint":"NamedTypeMemberInfo","signatureString":"(): Unit","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Unit","fullName":"scala.Unit","typehint":"BasicTypeInfo","typeId":50,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()Unit","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":49}},{"name":"synchronized","typehint":"NamedTypeMemberInfo","signatureString":"[T0](x$1: T0): T0","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"T0","fullName":"java.lang.T0","typehint":"BasicTypeInfo","typeId":51,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}},"name":"[T0](x$1: T0)T0","paramSections":[{"params":[["x$1",{"name":"T0","fullName":"java.lang.T0","typehint":"BasicTypeInfo","typeId":51,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":52}},{"name":"wait","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: Long): Unit","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Unit","fullName":"scala.Unit","typehint":"BasicTypeInfo","typeId":50,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: Long)Unit","paramSections":[{"params":[["x$1",{"name":"Long","fullName":"scala.Long","typehint":"BasicTypeInfo","typeId":53,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":54}},{"name":"wait","typehint":"NamedTypeMemberInfo","signatureString":"(x$1: Long,x$2: Int): Unit","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Unit","fullName":"scala.Unit","typehint":"BasicTypeInfo","typeId":50,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(x$1: Long, x$2: Int)Unit","paramSections":[{"params":[["x$1",{"name":"Long","fullName":"scala.Long","typehint":"BasicTypeInfo","typeId":53,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],["x$2",{"name":"Int","fullName":"scala.Int","typehint":"BasicTypeInfo","typeId":8,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":55}},{"name":"wait","typehint":"NamedTypeMemberInfo","signatureString":"(): Unit","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Unit","fullName":"scala.Unit","typehint":"BasicTypeInfo","typeId":50,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"()Unit","paramSections":[{"params":[],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":49}}],"declAs":{"typehint":"Class"}}},{"type":{"name":"Ensuring","fullName":"scala.Predef$$Ensuring","typehint":"BasicTypeInfo","typeId":63,"outerTypeId":3,"typeArgs":[{"name":"A","fullName":"scala.A","typehint":"BasicTypeInfo","typeId":64,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}}],"members":[{"name":"ensuring","typehint":"NamedTypeMemberInfo","signatureString":"(cond: Boolean): A","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(cond: Boolean)org.codeprose.rational.Rational","paramSections":[{"params":[["cond",{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":57}},{"name":"ensuring","typehint":"NamedTypeMemberInfo","signatureString":"(cond: Boolean,msg: => Any): A","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(cond: Boolean, msg: => Any)org.codeprose.rational.Rational","paramSections":[{"params":[["cond",{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],["msg",{"name":"<byname>","fullName":"scala.<byname>","typehint":"BasicTypeInfo","typeId":58,"typeArgs":[{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":59}},{"name":"ensuring","typehint":"NamedTypeMemberInfo","signatureString":"(cond: A => Boolean): A","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(cond: org.codeprose.rational.Rational => Boolean)org.codeprose.rational.Rational","paramSections":[{"params":[["cond",{"name":"Function1","fullName":"scala.Function1","typehint":"BasicTypeInfo","typeId":60,"typeArgs":[{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],"members":[],"declAs":{"typehint":"Trait"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":86}},{"name":"ensuring","typehint":"NamedTypeMemberInfo","signatureString":"(cond: A => Boolean,msg: => Any): A","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"(cond: org.codeprose.rational.Rational => Boolean, msg: => Any)org.codeprose.rational.Rational","paramSections":[{"params":[["cond",{"name":"Function1","fullName":"scala.Function1","typehint":"BasicTypeInfo","typeId":60,"typeArgs":[{"name":"Rational","fullName":"org.codeprose.rational.Rational","typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],"members":[],"declAs":{"typehint":"Trait"}}],["msg",{"name":"<byname>","fullName":"scala.<byname>","typehint":"BasicTypeInfo","typeId":58,"typeArgs":[{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}}],"members":[],"declAs":{"typehint":"Class"}}]],"isImplicit":false}],"typehint":"ArrowTypeInfo","typeId":87}}],"declAs":{"typehint":"Class"}},"viaView":"Ensuring"},{"type":{"name":"Any","fullName":"scala.Any","typehint":"BasicTypeInfo","typeId":37,"typeArgs":[],"members":[{"name":"asInstanceOf","typehint":"NamedTypeMemberInfo","signatureString":"[T0]: T0","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"T0","fullName":"scala.T0","typehint":"BasicTypeInfo","typeId":66,"typeArgs":[],"members":[],"declAs":{"typehint":"Nil"}},"name":"[T0]=> T0","paramSections":[],"typehint":"ArrowTypeInfo","typeId":65}},{"name":"isInstanceOf","typehint":"NamedTypeMemberInfo","signatureString":"[T0]: Boolean","declAs":{"typehint":"Method"},"type":{"resultType":{"name":"Boolean","fullName":"scala.Boolean","typehint":"BasicTypeInfo","typeId":39,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"name":"[T0]=> Boolean","paramSections":[],"typehint":"ArrowTypeInfo","typeId":67}}],"declAs":{"typehint":"Class"}}}],"infoType":"typeInspect"}}"""

      import spray.json._
      import org.ensime.jerk.JerkFormats._
      import org.ensime.jerk.JerkEnvelopeFormats._

      val jsonAst = response.parseJson
      println(jsonAst)
      val json = jsonAst.prettyPrint // or .compactPrint
      println(json)
      val myObject = jsonAst.convertTo[RpcResponseEnvelope]
      println(myObject.toString)
      val responseObj = myObject.payload

      println(responseObj)
      println(responseObj.isInstanceOf[TypeInspectInfo])

      responseObj match {
        case TypeInspectInfo(_, companionId, interfaces, _) => {
          println("yeah")
          println(companionId)
          interfaces.foreach(println)
        }
        case _ => fail()
      }

    }

    it("should send a SymbolInfoReq") {
      //      val point = 324
      //      val req = SymbolAtPointReq(file, point)
      //      val callId = callIdCounter.getAndIncrement()
      //      val reqMsg = WireFormatterJerk.toWireFormat(req, callId)
      //      assert(reqMsg == s"""{"req":{"typehint":"SymbolAtPointReq","file":"$path","point":$point},"callId":$callId}""")
      //
      //      netClient.sendMessage(Promise[String], callId, reqMsg)

      val response = """{"callId":1,"payload":{"name":"org.codeprose.rational.Rational","localName":"Rational","typehint":"SymbolInfo","declPos":{"typehint":"OffsetSourcePosition","file":"/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Rational.scala","offset":627},"type":{"name":"Rational","fullName":"org.codeprose.rational.Rational","pos":{"typehint":"OffsetSourcePosition","file":"/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Rational.scala","offset":627},"typehint":"BasicTypeInfo","typeId":6,"typeArgs":[],"members":[],"declAs":{"typehint":"Class"}},"isCallable":false,"ownerTypeId":88}}"""

      import spray.json._
      import org.ensime.jerk.JerkFormats._
      import org.ensime.jerk.JerkEnvelopeFormats._

      println("\n\nSymbolInfoReq")

      val jsonAst = response.parseJson
      println(jsonAst)
      val json = jsonAst.prettyPrint // or .compactPrint
      println(json)
      val myObject = jsonAst.convertTo[RpcResponseEnvelope]
      println(myObject.toString)
      val responseObj = myObject.payload

      println(responseObj)
      println(responseObj.isInstanceOf[SymbolInfo])

      responseObj match {
        case SymbolInfo(name, localName, declPos, _, isCallable, ownerTypeId) => {
          println(name)
          println(localName)
          println(declPos)

          if (declPos.isDefined) {
            val srcPos = declPos.get
            if (srcPos.isInstanceOf[OffsetSourcePosition])
              println("OffsetSourcePosition")
            else if (srcPos.isInstanceOf[LineSourcePosition])
              println("LinesourcePosition")
            else
              println("Unknown SourcePositionType")

            srcPos match {
              case OffsetSourcePosition(file, offset) => {

              }
              case LineSourcePosition(file, line) => {

              }
              case EmptySourcePosition() => {}
            }
          }
          println(isCallable)
          println(ownerTypeId)
        }
        case _ => fail()
      }

    }

  }

}