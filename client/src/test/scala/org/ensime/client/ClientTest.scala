package org.ensime.client

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import org.ensime.model._
import java.io.File
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global
import org.ensime.api.OffsetRange
import org.ensime.api.BasicTypeInfo
import org.ensime.api.OffsetSourcePosition
import org.ensime.api.ArrowTypeInfo
import org.ensime.api.ArrowTypeInfo

class ClientTest extends FunSpec with BeforeAndAfterAll {

  val host = "127.0.0.1"
  val port = 55357

  val logString = "[ClientClient]\t"

  val client = new Client()(new ClientContext(host, port, true))
  // File used in all tests 
  val path = "/home/gus/coding/scala/bscthesis/codeprose/codeprosetestprojects/codeprosetestprojects/testproject000/src/main/scala/org/codeprose/rational/Example.scala"

  override def beforeAll() {

    client.initialize()
    log("Done.")
  }

  override def afterAll() {
    log("Waiting for results...")
    Thread.sleep(2000)
    log("Done")
    log("Closing ENSIME-Client...")
    client.close()
    log("Done")
  }

  def log(msg: String): Unit = {
    println(logString + msg)
  }

  describe("A ENSIME-Client") {

    ignore("should send and receive a ConnectionInfoReq") {
      val connectionInfo = client.connectionInfo()

      connectionInfo.onComplete {
        case Success(cI) => {
          if (cI.pid.isDefined) {
            assert(cI.pid.get == 1)
          } else {
            assert(cI.pid.isEmpty)
          }
          assert(cI.implementation.name.equals("ENSIME"))
          assert(cI.version.equals("0.8.17"))

        }
        case Failure(t) => {
          log("An error has occured: " + t.getMessage)
          fail()
        }
      }
    }

    ignore("should send and receive a SymbolDesignationsReq") {
      val f = new File(path)
      val start = 0
      val end = 588
      val requestedTypes = org.ensime.api.SourceSymbol.allSymbols
      val semanticHighlight = client.symbolDesignations(f, start, end, requestedTypes)

      semanticHighlight.onComplete {
        case Success(sH) => {
          //sH.syms.foreach(println)
          assert(sH.file.getAbsolutePath.equals(path))

        }
        case Failure(t) => {
          println("An error has occured: " + t.getMessage)
          fail()
        }
      }

    }

    ignore("should send a InspectTypeAtPoint w/ from eq to") {
      val file = new File(path)
      val rangeFrom = 314
      val rangeTo = 314
      val typeInspectInfo = client.inspectTypeAtPoint(file, new OffsetRange(rangeFrom, rangeTo))

      typeInspectInfo.onComplete {
        case Success(tII) => {
          //          println("\n")
          //          println("infoType: " + tII.infoType + "\n")
          //          println("interfaces: " + "\n")
          //          tII.interfaces.foreach { println }
          //          println("\n")
          assert(true)

        }
        case Failure(t) => {
          fail()
        }

      }
    }

    ignore("should send a SymbolInfoReq") {
      val file = new File(path)
      val point = 324
      //val point = 398 // Points to a function call
      val symbolInfo = client.symbolAtPoint(file, point)

      symbolInfo.onComplete {
        case Success(sI) => {
          println(sI)
          if (sI.declPos.isDefined) {
            val srcPos = sI.declPos.get
            if (srcPos.isInstanceOf[OffsetSourcePosition]) {
              assert(srcPos.asInstanceOf[OffsetSourcePosition].offset == 627)
            } else {
              fail("Unexpected SourcePosition")
            }

          } else
            fail()

        }
        case Failure(t) => {
          fail(t)
        }

      }
    }

    ignore("should send a TypeAtPointReq with range input from eq to (BasicTypeInfo expected)") {
      val file = new File(path)
      val start = 324
      val typeInfo = client.typeAtPoint(file, new OffsetRange(start, start))

      typeInfo.onComplete {
        case Success(tI) => {
          assert(tI.isInstanceOf[BasicTypeInfo])

          if (tI.isInstanceOf[BasicTypeInfo]) {
            // println(tI)
          } else {
            fail("Unknown TypeInfo type.")
          }
        }
        case Failure(t) => {
          fail(t)
        }

      }
    }
    ignore("should send a TypeAtPointReq with range input from eq to (ArrowTypeInfo expected)") {
      val file = new File(path)
      val start = 398
      val typeInfo = client.typeAtPoint(file, new OffsetRange(start, start))

      typeInfo.onComplete {
        case Success(tI) => {

          assert(tI.isInstanceOf[ArrowTypeInfo])

          if (tI.isInstanceOf[ArrowTypeInfo]) {
            //println(tI)
          } else {
            fail("Unknown TypeInfo type.")
          }
        }
        case Failure(t) => {
          fail(t)
        }

      }
    }

    ignore("should send a UsesOfSymbolAtPointReq") {
      val file = new File(path)
      val point = 314
      val usesOfSymbol = client.usesOfSymAtPoint(file, point)

      usesOfSymbol.onComplete {
        case Success(uses) => {
          assert(uses.positions(0).offset == 314)
          // uses.positions.foreach(println)
          // Each element: ERangePosition(file: String, offset: Int, start: Int, end: Int)
        }
        case Failure(t) => {
          fail(t)
        }

      }
    }

    it("should send a InspectPackageByPath (path: org.codeprose.rational)") {
      val path = "org.codeprose.rational"
      val packageInfo = client.inspectPackageByPath(path)

      println("\nInspectPackageByPath\n")

      packageInfo.onComplete {

        case Success(pI) => {
          assert(pI.fullName == "org.codeprose.rational")
          println(pI.fullName)
          pI.members.foreach(x => println("\n" + x.toString + "\n"))
        }
        case Failure(t) => {
          fail(t)
        }
      }

    }

    it("should send a ImplicitInfoReq") {

      val file = new File(path)
      val rangeFrom = 0
      val rangeTo = 589
      val implicitInfo = client.implicitInfoReq(file, new OffsetRange(rangeFrom, rangeTo))

      implicitInfo.onComplete {

        case Success(iI) => {
          println("Implicit conversions infos: \n")
          iI.infos.foreach { x => println("\n" + x + "\n") }

        }
        case Failure(t) => {
          fail(t)
        }
      }

    }

    it("should send a InspectTypeByIdReq") {
      val typeId = 1
      val implicitInfo = client.inspectTypeById(typeId)

      implicitInfo.onComplete {

        case Success(tII) => {
          println("TypeInspectInfo:\n")
          println(tII.toString)

        }
        case Failure(t) => {
          fail(t)
        }
      }

    }

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
  }

}