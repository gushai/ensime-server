package org.ensime.client.util

import org.scalatest.path.FunSpec

class TestWireResponseExtractorSwank extends FunSpec {

  val responseExtractor = new WireResponseExtractorSwank()

  describe("A WireResponseExtractorSwank") {

    it("should extract a ConnectionInfo object without pid") {
      val responseMessage = """(:return (:ok (:pid nil :implementation (:name "ENSIME") :version "0.8.14")) 1)"""
      val connectionInfo = responseExtractor.getConnectionInfo(responseMessage)
      if (connectionInfo.pid.isEmpty) {
        assert(connectionInfo.implementation.name.equals("ENSIME") && connectionInfo.version.equals("0.8.14"))
      } else fail()
    }

    it("should extract a ConnectionInfo object with pid") {

      val responseMessage = """(:return (:ok (:pid 15 :implementation (:name "ENSIME") :version "0.8.14")) 1)"""
      val connectionInfo = responseExtractor.getConnectionInfo(responseMessage)
      if (!connectionInfo.pid.isEmpty) {
        assert(connectionInfo.pid.get == 15 && connectionInfo.implementation.name.equals("ENSIME") && connectionInfo.version.equals("0.8.14"))
      } else fail()
    }

    it("should extract a SmybolDesignations object") {

      val responseMessage = """(:return (:ok (:file "/path/to/file.scala" :syms ((package 8 12) (class 87 94) (functionCall 100 108) (functionCall 240 249) (class 250 274) (val 290 298) (class 305 313) (val 406 414) (class 421 429) (val 448 456) (val 519 528) (class 535 543) (val 634 642) (class 649 657) (val 677 685) (val 921 929) (class 936 944) (val 961 969) (val 1032 1040) (class 1047 1055) (val 1074 1082)))) 3)"""
      val symbolDesignations = responseExtractor.getSymbolDesignations(responseMessage)
      assert(symbolDesignations.file.getAbsolutePath.equals("/path/to/file.scala"))

    }

    ignore("should extract a TypeCheckFileReq") {
      val responseMessage = """ (:return (:ok t) 4)"""
      val typeCheckFile = responseExtractor.getTypeCheckFile(responseMessage)
      assert(typeCheckFile)
    }

    it("should extract a TypeAtPointReq") {
      val responseMessage = """(:return (:ok (:arrow-type nil :name "Rational" :type-id 1 :decl-as class :full-name "rational.Rational" :type-args nil :members nil :pos (:type offset :file "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala" :offset 314) :outer-type-id nil)) 1)"""
      // val responseMessage = """(:return (:ok (:name "String" :type-id 1188 :full-name "java.lang.String" :decl-as class)) 42)"""
      // val responseMessage = """(:return (:ok (:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8)) 1)"""
      val typeAtPoint = responseExtractor.getTypeAtPoint(responseMessage)
      if (typeAtPoint.isDefined) {
        assert(
          typeAtPoint.get.name === "Rational" &&
            typeAtPoint.get.fullName === "rational.Rational" &&
            typeAtPoint.get.typeId == 1
        )
        /// TODO include more asserts...          
      } else fail()

    }

  }

}