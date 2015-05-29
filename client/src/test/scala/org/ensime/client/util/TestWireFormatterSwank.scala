package org.ensime.client.util

import org.scalatest.FunSpec
import java.util.concurrent.atomic.AtomicInteger
import org.ensime.server.protocol._
import org.ensime.model._
import java.io.File

class TestWireFormatterSwank extends FunSpec {

  val wireFormatterSwank = new WireFormatterSwank()
  val currentId = new AtomicInteger(1)

  describe("The WireFormatterSwank") {

    it("should generate a correct ConnectionInfoReq") {
      val id = currentId.getAndIncrement
      val request = ConnectionInfoReq
      val wfString = wireFormatterSwank.toWireFormat(request, id)
      val cmpString = s"""(:swank-rpc (swank:connection-info) $id)"""
      assert(wfString.equals(cmpString))
    }

    it("should generate a correct SymbolDesignationsReq") {
      val id = currentId.getAndIncrement
      val path = "/path/to/file.scala"
      val file = new File(path)
      val start = 0
      val end = 42

      val requestedTypes = List(ObjectSymbol, ClassSymbol, TraitSymbol,
        PackageSymbol, ConstructorSymbol, ImportedNameSymbol, TypeParamSymbol,
        ParamSymbol, VarFieldSymbol, ValFieldSymbol, OperatorFieldSymbol, VarSymbol, ValSymbol, FunctionCallSymbol)

      val request = SymbolDesignationsReq(file, start, end, requestedTypes);
      val wfString = wireFormatterSwank.toWireFormat(request, id);
      val cmpString = s"""(:swank-rpc (swank:symbol-designations "/path/to/file.scala" $start $end (object class trait package constructor importedName typeParam param varField valField operator var val functionCall)) $id)"""
      assert(wfString.equals(cmpString))
    }

    it("should generate a correct ShutdownServerReq") {
      val id = currentId.getAndIncrement
      val request = ShutdownServerReq;
      val wfString = wireFormatterSwank.toWireFormat(request, id)
      val cmpString = s"""(:swank-rpc (swank:shutdown-server) $id)"""
      assert(wfString.equals(cmpString))
    }

    it("should generate a correct TypeAtPointReq with range input from neq to") {
      val id = currentId.getAndIncrement
      val file = new File("/path/to/file.scala")
      val rangeFrom = 149
      val rangeTo = 150
      val range = new OffsetRange(rangeFrom, rangeTo)

      val request = TypeAtPointReq(file, range)
      val wfString = wireFormatterSwank.toWireFormat(request, id);

      val cmpString = s"""(:swank-rpc (swank:type-at-point "$file" ($rangeFrom $rangeTo)) $id)"""

      assert(wfString.equals(cmpString))

    }

    it("should generate a correct TypeAtPointReq with range input from eq to") {
      val id = currentId.getAndIncrement
      val file = new File("/path/to/file.scala")
      val rangeFrom = 150
      val rangeTo = 150
      val range = new OffsetRange(rangeFrom, rangeTo)

      val request = TypeAtPointReq(file, range)
      val wfString = wireFormatterSwank.toWireFormat(request, id);

      val cmpString = s"""(:swank-rpc (swank:type-at-point "$file" $rangeFrom) $id)"""

      assert(wfString.equals(cmpString))

    }

    ignore("should generate a correct TypecheckFileReq") {
      val id = currentId.getAndIncrement

      val files = List(new File("/path/to/file.scala"))
      val request = TypecheckFilesReq(files)
      val wfString = wireFormatterSwank.toWireFormat(request, id)
      val fStr = files.head
      val cmpString = s"""(:swank-rpc (swank:typecheck-file ("$fStr")) $id)"""
      assert(wfString.equals(cmpString))

    }

  }

}