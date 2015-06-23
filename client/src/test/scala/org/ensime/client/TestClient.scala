package org.ensime.client

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import org.ensime.model._
import java.io.File
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

class TestClient extends FunSpec with BeforeAndAfterAll {

  val host = "127.0.0.1"
  val port = 33965

  val logString = "[TestClient]\t"

  val client = new Client(host, port)

  override def beforeAll() {
    println(logString + "Starting ENSIME-Client...")
    client.initialize()
    println(logString + "Done.")
  }

  override def afterAll() {
    println(logString + "Waiting for results...")
    Thread.sleep(20000)
    println(logString + "Done")
    println(logString + "Closing ENSIME-Client...")
    client.close()
    println(logString + "Done")
  }

  describe("A ENSIME-Client") {

    it("should send and receive a ConnectionInfoReq") {
      val connectionInfo = client.connectionInfo()

      connectionInfo.onComplete {
        case Success(cI) => {
          if (cI.pid.isDefined) {
            assert(cI.pid.get == 1)
          } else {
            assert(cI.pid.isEmpty)
          }
          assert(cI.implementation.name.equals("ENSIME"))
          assert(cI.version.equals("0.8.14"))

        }
        case Failure(t) => {
          println("An error has occured: " + t.getMessage)
          fail()
        }
      }
    }

    it("should send and receive a SymbolDesignationsReq") {
      val path = "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala"
      val f = new File(path)
      val start = 0
      val end = 1897
      val requestedTypes = List(ObjectSymbol, ClassSymbol, TraitSymbol,
        PackageSymbol, ConstructorSymbol, ImportedNameSymbol, TypeParamSymbol,
        ParamSymbol, VarFieldSymbol, ValFieldSymbol, OperatorFieldSymbol, VarSymbol, ValSymbol, FunctionCallSymbol)
      val semanticHighlight = client.symbolDesignations(f, start, end, requestedTypes)

      semanticHighlight.onComplete {
        case Success(sH) => {
          //println(sH.file)
          // println(sH.syms)
          assert(sH.file.getAbsolutePath.equals(path))
        }
        case Failure(t) => {
          println("An error has occured: " + t.getMessage)
          fail()
        }
      }

    }

    it("should send a TypeAtPointReq with range input from eq to") {
      val path = "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala"
      val file = new File(path)
      val rangeFrom = 247
      val rangeTo = 247

      client.typeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
    }

    it("should send a TypeAtPointReq with range input from neq to") {
      val path = "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala"
      val file = new File(path)
      val rangeFrom = 150
      val rangeTo = 151

      val typeAtPoint = client.typeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))

      typeAtPoint onComplete {
        case Success(tI) => {
          if (tI.isDefined) {
            println("TypeAtPointReq:\t" + tI.get.fullName + " \\\\ ")
          }
        }
        case Failure(t) => fail()
      }
    }

    ignore("should send a InspectTypeAtPoint") {
      val path = "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala"
      val file = new File(path)
      val rangeFrom = 150
      val rangeTo = 151
      val inspectTypeAtPoint = client.inspectTypeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))
    }

    ignore("should send a InspectPackageByPath") {
      val path = "rational"
      val inspectPackageByPath = client.inspectPackageByPath(path)
    }

    ignore("should send a TypecheckFileReq") {
      val f = new File("/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala")
      client.typecheckFile(SourceFileInfo(f, None, None))
    }

    ignore("should send a TypecheckAllReq") {
      client.typecheckAll()
    }

    ignore("should send a ShutdownReq") {
      //client.shutdownServer()
      fail()
    }

  }

}