package org.ensime.client.util

import org.scalatest.path.FunSpec

class TestWireResponseExtractorSwank extends FunSpec {
  //
  //  val responseExtractor = new WireResponseExtractorSwank()
  //
  //  describe("A WireResponseExtractorSwank") {
  //
  //    it("should extract a ConnectionInfo object without pid") {
  //      val responseMessage = """(:return (:ok (:pid nil :implementation (:name "ENSIME") :version "0.8.14")) 1)"""
  //      val connectionInfo = responseExtractor.getConnectionInfo(responseMessage)
  //      if (connectionInfo.pid.isEmpty) {
  //        assert(connectionInfo.implementation.name.equals("ENSIME") && connectionInfo.version.equals("0.8.14"))
  //      } else fail()
  //    }
  //
  //    it("should extract a ConnectionInfo object with pid") {
  //
  //      val responseMessage = """(:return (:ok (:pid 15 :implementation (:name "ENSIME") :version "0.8.14")) 1)"""
  //      val connectionInfo = responseExtractor.getConnectionInfo(responseMessage)
  //      if (!connectionInfo.pid.isEmpty) {
  //        assert(connectionInfo.pid.get == 15 && connectionInfo.implementation.name.equals("ENSIME") && connectionInfo.version.equals("0.8.14"))
  //      } else fail()
  //    }
  //
  //    it("should extract a SmybolDesignations object") {
  //
  //      val responseMessage = """(:return (:ok (:file "/path/to/file.scala" :syms ((package 8 12) (class 87 94) (functionCall 100 108) (functionCall 240 249) (class 250 274) (val 290 298) (class 305 313) (val 406 414) (class 421 429) (val 448 456) (val 519 528) (class 535 543) (val 634 642) (class 649 657) (val 677 685) (val 921 929) (class 936 944) (val 961 969) (val 1032 1040) (class 1047 1055) (val 1074 1082)))) 3)"""
  //      val symbolDesignations = responseExtractor.getSymbolDesignations(responseMessage)
  //      assert(symbolDesignations.file.getAbsolutePath.equals("/path/to/file.scala"))
  //
  //    }
  //
  //    ignore("should extract a TypeCheckFileReq ") {
  //      val responseMessage = """ (:return (:ok t) 4)"""
  //      val typeCheckFile = responseExtractor.getTypeCheckFile(responseMessage)
  //      assert(typeCheckFile)
  //    }
  //
  //    it("should extract a BasicTypeInfo object (TypeAtPointReq)") {
  //      val responseMessage = """(:return (:ok (:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8)) 1)"""
  //      val typeAtPoint = responseExtractor.getTypeAtPoint(responseMessage)
  //      if (typeAtPoint.isDefined) {
  //        assert(
  //          typeAtPoint.get.name === "type1" &&
  //            typeAtPoint.get.fullName === "FOO.type1" &&
  //            typeAtPoint.get.typeId == 7
  //        )
  //        /// TODO include more asserts...          
  //      } else fail()
  //
  //    }
  //
  //    it("should extract a BasicTypeInfo object (ArrowTypeInfo)") {
  //      // original message from server: """(:return (:ok (:arrow-type t :name "(that: Long)rational.Rational" :type-id 28 :result-type (:arrow-type nil :name "Rational" :type-id 8 :decl-as class :full-name "rational.Rational" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("that" (:arrow-type nil :name "Long" :type-id 13 :decl-as class :full-name "scala.Long" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil)))) 129)"""
  //      val responseMessage = s"""(:return (:ok (:arrow-type t :name "(x: Any)Unit" :type-id 7 :result-type (:arrow-type nil :name "Unit" :type-id 5 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("x" (:arrow-type nil :name "Any" :type-id 6 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil)))) 8)"""
  //
  //      val typeAtPoint = responseExtractor.getTypeAtPoint(responseMessage)
  //
  //      if (typeAtPoint.isDefined) {
  //        assert(
  //          typeAtPoint.get.name === "type1" &&
  //            typeAtPoint.get.fullName === "FOO.type1" &&
  //            typeAtPoint.get.typeId == 7
  //        )
  //        /// TODO include more asserts...          
  //      }
  //      //else fail() 
  //    }
  //
  //    ignore("should extract a TypeInspectInfo object (InspectTypeAtPointReq)") {
  //      //      val typeInfoStr = """(:arrow-type nil :name "type1" :type-id 7 :decl-as method :full-name "FOO.type1" :type-args nil :members nil :pos nil :outer-type-id 8)"""
  //      //      val typeInspectStr = s"""(:type $typeInfoStr :companion-id 1 :interfaces ((:type """ + typeInfoStr + """ :via-view "DEF")) :info-type typeInspect)"""
  //      //      val responseMessge = s"""(:return (:ok $typeInspectStr) 1)"""
  //
  //      // original message returned from ensime server
  //      val responseMessge = """(:return (:ok (:type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos (:type offset :file "/home/gus/scala/sbtlearn/rational/.ensime_cache/dep-src/source-jars/scala/Unit.scala" :offset 1014) :outer-type-id nil) :companion-id 47 :interfaces ((:type (:arrow-type nil :name "StringFormat" :type-id 8 :decl-as class :full-name "scala.Predef$$StringFormat" :type-args ((:arrow-type nil :name "A" :type-id 9 :decl-as nil :full-name "scala.A" :type-args nil :members nil :pos nil :outer-type-id nil)) :members ((:info-type named :name "formatted" :type (:arrow-type t :name "(fmtstr: String)String" :type-id 6 :result-type (:arrow-type nil :name "String" :type-id 5 :decl-as class :full-name "java.lang.String" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("fmtstr" (:arrow-type nil :name "String" :type-id 5 :decl-as class :full-name "java.lang.String" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(fmtstr: String): String" :decl-as method)) :pos nil :outer-type-id 7) :via-view "StringFormat") (:type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members ((:info-type named :name "<init>" :type (:arrow-type t :name "()Unit" :type-id 10 :result-type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params nil :is-implicit nil))) :pos (:type empty) :signature-string "(): Unit" :decl-as method) (:info-type named :name "getClass" :type (:arrow-type t :name "()Class[Unit]" :type-id 11 :result-type (:arrow-type nil :name "Class" :type-id 12 :decl-as class :full-name "java.lang.Class" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil) :param-sections ((:params nil :is-implicit nil))) :pos (:type empty) :signature-string "(): Class[Unit]" :decl-as method)) :pos (:type offset :file "/home/gus/scala/sbtlearn/rational/.ensime_cache/dep-src/source-jars/scala/Unit.scala" :offset 1014) :outer-type-id nil) :via-view nil) (:type (:arrow-type nil :name "any2stringadd" :type-id 16 :decl-as class :full-name "scala.Predef$$any2stringadd" :type-args ((:arrow-type nil :name "A" :type-id 17 :decl-as nil :full-name "scala.A" :type-args nil :members nil :pos nil :outer-type-id nil)) :members ((:info-type named :name "+" :type (:arrow-type t :name "(other: String)String" :type-id 13 :result-type (:arrow-type nil :name "String" :type-id 5 :decl-as class :full-name "java.lang.String" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("other" (:arrow-type nil :name "String" :type-id 5 :decl-as class :full-name "java.lang.String" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(other: String): String" :decl-as method) (:info-type named :name "<init>" :type (:arrow-type t :name "(self: Unit)any2stringadd[Unit]" :type-id 14 :result-type (:arrow-type nil :name "any2stringadd" :type-id 15 :decl-as class :full-name "scala.Predef$$any2stringadd" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id 7) :param-sections ((:params (("self" (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(self: A): any2stringadd[A]" :decl-as method)) :pos nil :outer-type-id 7) :via-view "any2stringadd") (:type (:arrow-type nil :name "ArrowAssoc" :type-id 24 :decl-as class :full-name "scala.Predef$$ArrowAssoc" :type-args ((:arrow-type nil :name "A" :type-id 25 :decl-as nil :full-name "scala.A" :type-args nil :members nil :pos nil :outer-type-id nil)) :members ((:info-type named :name "->" :type (:arrow-type t :name "[B](y: B)(Unit, B)" :type-id 19 :result-type (:arrow-type nil :name "Tuple2" :type-id 20 :decl-as class :full-name "scala.Tuple2" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) (:arrow-type nil :name "B" :type-id 18 :decl-as nil :full-name "scala.B" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("y" (:arrow-type nil :name "B" :type-id 18 :decl-as nil :full-name "scala.B" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "[B](y: B): (A, B)" :decl-as method) (:info-type named :name "→" :type (:arrow-type t :name "[B](y: B)(Unit, B)" :type-id 22 :result-type (:arrow-type nil :name "Tuple2" :type-id 23 :decl-as class :full-name "scala.Tuple2" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) (:arrow-type nil :name "B" :type-id 21 :decl-as nil :full-name "scala.B" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("y" (:arrow-type nil :name "B" :type-id 21 :decl-as nil :full-name "scala.B" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "[B](y: B): (A, B)" :decl-as method)) :pos nil :outer-type-id 7) :via-view "ArrowAssoc") (:type (:arrow-type nil :name "Ensuring" :type-id 34 :decl-as class :full-name "scala.Predef$$Ensuring" :type-args ((:arrow-type nil :name "A" :type-id 35 :decl-as nil :full-name "scala.A" :type-args nil :members nil :pos nil :outer-type-id nil)) :members ((:info-type named :name "ensuring" :type (:arrow-type t :name "(cond: Boolean)Unit" :type-id 27 :result-type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("cond" (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(cond: Boolean): A" :decl-as method) (:info-type named :name "ensuring" :type (:arrow-type t :name "(cond: Boolean, msg: => Any)Unit" :type-id 30 :result-type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("cond" (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil)) ("msg" (:arrow-type nil :name "<byname>" :type-id 28 :decl-as class :full-name "scala.<byname>" :type-args ((:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(cond: Boolean,msg: => Any): A" :decl-as method) (:info-type named :name "ensuring" :type (:arrow-type t :name "(cond: Unit => Boolean)Unit" :type-id 32 :result-type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("cond" (:arrow-type nil :name "Function1" :type-id 31 :decl-as trait :full-name "scala.Function1" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(cond: A => Boolean): A" :decl-as method) (:info-type named :name "ensuring" :type (:arrow-type t :name "(cond: Unit => Boolean, msg: => Any)Unit" :type-id 33 :result-type (:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("cond" (:arrow-type nil :name "Function1" :type-id 31 :decl-as trait :full-name "scala.Function1" :type-args ((:arrow-type nil :name "Unit" :type-id 2 :decl-as class :full-name "scala.Unit" :type-args nil :members nil :pos nil :outer-type-id nil) (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil)) ("msg" (:arrow-type nil :name "<byname>" :type-id 28 :decl-as class :full-name "scala.<byname>" :type-args ((:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil)) :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(cond: A => Boolean,msg: => Any): A" :decl-as method)) :pos nil :outer-type-id 7) :via-view "Ensuring") (:type (:arrow-type nil :name "AnyVal" :type-id 36 :decl-as class :full-name "scala.AnyVal" :type-args nil :members nil :pos nil :outer-type-id nil) :via-view nil) (:type (:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members ((:info-type named :name "!=" :type (:arrow-type t :name "(x$1: Any)Boolean" :type-id 37 :result-type (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("x$1" (:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(x$1: Any): Boolean" :decl-as method) (:info-type named :name "##" :type (:arrow-type t :name "()Int" :type-id 38 :result-type (:arrow-type nil :name "Int" :type-id 39 :decl-as class :full-name "scala.Int" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params nil :is-implicit nil))) :pos nil :signature-string "(): Int" :decl-as method) (:info-type named :name "==" :type (:arrow-type t :name "(x$1: Any)Boolean" :type-id 40 :result-type (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("x$1" (:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(x$1: Any): Boolean" :decl-as method) (:info-type named :name "asInstanceOf" :type (:arrow-type t :name "[T0]=> T0" :type-id 41 :result-type (:arrow-type nil :name "T0" :type-id 42 :decl-as nil :full-name "scala.T0" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections nil) :pos nil :signature-string "[T0]: T0" :decl-as method) (:info-type named :name "equals" :type (:arrow-type t :name "(x$1: Any)Boolean" :type-id 43 :result-type (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params (("x$1" (:arrow-type nil :name "Any" :type-id 29 :decl-as class :full-name "scala.Any" :type-args nil :members nil :pos nil :outer-type-id nil))) :is-implicit nil))) :pos nil :signature-string "(x$1: Any): Boolean" :decl-as method) (:info-type named :name "hashCode" :type (:arrow-type t :name "()Int" :type-id 38 :result-type (:arrow-type nil :name "Int" :type-id 39 :decl-as class :full-name "scala.Int" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params nil :is-implicit nil))) :pos nil :signature-string "(): Int" :decl-as method) (:info-type named :name "isInstanceOf" :type (:arrow-type t :name "[T0]=> Boolean" :type-id 44 :result-type (:arrow-type nil :name "Boolean" :type-id 26 :decl-as class :full-name "scala.Boolean" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections nil) :pos nil :signature-string "[T0]: Boolean" :decl-as method) (:info-type named :name "toString" :type (:arrow-type t :name "()String" :type-id 45 :result-type (:arrow-type nil :name "String" :type-id 46 :decl-as class :full-name "java.lang.String" :type-args nil :members nil :pos nil :outer-type-id nil) :param-sections ((:params nil :is-implicit nil))) :pos nil :signature-string "(): String" :decl-as method)) :pos nil :outer-type-id nil) :via-view nil)) :info-type typeInspect)) 5)"""
  //
  //      val typeInspectInfo = responseExtractor.getInspectTypeAtPoint(responseMessge)
  //      if (typeInspectInfo.isDefined) {
  //        assert(typeInspectInfo.get.companionId.get == 1)
  //        /// Todo add more conditions.)
  //      } else fail()
  //
  //    }
  //
  //    ignore("should extract a PackageInfo object (InspectPackageByPathReq)") {
  //      //val responseMessge = """(:return (:ok (:info-type package :name "name" :full-name "fullName" :members nil)) 1)"""
  //
  //      // Original message returned by the ensime server...
  //      val responseMessge = """(:return (:ok (:info-type package :name "rational" :full-name "rational" :members ((:info-type type :arrow-type nil :name "Rational" :type-id 3 :decl-as class :full-name "rational.Rational" :type-args nil :members nil :pos (:type offset :file "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala" :offset 309) :outer-type-id nil) (:info-type type :arrow-type nil :name "Rational$" :type-id 4 :decl-as object :full-name "rational.Rational$" :type-args nil :members nil :pos (:type offset :file "/home/gus/scala/sbtlearn/rational/src/main/scala/de/uni/tuebingen/rational/rational.scala" :offset 25) :outer-type-id nil)))) 5)"""
  //      val packageInspectInfo = responseExtractor.getInspectPackageByPath(responseMessge)
  //      if (packageInspectInfo.isDefined) {
  //        assert(packageInspectInfo.get.name === "name" &&
  //          packageInspectInfo.get.fullName === "fullName")
  //      } else fail()
  //    }
  //
  //  }

}