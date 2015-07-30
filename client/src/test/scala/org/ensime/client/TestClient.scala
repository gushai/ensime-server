package org.ensime.client

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import org.ensime.model._
import java.io.File
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

class TestClient extends FunSpec with BeforeAndAfterAll {
  //
  //  val host = "127.0.0.1"
  //  val port = 41549
  //
  //  val logString = "[TestClient]\t"
  //
  //  val client = new Client()(new ClientContext(host, port, true))
  //  // File used in all tests 
  //  val path = "/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Example.scala"
  //
  //  override def beforeAll() {
  //    println(logString + "Starting ENSIME-Client...")
  //    client.initialize()
  //    println(logString + "Done.")
  //  }
  //
  //  override def afterAll() {
  //    println(logString + "Waiting for results...")
  //    Thread.sleep(2000)
  //    println(logString + "Done")
  //    println(logString + "Closing ENSIME-Client...")
  //    client.close()
  //    println(logString + "Done")
  //  }
  //
  //  describe("A ENSIME-Client") {
  //
  //    it("should send and receive a ConnectionInfoReq") {
  //      val connectionInfo = client.connectionInfo()
  //
  //      connectionInfo.onComplete {
  //        case Success(cI) => {
  //          if (cI.pid.isDefined) {
  //            assert(cI.pid.get == 1)
  //          } else {
  //            assert(cI.pid.isEmpty)
  //          }
  //          assert(cI.implementation.name.equals("ENSIME"))
  //          assert(cI.version.equals("0.8.14"))
  //
  //        }
  //        case Failure(t) => {
  //          println("An error has occured: " + t.getMessage)
  //          fail()
  //        }
  //      }
  //    }
  //
  //    it("should send and receive a SymbolDesignationsReq") {
  //      val f = new File(path)
  //      val start = 0
  //      val end = 588
  //      val requestedTypes = List(ObjectSymbol, ClassSymbol, TraitSymbol,
  //        PackageSymbol, ConstructorSymbol, ImportedNameSymbol, TypeParamSymbol,
  //        ParamSymbol, VarFieldSymbol, ValFieldSymbol, OperatorFieldSymbol, VarSymbol, ValSymbol, FunctionCallSymbol)
  //      val semanticHighlight = client.symbolDesignations(f, start, end, requestedTypes)
  //
  //      semanticHighlight.onComplete {
  //        case Success(sH) => {
  //          //println(sH.file)
  //          // println(sH.syms)
  //          assert(sH.file.getAbsolutePath.equals(path))
  //        }
  //        case Failure(t) => {
  //          println("An error has occured: " + t.getMessage)
  //          fail()
  //        }
  //      }
  //
  //    }
  //
  //    it("should send a TypeAtPointReq with range input from eq to") {
  //      val file = new File(path)
  //      val rangeFrom = 314
  //      val rangeTo = 314
  //
  //      val typeInfo = client.typeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
  //
  //      typeInfo.onComplete {
  //        case Success(tI) => {
  //          if (tI.isDefined) {
  //            assert(tI.get.fullName == "org.codeprose.rational.Rational")
  //            println("Arguments: " + tI.get.args)
  //            println("Members: " + tI.get.members)
  //            println("Postion: " + tI.get.pos)
  //          } else fail()
  //
  //        }
  //        case Failure(t) => {
  //          println("An error has occured: " + t.getMessage)
  //          fail()
  //        }
  //      }
  //    }
  //
  //    it("should send a TypeAtPointReq with range input from eq to on function") {
  //      val file = new File(path)
  //      val rangeFrom = 398
  //      val rangeTo = 398
  //
  //      val typeInfo = client.typeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
  //
  //      typeInfo.onComplete {
  //        case Success(tI) => {
  //          if (tI.isDefined) {
  //            assert(tI.get.fullName == "org.codeprose.rational.Rational")
  //            println("Arguments: " + tI.get.args)
  //            println("Members: " + tI.get.members)
  //            println("Postion: " + tI.get.pos)
  //          } else fail()
  //
  //        }
  //        case Failure(t) => {
  //          println("An error has occured: " + t.getMessage)
  //          fail()
  //        }
  //      }
  //    }
  //
  //    it("should send a TypeAtPointReq with range input from neq to") {
  //      val file = new File(path)
  //      val rangeFrom = 323
  //      val rangeTo = 325
  //
  //      val typeAtPoint = client.typeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
  //
  //      typeAtPoint onComplete {
  //        case Success(tI) => {
  //          if (tI.isDefined) {
  //            assert(tI.get.fullName == "org.codeprose.rational.Rational")
  //            println("Arguments: " + tI.get.args)
  //            println("Members: " + tI.get.members)
  //            println("Postion: " + tI.get.pos)
  //          } else fail()
  //        }
  //        case Failure(t) => fail()
  //      }
  //    }
  //
  //    ignore("should send a InspectTypeAtPoint w/ from eq to") {
  //      val file = new File(path)
  //      val rangeFrom = 314
  //      val rangeTo = 314
  //      val inspectTypeAtPoint = client.inspectTypeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
  //
  //      inspectTypeAtPoint.onComplete {
  //        case Success(tII) => {
  //          if (tII.isDefined) {
  //            println("\n\nyeah!\n\n")
  //            println(tII.get.infoType.name)
  //
  //          } else fail()
  //
  //        }
  //        case Failure(t) => {
  //          println(t)
  //          fail()
  //        }
  //
  //      }
  //    }
  //
  //    ignore("should send a InspectPackageByPath") {
  //      val path = "rational"
  //      val inspectPackageByPath = client.inspectPackageByPath(path)
  //    }
  //
  //    ignore("should send a TypecheckFileReq") {
  //      val f = new File("/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala")
  //      client.typecheckFile(SourceFileInfo(f, None, None))
  //    }
  //
  //    ignore("should send a TypecheckAllReq") {
  //      client.typecheckAll()
  //    }
  //
  //    ignore("should send a ShutdownReq") {
  //      //client.shutdownServer()
  //      fail()
  //    }
  //
  //  }

}